/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.text.schema;


import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.base.BaseTextOptions;

public class SchemaTextOptions
  extends BaseTextOptions
{

  private static final long serialVersionUID = -8133661515343358712L;

  private static final String SHOW_ORDINAL_NUMBERS = SCHEMACRAWLER_FORMAT_PREFIX
                                                     + "show_ordinal_numbers";
  private static final String SHOW_STANDARD_COLUMN_TYPE_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                                + "show_standard_column_type_names";

  private static final String HIDE_PRIMARY_KEY_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                       + "hide_primarykey_names";
  private static final String HIDE_FOREIGN_KEY_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                       + "hide_foreignkey_names";
  private static final String HIDE_INDEX_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                 + "hide_index_names";
  private static final String HIDE_CONSTRAINT_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                      + "hide_constraint_names";
  private static final String HIDE_TRIGGER_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                   + "hide_trigger_names";
  private static final String HIDE_ROUTINE_SPECIFIC_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                            + "hide_routine_specific_names";

  private static final String SC_SORT_ALPHABETICALLY_TABLES = SCHEMACRAWLER_FORMAT_PREFIX
                                                              + "sort_alphabetically.tables";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_COLUMNS = SCHEMACRAWLER_FORMAT_PREFIX
                                                                     + "sort_alphabetically.table_columns";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_INDEXES = SCHEMACRAWLER_FORMAT_PREFIX
                                                                     + "sort_alphabetically.table_indices";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS = SCHEMACRAWLER_FORMAT_PREFIX
                                                                         + "sort_alphabetically.table_foreignkeys";
  private static final String SC_SORT_ALPHABETICALLY_ROUTINE_COLUMNS = SCHEMACRAWLER_FORMAT_PREFIX
                                                                       + "sort_alphabetically.routine_columns";

  /**
   * Creates the default SchemaTextOptions.
   */
  public SchemaTextOptions()
  {
    this(null);
  }

  /**
   * Options from properties. Constructor.
   * 
   * @param config
   *        Properties
   */
  public SchemaTextOptions(final Config config)
  {
    super(config);

    setShowStandardColumnTypeNames(getBooleanValue(config,
                                                   SHOW_STANDARD_COLUMN_TYPE_NAMES));
    setShowOrdinalNumbers(getBooleanValue(config, SHOW_ORDINAL_NUMBERS));

    setHideForeignKeyNames(getBooleanValue(config, HIDE_FOREIGN_KEY_NAMES));
    setHidePrimaryKeyNames(getBooleanValue(config, HIDE_PRIMARY_KEY_NAMES));
    setHideIndexNames(getBooleanValue(config, HIDE_INDEX_NAMES));
    setHideTriggerNames(getBooleanValue(config, HIDE_TRIGGER_NAMES));
    setHideRoutineSpecificNames(getBooleanValue(config,
                                                HIDE_ROUTINE_SPECIFIC_NAMES));
    setHideConstraintNames(getBooleanValue(config, HIDE_CONSTRAINT_NAMES));

    setAlphabeticalSortForTables(getBooleanValue(config,
                                                 SC_SORT_ALPHABETICALLY_TABLES,
                                                 true));
    setAlphabeticalSortForTableColumns(getBooleanValue(config,
                                                       SC_SORT_ALPHABETICALLY_TABLE_COLUMNS));
    setAlphabeticalSortForForeignKeys(getBooleanValue(config,
                                                      SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS));
    setAlphabeticalSortForIndexes(getBooleanValue(config,
                                                  SC_SORT_ALPHABETICALLY_TABLE_INDEXES));
    setAlphabeticalSortForRoutineColumns(getBooleanValue(config,
                                                         SC_SORT_ALPHABETICALLY_ROUTINE_COLUMNS));
  }

  public boolean isAlphabeticalSortForForeignKeys()
  {
    return getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS);
  }

  public boolean isAlphabeticalSortForIndexes()
  {
    return getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES);
  }

  public boolean isAlphabeticalSortForRoutineColumns()
  {
    return getBooleanValue(SC_SORT_ALPHABETICALLY_ROUTINE_COLUMNS);
  }

  public boolean isAlphabeticalSortForTableColumns()
  {
    return getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_COLUMNS);
  }

  public boolean isAlphabeticalSortForTables()
  {
    return getBooleanValue(SC_SORT_ALPHABETICALLY_TABLES);
  }

  /**
   * Whether to hide constraint names.
   * 
   * @return Hide constraint names.
   */
  public boolean isHideConstraintNames()
  {
    return getBooleanValue(HIDE_CONSTRAINT_NAMES);
  }

  /**
   * Whether to hide foreign key names.
   * 
   * @return Hide foreign key names.
   */
  public boolean isHideForeignKeyNames()
  {
    return getBooleanValue(HIDE_FOREIGN_KEY_NAMES);
  }

  /**
   * Whether to hide index names.
   * 
   * @return Hide index names.
   */
  public boolean isHideIndexNames()
  {
    return getBooleanValue(HIDE_INDEX_NAMES);
  }

  /**
   * Whether to hide primary key names.
   * 
   * @return Hide primary key names.
   */
  public boolean isHidePrimaryKeyNames()
  {
    return getBooleanValue(HIDE_PRIMARY_KEY_NAMES);
  }

  /**
   * Whether to hide routine specific names.
   * 
   * @return Hide routine specific names.
   */
  public boolean isHideRoutineSpecificNames()
  {
    return getBooleanValue(HIDE_ROUTINE_SPECIFIC_NAMES);
  }

  /**
   * Whether to hide trigger names.
   * 
   * @return Hide trigger names.
   */
  public boolean isHideTriggerNames()
  {
    return getBooleanValue(HIDE_TRIGGER_NAMES);
  }

  /**
   * Whether to show ordinal numbers.
   * 
   * @return Whether to show ordinal numbers.
   */
  public boolean isShowOrdinalNumbers()
  {
    return getBooleanValue(SHOW_ORDINAL_NUMBERS);
  }

  /**
   * Whether to show standard column types.
   * 
   * @return Whether to show standard column types.
   */
  public boolean isShowStandardColumnTypeNames()
  {
    return getBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES);
  }

  public void setAlphabeticalSortForForeignKeys(final boolean isAlphabeticalSortForForeignKeys)
  {
    setBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS,
                    isAlphabeticalSortForForeignKeys);
  }

  public void setAlphabeticalSortForIndexes(final boolean isAlphabeticalSortForIndexes)
  {
    setBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES,
                    isAlphabeticalSortForIndexes);
  }

  public void setAlphabeticalSortForRoutineColumns(final boolean isAlphabeticalSortForRoutineColumns)
  {
    setBooleanValue(SC_SORT_ALPHABETICALLY_ROUTINE_COLUMNS,
                    isAlphabeticalSortForRoutineColumns);
  }

  public void setAlphabeticalSortForTableColumns(final boolean isAlphabeticalSortForTableColumns)
  {
    setBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_COLUMNS,
                    isAlphabeticalSortForTableColumns);
  }

  public void setAlphabeticalSortForTables(final boolean isAlphabeticalSortForTables)
  {
    setBooleanValue(SC_SORT_ALPHABETICALLY_TABLES, isAlphabeticalSortForTables);
  }

  /**
   * Sets whether to hide constraint names.
   * 
   * @param hideConstraintNames
   *        Whether to hide constraint names.
   */
  public void setHideConstraintNames(final boolean hideConstraintNames)
  {
    setBooleanValue(HIDE_CONSTRAINT_NAMES, hideConstraintNames);
  }

  /**
   * Sets whether to hide foreign key names.
   * 
   * @param hideForeignKeyNames
   *        Whether to hide foreign key names.
   */
  public void setHideForeignKeyNames(final boolean hideForeignKeyNames)
  {
    setBooleanValue(HIDE_FOREIGN_KEY_NAMES, hideForeignKeyNames);
  }

  /**
   * Sets whether to hide index names.
   * 
   * @param hideIndexNames
   *        Whether to hide index names.
   */
  public void setHideIndexNames(final boolean hideIndexNames)
  {
    setBooleanValue(HIDE_INDEX_NAMES, hideIndexNames);
  }

  /**
   * Sets whether to hide primary key names.
   * 
   * @param hidePrimaryKeyNames
   *        Whether to hide primary key names.
   */
  public void setHidePrimaryKeyNames(final boolean hidePrimaryKeyNames)
  {
    setBooleanValue(HIDE_PRIMARY_KEY_NAMES, hidePrimaryKeyNames);
  }

  /**
   * Sets whether to hide routine specific names.
   * 
   * @param hideRoutineSpecificNames
   *        Whether to hide routine specific names.
   */
  public void setHideRoutineSpecificNames(final boolean hideRoutineSpecificNames)
  {
    setBooleanValue(HIDE_ROUTINE_SPECIFIC_NAMES, hideRoutineSpecificNames);
  }

  /**
   * Sets whether to hide trigger names.
   * 
   * @param hideTriggerNames
   *        Whether to hide trigger names.
   */
  public void setHideTriggerNames(final boolean hideTriggerNames)
  {
    setBooleanValue(HIDE_TRIGGER_NAMES, hideTriggerNames);
  }

  /**
   * Sets whether to show ordinal numbers.
   * 
   * @param showOrdinalNumbers
   *        Whether to show ordinal numbers.
   */
  public void setShowOrdinalNumbers(final boolean showOrdinalNumbers)
  {
    setBooleanValue(SHOW_ORDINAL_NUMBERS, showOrdinalNumbers);
  }

  /**
   * Sets whether to show standard column type names.
   * 
   * @param showStandardColumnTypeNames
   *        Whether to show standard column type names.
   */
  public void setShowStandardColumnTypeNames(final boolean showStandardColumnTypeNames)
  {
    setBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES,
                    showStandardColumnTypeNames);
  }

}
