/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An enumeration wrapper around JDBC table types.
 */
public enum TableType
{

  /**
   * Unknown
   */
  unknown,
  /**
   * System table
   */
  system_table,
  /**
   * Global temporary
   */
  global_temporary,
  /**
   * Local temporary
   */
  local_temporary,
  /**
   * Table
   */
  table,
  /**
   * View
   */
  view,
  /**
   * Alias
   */
  alias,
  /**
   * Synonym
   */
  synonym,;

  /**
   * Converts an array of table types to an array of their corresponding string values.
   *
   * @param tableTypes Array of table types
   *
   * @return Array of string table types
   */
  public static String[] toStrings(final TableType[] tableTypes)
  {
    if (tableTypes == null || tableTypes.length == 0)
    {
      return new String[0];
    }

    final List<String> tableTypeStrings = new ArrayList<String>(tableTypes.length);
    for (final TableType tableType : tableTypes)
    {
      if (tableType != null)
      {
        tableTypeStrings.add(tableType.toString().toUpperCase(Locale.ENGLISH));
      }
    }
    return tableTypeStrings.toArray(new String[tableTypeStrings.size()]);
  }

  /**
   * Converts an array of string table types to an array of their corresponding enumeration values.
   *
   * @param tableTypeStrings Array of string table types
   *
   * @return Array of table types
   */
  public static TableType[] valueOf(final String[] tableTypeStrings)
  {
    if (tableTypeStrings == null || tableTypeStrings.length == 0)
    {
      return new TableType[0];
    }

    final List<TableType> tableTypes = new ArrayList<TableType>(tableTypeStrings.length);
    for (final String tableTypeString : tableTypeStrings)
    {
      tableTypes.add(valueOf(tableTypeString.toLowerCase(Locale.ENGLISH)));
    }
    return tableTypes.toArray(new TableType[tableTypes.size()]);
  }

}
