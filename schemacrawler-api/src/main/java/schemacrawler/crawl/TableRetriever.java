/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.IndexColumnSortSequence;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import sf.util.Utility;

/**
 * A retriever uses database metadata to get the details about the
 * database tables.
 * 
 * @author Sualeh Fatehi
 */
final class TableRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(TableRetriever.class
    .getName());

  /**
   * Converts an array of table types to an array of their corresponding
   * string values.
   * 
   * @param tableTypes
   *        Array of table types
   * @return Array of string table types
   */
  private static String[] toStrings(final Collection<TableType> tableTypes)
  {
    if (tableTypes == null || tableTypes.isEmpty())
    {
      return new String[0];
    }

    final List<String> tableTypeStrings = new ArrayList<>(tableTypes.size());
    for (final TableType tableType: tableTypes)
    {
      if (tableType != null)
      {
        tableTypeStrings.add(tableType.toString().toUpperCase(Locale.ENGLISH));
      }
    }
    return tableTypeStrings.toArray(new String[tableTypeStrings.size()]);
  }

  TableRetriever(final RetrieverConnection retrieverConnection,
                 final MutableDatabase database)
    throws SQLException
  {
    super(retrieverConnection, database);
  }

  void retrieveColumns(final MutableTable table,
                       final InclusionRule columnInclusionRule)
    throws SQLException
  {
    final InclusionRuleFilter<Column> columnFilter = new InclusionRuleFilter<>(columnInclusionRule,
                                                                               true);
    if (columnFilter.isExcludeAll())
    {
      return;
    }

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getColumns(unquotedName(table.getSchema().getCatalogName()),
                  unquotedName(table.getSchema().getName()),
                  unquotedName(table.getName()),
                  null));)
    {
      while (results.next())
      {
        // Get the "COLUMN_DEF" value first as it the Oracle drivers
        // don't handle it properly otherwise.
        // http://issues.apache.org/jira/browse/DDLUTILS-29?page=all
        final String defaultValue = results.getString("COLUMN_DEF");
        //
        final String columnCatalogName = quotedName(results
          .getString("TABLE_CAT"));
        final String schemaName = quotedName(results.getString("TABLE_SCHEM"));
        final String tableName = quotedName(results.getString("TABLE_NAME"));
        final String columnName = quotedName(results.getString("COLUMN_NAME"));
        LOGGER.log(Level.FINER, String.format("Retrieving column: %s.%s",
                                              tableName,
                                              columnName));

        MutableColumn column;

        column = lookupOrCreateColumn(table, columnName, false/* add */);
        // Note: If the table name contains an underscore character,
        // this is a wildcard character. We need to do another check to
        // see if the table name matches.
        if (columnFilter.include(column) && table.getName().equals(tableName)
            && belongsToSchema(table, columnCatalogName, schemaName))
        {
          column = lookupOrCreateColumn(table, columnName, true/* add */);

          final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
          final int dataType = results.getInt("DATA_TYPE", 0);
          final String typeName = results.getString("TYPE_NAME");
          final int size = results.getInt("COLUMN_SIZE", 0);
          final int decimalDigits = results.getInt("DECIMAL_DIGITS", 0);
          final boolean isNullable = results
            .getInt("NULLABLE", DatabaseMetaData.columnNullableUnknown) == DatabaseMetaData.columnNullable;
          final String remarks = results.getString("REMARKS");

          column.setOrdinalPosition(ordinalPosition);
          column.setColumnDataType(lookupOrCreateColumnDataType(table
            .getSchema(), dataType, typeName));
          column.setSize(size);
          column.setDecimalDigits(decimalDigits);
          column.setRemarks(remarks);
          column.setNullable(isNullable);
          if (defaultValue != null)
          {
            column.setDefaultValue(defaultValue);
          }

          column.addAttributes(results.getAttributes());

          table.addColumn(column);
        }
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve columns for table "
                                              + table,
                                          e);
    }

  }

  void retrieveForeignKeys(final MutableTable table)
    throws SQLException
  {

    final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<>();
    MetadataResultSet results;

    final DatabaseMetaData metaData = getMetaData();
    try
    {
      results = new MetadataResultSet(metaData.getImportedKeys(unquotedName(table
                                                                 .getSchema()
                                                                 .getCatalogName()),
                                                               unquotedName(table
                                                                 .getSchema()
                                                                 .getName()),
                                                               unquotedName(table
                                                                 .getName())));
      createForeignKeys(results, foreignKeys);

      results = new MetadataResultSet(metaData.getExportedKeys(unquotedName(table
                                                                 .getSchema()
                                                                 .getCatalogName()),
                                                               unquotedName(table
                                                                 .getSchema()
                                                                 .getName()),
                                                               unquotedName(table
                                                                 .getName())));
      createForeignKeys(results, foreignKeys);
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve forign keys for table "
                                              + table,
                                          e);
    }
  }

  void retrieveIndices(final MutableTable table, final boolean unique)
    throws SQLException
  {

    SQLException sqlEx = null;
    try
    {
      retrieveIndices1(table, unique);
    }
    catch (final SQLException e)
    {
      sqlEx = e;
    }

    if (sqlEx != null)
    {
      try
      {
        sqlEx = null;
        retrieveIndices2(table, unique);
      }
      catch (final SQLException e)
      {
        sqlEx = e;
      }
    }

    if (sqlEx != null)
    {
      throw sqlEx;
    }
  }

  void retrievePrimaryKey(final MutableTable table)
    throws SQLException
  {
    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getPrimaryKeys(unquotedName(table.getSchema().getCatalogName()),
                      unquotedName(table.getSchema().getName()),
                      unquotedName(table.getName())));)
    {

      MutablePrimaryKey primaryKey;
      while (results.next())
      {
        // "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME"
        final String columnName = quotedName(results.getString("COLUMN_NAME"));
        final String primaryKeyName = quotedName(results.getString("PK_NAME"));
        final int keySequence = Integer.parseInt(results.getString("KEY_SEQ"));

        primaryKey = table.getPrimaryKey();
        if (primaryKey == null)
        {
          primaryKey = new MutablePrimaryKey(table, primaryKeyName);
        }

        // Register primary key information
        final MutableColumn column = table.getColumn(columnName);
        if (column != null)
        {
          column.setPartOfPrimaryKey(true);
          final MutableIndexColumn indexColumn = new MutableIndexColumn(primaryKey,
                                                                        column);
          indexColumn.setSortSequence(IndexColumnSortSequence.ascending);
          indexColumn.setIndexOrdinalPosition(keySequence);
          //
          primaryKey.addColumn(indexColumn);
        }

        table.setPrimaryKey(primaryKey);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve primary keys for table "
                                              + table,
                                          e);
    }

  }

  void retrieveTables(final String catalogName,
                      final String schemaName,
                      final String tableNamePattern,
                      final Collection<TableType> tableTypes,
                      final InclusionRule tableInclusionRule)
    throws SQLException
  {
    final InclusionRuleFilter<Table> tableFilter = new InclusionRuleFilter<>(tableInclusionRule,
                                                                             false);
    if (tableFilter.isExcludeAll())
    {
      return;
    }

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getTables(unquotedName(catalogName),
                 unquotedName(schemaName),
                 tableNamePattern,
                 toStrings(tableTypes)));)
    {
      while (results.next())
      {
        // "TABLE_CAT", "TABLE_SCHEM"
        final String tableName = quotedName(results.getString("TABLE_NAME"));
        LOGGER.log(Level.FINER, String.format("Retrieving table: %s.%s",
                                              schemaName,
                                              tableName));
        final TableType tableType = results.getEnum("TABLE_TYPE",
                                                    TableType.unknown);
        final String remarks = results.getString("REMARKS");

        final SchemaReference schemaReference = new SchemaReference(catalogName,
                                                                    schemaName);
        final Schema schema = database.getSchema(schemaReference.getFullName());

        final MutableTable table;
        if (tableType == TableType.view)
        {
          table = new MutableView(schema, tableName);
        }
        else
        {
          table = new MutableTable(schema, tableName);
        }
        if (tableFilter.include(table))
        {
          table.setTableType(tableType);
          table.setRemarks(remarks);

          database.addTable(table);
        }
      }
    }
  }

  private void createForeignKeys(final MetadataResultSet results,
                                 final NamedObjectList<MutableForeignKey> foreignKeys)
    throws SQLException
  {
    try
    {
      while (results.next())
      {
        String foreignKeyName = quotedName(results.getString("FK_NAME"));
        LOGGER.log(Level.FINER, "Retrieving foreign key: " + foreignKeyName);

        final String pkTableCatalogName = quotedName(results
          .getString("PKTABLE_CAT"));
        final String pkTableSchemaName = quotedName(results
          .getString("PKTABLE_SCHEM"));
        final String pkTableName = quotedName(results.getString("PKTABLE_NAME"));
        final String pkColumnName = quotedName(results
          .getString("PKCOLUMN_NAME"));

        final String fkTableCatalogName = quotedName(results
          .getString("FKTABLE_CAT"));
        final String fkTableSchemaName = quotedName(results
          .getString("FKTABLE_SCHEM"));
        final String fkTableName = quotedName(results.getString("FKTABLE_NAME"));
        final String fkColumnName = quotedName(results
          .getString("FKCOLUMN_NAME"));

        final int keySequence = results.getInt("KEY_SEQ", 0);
        final int updateRule = results.getInt("UPDATE_RULE",
                                              ForeignKeyUpdateRule.unknown
                                                .getId());
        final int deleteRule = results.getInt("DELETE_RULE",
                                              ForeignKeyUpdateRule.unknown
                                                .getId());
        final int deferrability = results
          .getInt("DEFERRABILITY", ForeignKeyDeferrability.unknown.getId());

        final Column pkColumn = lookupOrCreateColumn(pkTableCatalogName,
                                                     pkTableSchemaName,
                                                     pkTableName,
                                                     pkColumnName);
        final Column fkColumn = lookupOrCreateColumn(fkTableCatalogName,
                                                     fkTableSchemaName,
                                                     fkTableName,
                                                     fkColumnName);

        // Make a direct connection between the two columns
        if (pkColumn != null && fkColumn != null)
        {
          if (Utility.isBlank(foreignKeyName))
          {
            foreignKeyName = String.format("SC_%s_%s",
                                           Integer.toHexString(pkColumn
                                             .getFullName().hashCode())
                                             .toUpperCase(),
                                           Integer.toHexString(fkColumn
                                             .getFullName().hashCode())
                                             .toUpperCase());
          }

          MutableForeignKey foreignKey = foreignKeys.lookup(foreignKeyName);
          if (foreignKey == null)
          {
            foreignKey = new MutableForeignKey(foreignKeyName);
            foreignKeys.add(foreignKey);
          }

          foreignKey.addColumnReference(keySequence, pkColumn, fkColumn);
          foreignKey.setUpdateRule(ForeignKeyUpdateRule.valueOf(updateRule));
          foreignKey.setDeleteRule(ForeignKeyUpdateRule.valueOf(deleteRule));
          foreignKey.setDeferrability(ForeignKeyDeferrability
            .valueOf(deferrability));
          foreignKey.addAttributes(results.getAttributes());

          if (fkColumn instanceof MutableColumn)
          {
            ((MutableColumn) fkColumn).setReferencedColumn(pkColumn);
            ((MutableTable) fkColumn.getParent()).addForeignKey(foreignKey);
          }
          else if (fkColumn instanceof ColumnPartial)
          {
            ((ColumnPartial) fkColumn).setReferencedColumn(pkColumn);
            ((TablePartial) fkColumn.getParent()).addForeignKey(foreignKey);
          }

          if (pkColumn instanceof MutableColumn)
          {
            ((MutableTable) pkColumn.getParent()).addForeignKey(foreignKey);
          }
          else if (pkColumn instanceof ColumnPartial)
          {
            ((TablePartial) pkColumn.getParent()).addForeignKey(foreignKey);
          }
        }
      }
    }
    finally
    {
      results.close();
    }

  }

  private void createIndices(final MutableTable table,
                             final MetadataResultSet results)
    throws SQLException
  {
    try
    {
      while (results.next())
      {
        // "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME"
        String indexName = quotedName(results.getString("INDEX_NAME"));
        LOGGER.log(Level.FINER, String.format("Retrieving index: %s.%s",
                                              table.getFullName(),
                                              indexName));

        // Work-around PostgreSQL JDBC driver bugs by unquoting column
        // names first
        // #3480 -
        // http://www.postgresql.org/message-id/200707231358.l6NDwlWh026230@wwwmaster.postgresql.org
        // #6253 -
        // http://www.postgresql.org/message-id/201110121403.p9CE3fsx039675@wwwmaster.postgresql.org
        final String columnName = quotedName(unquotedName(results
          .getString("COLUMN_NAME")));
        if (Utility.isBlank(columnName))
        {
          continue;
        }

        final boolean uniqueIndex = !results.getBoolean("NON_UNIQUE");
        final int type = results.getInt("TYPE", IndexType.unknown.getId());
        final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
        final IndexColumnSortSequence sortSequence = IndexColumnSortSequence
          .valueOfFromCode(results.getString("ASC_OR_DESC"));
        final int cardinality = results.getInt("CARDINALITY", 0);
        final int pages = results.getInt("PAGES", 0);

        final MutableColumn column = table.getColumn(columnName);
        if (column != null)
        {
          if (Utility.isBlank(indexName))
          {
            indexName = String.format("SC_%s",
                                      Integer.toHexString(column.getFullName()
                                        .hashCode()).toUpperCase());
          }

          MutableIndex index = table.getIndex(indexName);
          if (index == null)
          {
            index = new MutableIndex(table, indexName);
            table.addIndex(index);
          }

          column.setPartOfUniqueIndex(uniqueIndex);
          final MutableIndexColumn indexColumn = new MutableIndexColumn(index,
                                                                        column);
          indexColumn.setIndexOrdinalPosition(ordinalPosition);
          indexColumn.setSortSequence(sortSequence);
          //
          index.addColumn(indexColumn);
          index.setUnique(uniqueIndex);
          index.setIndexType(IndexType.valueOf(type));
          index.setCardinality(cardinality);
          index.setPages(pages);
          index.addAttributes(results.getAttributes());
        }
      }
    }
    finally
    {
      results.close();
    }
  }

  private MutableColumn lookupOrCreateColumn(final MutableTable table,
                                             final String columnName,
                                             final boolean add)
  {
    MutableColumn column = null;
    if (table != null)
    {
      column = table.getColumn(columnName);
    }
    if (column == null)
    {
      column = new MutableColumn(table, columnName);
      if (add)
      {
        LOGGER.log(Level.FINER,
                   String.format("Adding column to table: %s",
                                 column.getFullName()));
        table.addColumn(column);
      }
    }
    return column;
  }

  /**
   * Looks up a column in the database. If the column and table are not
   * found, they are created, and added to the schema. This is prevent
   * foreign key relationships from having a null pointer.
   */
  private Column lookupOrCreateColumn(final String catalogName,
                                      final String schemaName,
                                      final String tableName,
                                      final String columnName)
  {
    final boolean supportsCatalogs = getRetrieverConnection()
      .isSupportsCatalogs();
    Column column = null;
    final Schema schema = new SchemaReference(supportsCatalogs? catalogName
                                                              : null,
                                              schemaName);
    if (schema != null)
    {
      Table table = database.getTable(new SchemaReference(catalogName,
                                                          schemaName),
                                      tableName);
      if (table != null)
      {
        column = table.getColumn(columnName);
      }

      if (column == null)
      {
        // Create the table and column, but do not add it to the schema
        table = new TablePartial(schema, tableName);
        column = new ColumnPartial(table, columnName);
        ((TablePartial) table).addColumn(column);

        LOGGER
          .log(Level.FINER,
               String
                 .format("Creating column reference for a column that is referenced by a foreign key: %s",
                         column.getFullName()));
      }
    }
    return column;
  }

  private void retrieveIndices1(final MutableTable table, final boolean unique)
    throws SQLException
  {

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getIndexInfo(unquotedName(table.getSchema().getCatalogName()),
                    unquotedName(table.getSchema().getName()),
                    unquotedName(table.getName()),
                    unique,
                    true/* approximate */));)
    {
      createIndices(table, results);
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve indices for table "
                                              + table,
                                          e);
    }

  }

  private void retrieveIndices2(final MutableTable table, final boolean unique)
    throws SQLException
  {

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getIndexInfo(null, null, table.getName(), unique, true/* approximate */));)
    {
      createIndices(table, results);
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve indices for table "
                                              + table,
                                          e);
    }

  }

}
