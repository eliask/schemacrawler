/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import sf.util.Utility;

/**
 * Base class for retriever that uses database metadata to get the
 * details about the schema.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractRetriever
{

  /**
   * Reads a single column result set as a list.
   * 
   * @param results
   *        Result set
   * @return List
   * @throws SQLException
   *         On an exception
   */
  static List<String> readResultsVector(final ResultSet results)
    throws SQLException
  {
    final List<String> values = new ArrayList<String>();
    try
    {
      while (results.next())
      {
        final String value = results.getString(1);
        values.add(value);
      }
    }
    finally
    {
      results.close();
    }
    return values;
  }

  private final RetrieverConnection retrieverConnection;

  final MutableDatabase database;

  AbstractRetriever()
    throws SQLException
  {
    this(null, null);
  }

  AbstractRetriever(final RetrieverConnection retrieverConnection,
                    final MutableDatabase database)
    throws SQLException
  {
    this.retrieverConnection = retrieverConnection;
    this.database = database;
  }

  /**
   * Checks whether the provided database object belongs to the
   * specified schema.
   * 
   * @param dbObject
   *        Database object to check
   * @param catalogName
   *        Database catalog to check against
   * @param schemaName
   *        Database schema to check against
   * @return Whether the database object belongs to the specified schema
   */
  boolean belongsToSchema(final DatabaseObject dbObject,
                          final String catalogName,
                          final String schemaName)
  {
    if (dbObject == null)
    {
      return false;
    }

    final boolean supportsCatalogs = retrieverConnection.isSupportsCatalogs();

    boolean belongsToCatalog = true;
    boolean belongsToSchema = true;
    if (supportsCatalogs)
    {
      final String dbObjectCatalogName = dbObject.getSchema().getCatalogName();
      if (catalogName != null
          && !unquotedName(catalogName)
            .equals(unquotedName(dbObjectCatalogName)))
      {
        belongsToCatalog = false;
      }
    }
    final String dbObjectSchemaName = dbObject.getSchema().getName();
    if (schemaName != null
        && !unquotedName(schemaName).equals(unquotedName(dbObjectSchemaName)))
    {
      belongsToSchema = false;
    }
    return belongsToCatalog && belongsToSchema;
  }

  Connection getDatabaseConnection()
  {
    return retrieverConnection.getConnection();
  }

  DatabaseMetaData getMetaData()
  {
    return retrieverConnection.getMetaData();
  }

  RetrieverConnection getRetrieverConnection()
  {
    return retrieverConnection;
  }

  Collection<Schema> getSchemas()
  {
    return database.getSchemas();
  }

  /**
   * Creates a data type from the JDBC data type id, and the database
   * specific type name, if it does not exist.
   * 
   * @param schema
   *        Schema
   * @param javaSqlType
   *        JDBC data type
   * @param databaseSpecificTypeName
   *        Database specific type name
   * @return Column data type
   */
  MutableColumnDataType lookupOrCreateColumnDataType(final Schema schema,
                                                     final int javaSqlType,
                                                     final String databaseSpecificTypeName)
  {
    MutableColumnDataType columnDataType = database
      .getColumnDataType(schema, databaseSpecificTypeName);
    if (columnDataType == null)
    {
      columnDataType = database
        .getSystemColumnDataType(databaseSpecificTypeName);
    }
    // Create new data type, if needed
    if (columnDataType == null)
    {
      columnDataType = new MutableColumnDataType(schema,
                                                 databaseSpecificTypeName);
      // Set the Java SQL type code, but no mapped Java class is
      // available, so use the defaults
      columnDataType.setType(javaSqlType, null);
      database.addColumnDataType(columnDataType);
    }
    return columnDataType;
  }

  MutableRoutine lookupRoutine(final String catalogName,
                               final String schemaName,
                               final String routineName,
                               final String specificName)
  {
    MutableRoutine routine = null;
    final Schema schema = new SchemaReference(catalogName, schemaName);
    if (schema != null)
    {
      final String routineLookupName;
      if (!Utility.isBlank(specificName))
      {
        routineLookupName = specificName;
      }
      else
      {
        routineLookupName = routineName;
      }
      routine = (MutableRoutine) database
        .getRoutine(new SchemaReference(catalogName, schemaName),
                    routineLookupName);
    }
    return routine;
  }

  MutableTable lookupTable(final String catalogName,
                           final String schemaName,
                           final String tableName)
  {
    MutableTable table = null;
    final Schema schema = new SchemaReference(catalogName, schemaName);
    if (schema != null)
    {
      table = database.getTable(new SchemaReference(catalogName, schemaName),
                                tableName);
    }
    return table;
  }

  String quotedName(final String name)
  {
    final String quotedName;
    final RetrieverConnection retrieverConnection = getRetrieverConnection();
    if (retrieverConnection != null && !Utility.isBlank(name))
    {
      final String identifierQuoteString = retrieverConnection
        .getIdentifierQuoteString();
      if (retrieverConnection.needsToBeQuoted(name))
      {
        quotedName = identifierQuoteString + name + identifierQuoteString;
      }
      else
      {
        quotedName = name;
      }
    }
    else
    {
      quotedName = name;
    }
    return quotedName;
  }

  String unquotedName(final String name)
  {
    final String unquotedName;
    final RetrieverConnection retrieverConnection = getRetrieverConnection();
    if (retrieverConnection != null && !Utility.isBlank(name))
    {
      final String identifierQuoteString = retrieverConnection
        .getIdentifierQuoteString();
      if (name.startsWith(identifierQuoteString)
          && name.endsWith(identifierQuoteString))
      {
        final int quoteLength = identifierQuoteString.length();
        unquotedName = name.substring(quoteLength, name.length() - quoteLength);
      }
      else
      {
        unquotedName = name;
      }
    }
    else
    {
      unquotedName = name;
    }
    return unquotedName;
  }

}
