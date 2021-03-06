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

package schemacrawler.schema;


import java.util.Collection;

/**
 * Database and connection information.
 * 
 * @author Sualeh Fatehi
 */
public interface Database
  extends NamedObject
{

  /**
   * Gets the column data types defined in the schema, by name.
   * 
   * @param name
   *        Name
   * @return Column data types
   */
  ColumnDataType getColumnDataType(Schema schema, String name);

  /**
   * Gets the column data types
   * 
   * @return Column data types
   */
  Collection<ColumnDataType> getColumnDataTypes();

  /**
   * Gets the column data types defined in the schema, by name.
   * 
   * @return Column data types
   */
  Collection<ColumnDataType> getColumnDataTypes(Schema schema);

  DatabaseInfo getDatabaseInfo();

  JdbcDriverInfo getJdbcDriverInfo();

  /**
   * Gets a routine by name.
   * 
   * @param name
   *        Name
   * @return Routine.
   */
  Routine getRoutine(Schema schema, String name);

  /**
   * Gets the routine.
   * 
   * @return Routines
   */
  Collection<Routine> getRoutines();

  /**
   * Gets the routine.
   * 
   * @return Routines
   */
  Collection<Routine> getRoutines(Schema schema);

  /**
   * Gets a schema by name.
   * 
   * @param name
   *        Schema name
   * @return Schema.
   */
  Schema getSchema(String name);

  SchemaCrawlerInfo getSchemaCrawlerInfo();

  /**
   * Gets the schemas.
   * 
   * @return Schemas
   */
  Collection<Schema> getSchemas();

  /**
   * Gets the synonym by name.
   * 
   * @param name
   *        Name
   * @return Synonym.
   */
  Synonym getSynonym(Schema schema, String name);

  /**
   * Gets the synonyms.
   * 
   * @return Synonyms
   */
  Collection<Synonym> getSynonyms();

  /**
   * Gets the synonyms.
   * 
   * @return Synonyms
   */
  Collection<Synonym> getSynonyms(Schema schema);

  /**
   * Gets the column data types defined by the RDBMS system, by name.
   * 
   * @param name
   *        Column data type name
   * @return Column data type
   */
  ColumnDataType getSystemColumnDataType(String name);

  /**
   * Gets the column data types defined by the RDBMS system.
   * 
   * @return Column data types
   */
  Collection<ColumnDataType> getSystemColumnDataTypes();

  /**
   * Gets a table by name.
   * 
   * @param name
   *        Name
   * @return Table.
   */
  Table getTable(Schema schema, String name);

  /**
   * Gets the tables.
   * 
   * @return Tables
   */
  Collection<Table> getTables();

  /**
   * Gets the tables.
   * 
   * @return Tables
   */
  Collection<Table> getTables(Schema schema);

}
