/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


/**
 * Represents the database schema.
 * 
 * @author Sualeh Fatehi
 */
public interface Schema
  extends DependantNamedObject
{

  /**
   * Gets the column data types defined in the schema, by name.
   * 
   * @return Column data types
   */
  ColumnDataType getColumnDataType(String name);

  /**
   * Gets the column data types defined in the schema, by name.
   * 
   * @return Column data types
   */
  ColumnDataType[] getColumnDataTypes();

  /**
   * Gets the full name, including the schema name.
   * 
   * @return Full name
   */
  String getFullName();

  /**
   * Gets a procedure by name.
   * 
   * @param name
   *        Name
   * @return Procedure.
   */
  Procedure getProcedure(String name);

  /**
   * Gets the procedures.
   * 
   * @return Procedures
   */
  Procedure[] getProcedures();

  /**
   * Gets a table by name.
   * 
   * @param name
   *        Name
   * @return Table.
   */
  Table getTable(String name);

  /**
   * Gets the tables.
   * 
   * @return Tables
   */
  Table[] getTables();

}
