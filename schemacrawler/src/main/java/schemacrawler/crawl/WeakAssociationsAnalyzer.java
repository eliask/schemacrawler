package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;

public class WeakAssociationsAnalyzer
{

  private static final Logger LOGGER = Logger
    .getLogger(WeakAssociationsAnalyzer.class.getName());

  public MutableWeakAssociations analyzeTables(final MutableDatabase database)
  {
    final NamedObjectList<MutableTable> tables = database.getAllTables();

    final Collection<String> prefixes = findTableNamePrefixes(tables);
    LOGGER.log(Level.FINE, "Table prefixes=" + prefixes);

    final Map<String, MutableTable> tableMatchMap = mapTableNameMatches(tables,
                                                                        prefixes);
    LOGGER.log(Level.FINE, "Table matches map=" + tableMatchMap);

    final Map<String, ForeignKeyColumnMap> fkColumnsMap = mapForeignKeyColumns(tables);

    return findWeakAssociations(tables, tableMatchMap, fkColumnsMap);
  }

  private String commonPrefix(final String string1, final String string2)
  {
    final int index = indexOfDifference(string1, string2);
    if (index == -1)
    {
      return null;
    }
    else
    {
      return string1.substring(0, index).toLowerCase();
    }
  }

  /**
   * Finds table prefixes. A prefix ends with "_".
   */
  private Collection<String> findTableNamePrefixes(final NamedObjectList<MutableTable> tables)
  {
    final SortedMap<String, Integer> prefixesMap = new TreeMap<String, Integer>();
    final List<MutableTable> tablesList = tables.getAll();
    for (int i = 0; i < tables.size(); i++)
    {
      for (int j = i + 1; j < tables.size(); j++)
      {
        final String table1 = tablesList.get(i).getName();
        final String table2 = tablesList.get(j).getName();
        final String commonPrefix = commonPrefix(table1, table2);
        if (commonPrefix != null && !commonPrefix.equals("")
            && commonPrefix.endsWith("_"))
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

  private MutableWeakAssociations findWeakAssociations(final NamedObjectList<MutableTable> tables,
                                                       final Map<String, MutableTable> tableMatchMap,
                                                       final Map<String, ForeignKeyColumnMap> fkColumnsMap)
  {
    final MutableWeakAssociations weakAssociations = new MutableWeakAssociations();
    final List<MutableTable> tablesList = tables.getAll();
    for (final MutableTable table: tablesList)
    {
      final Map<String, Column> columnNameMatchesMap = mapColumnNameMatches(table);

      for (final Map.Entry<String, Column> columnEntry: columnNameMatchesMap
        .entrySet())
      {
        final String matchColumnName = columnEntry.getKey();
        final MutableTable matchedTable = tableMatchMap.get(matchColumnName);
        final Column fkColumn = columnEntry.getValue();
        if (matchedTable != null && !fkColumn.getParent().equals(matchedTable))
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
            if (pkColumn != null && fkColumn != null)
            {
              final ColumnDataType fkColumnType = fkColumn.getType();
              final ColumnDataType pkColumnType = pkColumn.getType();
              if (pkColumnType != null && fkColumnType != null
                  && fkColumnType.getType() == pkColumnType.getType())
              {
                LOGGER.log(Level.FINE, "Found weak association "
                                       + fkColumn.getFullName() + " --> "
                                       + pkColumn.getFullName());
                weakAssociations.addColumnPair(pkColumn, fkColumn);
              }
            }
          }
        }
      }
    }

    return weakAssociations;
  }

  private int indexOfDifference(final String string1, final String string2)
  {
    if (string1 == string2)
    {
      return -1;
    }
    if (string1 == null || string2 == null)
    {
      return 0;
    }
    int i;
    for (i = 0; i < string1.length() && i < string2.length(); ++i)
    {
      if (string1.charAt(i) != string2.charAt(i))
      {
        break;
      }
    }
    if (i < string2.length() || i < string1.length())
    {
      return i;
    }
    return -1;
  }

  private Map<String, Column> mapColumnNameMatches(final MutableTable table)
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

  private Map<String, ForeignKeyColumnMap> mapForeignKeyColumns(final NamedObjectList<MutableTable> tables)
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

  private Map<String, MutableTable> mapTableNameMatches(final NamedObjectList<MutableTable> tables,
                                                        final Collection<String> prefixes)
  {
    final Map<String, MutableTable> matchMap = new HashMap<String, MutableTable>();
    for (final MutableTable table: tables)
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

}