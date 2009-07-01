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
 * Represents a table in the database.
 * 
 * @author Sualeh Fatehi
 */
public interface Table
  extends DatabaseObject
{

  /**
   * Gets the list of check constraints.
   * 
   * @return Check constraints for the table
   */
  CheckConstraint[] getCheckConstraints();

  /**
   * Gets a column by name.
   * 
   * @param name
   *        Name
   * @return Column.
   */
  Column getColumn(String name);

  /**
   * Gets the list of columns in ordinal order.
   * 
   * @return Columns of the table
   */
  Column[] getColumns();

  /**
   * Gets a comma-separated list of columns.
   * 
   * @return Comma-separated list of columns
   */
  String getColumnsListAsString();

  /**
   * Gets a foreign key by name.
   * 
   * @param name
   *        Name
   * @return ForeignKey.
   */
  ForeignKey getForeignKey(String name);

  /**
   * Gets the list of foreign keys.
   * 
   * @return Foreign keys of the table.
   */
  ForeignKey[] getForeignKeys();

  /**
   * Gets an index by name.
   * 
   * @param name
   *        Name
   * @return Index.
   */
  Index getIndex(String name);

  /**
   * Gets the list of indices.
   * 
   * @return Indices of the table.
   */
  Index[] getIndices();

  /**
   * Gets the primary key.
   * 
   * @return Primary key
   */
  PrimaryKey getPrimaryKey();

  /**
   * Gets the list of privileges.
   * 
   * @return Privileges for the table.
   */
  Privilege[] getPrivileges();

  /**
   * Gets a trigger by name.
   * 
   * @param name
   *        Name
   * @return Trigger.
   */
  Trigger getTrigger(String name);

  /**
   * Gets the list of triggers.
   * 
   * @return Triggers for the table.
   */
  Trigger[] getTriggers();

  /**
   * Gets the table type.
   * 
   * @return Table type.
   */
  TableType getType();

  /**
   * Weak column associations that are derived by SchemaCrawler from the
   * column names.
   */
  WeakAssociations getWeakAssociations();

}
