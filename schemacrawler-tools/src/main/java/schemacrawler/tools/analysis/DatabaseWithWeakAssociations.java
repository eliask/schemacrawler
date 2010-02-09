package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Table;
import sf.util.Inflection;
import sf.util.ObjectToString;
import sf.util.Utility;

public final class DatabaseWithWeakAssociations
  implements Database
{

  private static final long serialVersionUID = -3953296149824921463L;

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseWithWeakAssociations.class.getName());

  /**
   * Finds table prefixes. A prefix ends with "_".
   * 
   * @param tables
   *        Tables
   * @return Table name prefixes
   */
  private static Collection<String> findTableNamePrefixes(final List<Table> tablesList)
  {
    final SortedMap<String, Integer> prefixesMap = new TreeMap<String, Integer>();
    for (int i = 0; i < tablesList.size(); i++)
    {
      for (int j = i + 1; j < tablesList.size(); j++)
      {
        final String table1 = tablesList.get(i).getName();
        final String table2 = tablesList.get(j).getName();
        final String commonPrefix = Utility.commonPrefix(table1, table2);
        if (!Utility.isBlank(commonPrefix) && commonPrefix.endsWith("_"))
        {
          final List<String> splitCommonPrefixes = new ArrayList<String>();
          final String[] splitPrefix = commonPrefix.split("_");
          if (splitPrefix != null && splitPrefix.length > 0)
          {
            for (int k = 0; k < splitPrefix.length; k++)
            {
              final StringBuilder buffer = new StringBuilder();
              for (int l = 0; l < k; l++)
              {
                buffer.append(splitPrefix[l]).append("_");
              }
              if (buffer.length() > 0)
              {
                splitCommonPrefixes.add(buffer.toString());
              }
            }
          }
          splitCommonPrefixes.add(commonPrefix);

          for (final String splitCommonPrefix: splitCommonPrefixes)
          {
            final int prevCount;
            if (prefixesMap.containsKey(splitCommonPrefix))
            {
              prevCount = prefixesMap.get(splitCommonPrefix);
            }
            else
            {
              prevCount = 0;
            }
            prefixesMap.put(splitCommonPrefix, prevCount + 1);
          }
        }
      }
    }

    // Make sure we have the smallest prefixes
    final List<String> keySet = new ArrayList<String>(prefixesMap.keySet());
    Collections.sort(keySet, new Comparator<String>()
    {

      public int compare(final String o1, final String o2)
      {
        int comparison = 0;
        comparison = o2.length() - o1.length();
        if (comparison == 0)
        {
          comparison = o2.compareTo(o1);
        }
        return comparison;
      }

    });
    for (int i = 0; i < keySet.size(); i++)
    {
      for (int j = i + 1; j < keySet.size(); j++)
      {
        final String longPrefix = keySet.get(i);
        if (longPrefix.startsWith(keySet.get(j)))
        {
          prefixesMap.remove(longPrefix);
          break;
        }
      }
    }

    // Sort prefixes by the number of tables using them, in descending
    // order
    final List<Map.Entry<String, Integer>> prefixesList = new ArrayList<Map.Entry<String, Integer>>(prefixesMap
      .entrySet());
    Collections.sort(prefixesList, new Comparator<Map.Entry<String, Integer>>()
    {

      public int compare(final Entry<String, Integer> o1,
                         final Entry<String, Integer> o2)
      {
        return o1.getValue().compareTo(o2.getValue());
      }
    });

    // Reduce the number of prefixes in use
    final List<String> prefixes = new ArrayList<String>();
    for (int i = 0; i < prefixesList.size(); i++)
    {
      final boolean add = i < 5
                          || prefixesList.get(i).getValue() > prefixesMap
                            .size() * 0.5;
      if (add)
      {
        prefixes.add(prefixesList.get(i).getKey());
      }
    }
    prefixes.add("");

    return prefixes;
  }

  private static Map<String, Column> mapColumnNameMatches(final Table table)
  {
    final Map<String, Column> matchMap = new HashMap<String, Column>();

    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey != null && primaryKey.getColumns().length == 1)
    {
      matchMap.put("id", primaryKey.getColumns()[0]);
    }

    for (final Column column: table.getColumns())
    {
      String matchColumnName = column.getName().toLowerCase();
      if (matchColumnName.endsWith("_id"))
      {
        matchColumnName = matchColumnName
          .substring(0, matchColumnName.length() - 3);
      }
      if (matchColumnName.endsWith("id") && !matchColumnName.equals("id"))
      {
        matchColumnName = matchColumnName
          .substring(0, matchColumnName.length() - 2);
      }
      matchMap.put(matchColumnName, column);
    }

    return matchMap;
  }

  private static Map<String, ForeignKeyColumnMap> mapForeignKeyColumns(final List<Table> tables)
  {
    final Map<String, ForeignKeyColumnMap> fkColumnsMap = new HashMap<String, ForeignKeyColumnMap>();
    for (final Table table: tables)
    {
      for (final ForeignKey fk: table.getForeignKeys())
      {
        for (final ForeignKeyColumnMap fkMap: fk.getColumnPairs())
        {
          fkColumnsMap.put(fkMap.getForeignKeyColumn().getFullName(), fkMap);
        }
      }
    }
    return fkColumnsMap;
  }

  private static Map<String, Table> mapTableNameMatches(final List<Table> tables,
                                                        final Collection<String> prefixes)
  {
    final Map<String, Table> matchMap = new HashMap<String, Table>();
    for (final Table table: tables)
    {
      for (final String prefix: prefixes)
      {
        String matchTableName = table.getName().toLowerCase();
        if (matchTableName.startsWith(prefix))
        {
          matchTableName = matchTableName.substring(prefix.length());
          matchTableName = Inflection.singularize(matchTableName);
          matchMap.put(matchTableName, table);
        }
      }
    }
    matchMap.remove("");
    return matchMap;
  }

  private final Database database;
  private final List<Table> tables;
  private final Map<String, List<ColumnMap>> weakAssociationsMap;

  public DatabaseWithWeakAssociations(final Database database)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;

    tables = new ArrayList<Table>();
    for (final Schema schema: database.getSchemas())
    {
      for (final Table table: schema.getTables())
      {
        tables.add(table);
      }
    }

    weakAssociationsMap = new LinkedHashMap<String, List<ColumnMap>>();

    analyzeTables();
  }

  public int compareTo(final NamedObject o)
  {
    return database.compareTo(o);
  }

  public Object getAttribute(final String name)
  {
    return database.getAttribute(name);
  }

  public Map<String, Object> getAttributes()
  {
    return database.getAttributes();
  }

  public DatabaseInfo getDatabaseInfo()
  {
    return database.getDatabaseInfo();
  }

  public String getFullName()
  {
    return database.getFullName();
  }

  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return database.getJdbcDriverInfo();
  }

  public String getName()
  {
    return database.getName();
  }

  public String getRemarks()
  {
    return database.getRemarks();
  }

  public Schema getSchema(final String name)
  {
    return database.getSchema(name);
  }

  public SchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return database.getSchemaCrawlerInfo();
  }

  public Schema[] getSchemas()
  {
    return database.getSchemas();
  }

  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return database.getSystemColumnDataType(name);
  }

  public ColumnDataType[] getSystemColumnDataTypes()
  {
    return database.getSystemColumnDataTypes();
  }

  public ColumnMap[] getWeakAssociations()
  {
    final Set<ColumnMap> weakAssociations = new HashSet<ColumnMap>();
    for (final List<ColumnMap> weakAssociationsForTable: weakAssociationsMap
      .values())
    {
      weakAssociations.addAll(weakAssociationsForTable);
    }
    return weakAssociations.toArray(new ColumnMap[weakAssociations.size()]);
  }

  public ColumnMap[] getWeakAssociationsForTable(final String tableFullName)
  {
    if (weakAssociationsMap.containsKey(tableFullName))
    {
      final List<ColumnMap> weakAssociationsForTable = weakAssociationsMap
        .get(tableFullName);
      return weakAssociationsForTable
        .toArray(new ColumnMap[weakAssociationsForTable.size()]);
    }
    else
    {
      return new ColumnMap[0];
    }
  }

  public void setAttribute(final String name, final Object value)
  {
    database.setAttribute(name, value);
  }

  private void addWeakAssociation(final Table table,
                                  final ColumnMap weakAssociation)
  {
    List<ColumnMap> weakAssociations = weakAssociationsMap.get(table
      .getFullName());
    if (weakAssociations == null)
    {
      weakAssociations = new ArrayList<ColumnMap>();
      weakAssociationsMap.put(table.getFullName(), weakAssociations);
    }
    weakAssociations.add(weakAssociation);
  }

  private void analyzeTables()
  {
    final Collection<String> prefixes = findTableNamePrefixes(tables);
    final Map<String, Table> tableMatchMap = mapTableNameMatches(tables,
                                                                 prefixes);
    if (LOGGER.isLoggable(Level.FINE))
    {
      LOGGER.log(Level.FINE, "Table prefixes=" + prefixes);
      LOGGER.log(Level.FINE, "Table matches map:"
                             + ObjectToString.toString(tableMatchMap));
    }

    final Map<String, ForeignKeyColumnMap> fkColumnsMap = mapForeignKeyColumns(tables);

    findWeakAssociations(tables, tableMatchMap, fkColumnsMap);
  }

  private void findWeakAssociations(final List<Table> tablesList,
                                    final Map<String, Table> tableMatchMap,
                                    final Map<String, ForeignKeyColumnMap> fkColumnsMap)
  {
    for (final Table table: tablesList)
    {
      final Map<String, Column> columnNameMatchesMap = mapColumnNameMatches(table);

      for (final Map.Entry<String, Column> columnEntry: columnNameMatchesMap
        .entrySet())
      {
        final String matchColumnName = columnEntry.getKey();
        final Table matchedTable = tableMatchMap.get(matchColumnName);
        final Column fkColumn = columnEntry.getValue();
        if (matchedTable != null && fkColumn != null
            && !fkColumn.getParent().equals(matchedTable))
        {
          // Check if the table association is already expressed as a
          // foreign key
          final ForeignKeyColumnMap fkColumnMap = fkColumnsMap.get(fkColumn
            .getFullName());
          if (fkColumnMap == null
              || !fkColumnMap.getPrimaryKeyColumn().getParent()
                .equals(matchedTable))
          {
            // Ensure that we associate to the primary key
            final Map<String, Column> pkColumnNameMatchesMap = mapColumnNameMatches(matchedTable);
            final Column pkColumn = pkColumnNameMatchesMap.get("id");
            if (pkColumn != null)
            {
              final ColumnDataType fkColumnType = fkColumn.getType();
              final ColumnDataType pkColumnType = pkColumn.getType();
              if (pkColumnType != null && fkColumnType != null
                  && fkColumnType.getType() == pkColumnType.getType())
              {
                LOGGER.log(Level.FINE, String
                  .format("Found weak association: %s --> %s", fkColumn
                    .getFullName(), pkColumn.getFullName()));
                final ColumnMap weakAssociation = new WeakAssociation(pkColumn,
                                                                      fkColumn);
                addWeakAssociation((Table) pkColumn.getParent(),
                                   weakAssociation);
                addWeakAssociation((Table) fkColumn.getParent(),
                                   weakAssociation);
              }
            }
          }
        }
      }
    }
  }

}
