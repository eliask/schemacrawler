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
package schemacrawler.tools.linter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import sf.util.Multimap;
import sf.util.Utility;

public class LinterTableWithIncrementingColumns
  extends BaseLinter
{

  private class IncrementingColumn
  {
    private final Integer columnIncrement;
    private final Column column;

    IncrementingColumn(final String columnIncrement, final Column column)
    {
      if (columnIncrement == null)
      {
        this.columnIncrement = null;
      }
      else
      {
        this.columnIncrement = new Integer(columnIncrement);
      }
      this.column = column;
    }

    public Column getColumn()
    {
      return column;
    }

    public Integer getColumnIncrement()
    {
      return columnIncrement;
    }

  }

  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getSummary()
  {
    return "incrementing columns";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table == null)
    {
      throw new IllegalArgumentException("No table provided");
    }

    final Multimap<String, IncrementingColumn> incrementingColumns = findIncrementingColumns(table
      .getColumns());
    for (final List<IncrementingColumn> incrementingColumnsList: incrementingColumns
      .values())
    {
      addIncrementingColumnsLints(table, incrementingColumnsList);
    }
  }

  private void addIncrementingColumnsLints(final Table table,
                                           final List<IncrementingColumn> incrementingColumnsList)
  {

    int minIncrement = Integer.MAX_VALUE;
    int maxIncrement = 0;
    final ArrayList<Column> incrementingColumns = new ArrayList<>(incrementingColumnsList
      .size());
    for (int i = 0; i < incrementingColumnsList.size(); i++)
    {
      final IncrementingColumn incrementingColumn = incrementingColumnsList
        .get(i);
      incrementingColumns.add(i, incrementingColumn.getColumn());
      if (incrementingColumn.getColumnIncrement() != null)
      {
        minIncrement = Math.min(minIncrement,
                                incrementingColumn.getColumnIncrement());
        maxIncrement = Math.max(maxIncrement,
                                incrementingColumn.getColumnIncrement());
      }
    }
    Collections.sort(incrementingColumns);
    addLint(table, getSummary(), incrementingColumns);

    // Check for increments that are not consecutive
    if (maxIncrement - minIncrement + 1 != incrementingColumnsList.size())
    {
      addLint(table,
              "incrementing columns are not consecutive",
              incrementingColumns);
    }

    // Check for consistent column data-types
    final ColumnDataType columnDataType = incrementingColumns.get(0)
      .getColumnDataType();
    final int columnSize = incrementingColumns.get(0).getSize();
    for (int i = 1; i < incrementingColumns.size(); i++)
    {
      if (!columnDataType
        .equals(incrementingColumns.get(i).getColumnDataType())
          || columnSize != incrementingColumns.get(i).getSize())
      {
        addLint(table,
                "incrementing columns don't have the same data-type",
                incrementingColumns);
        break;
      }
    }

  }

  private Multimap<String, IncrementingColumn> findIncrementingColumns(final List<Column> columns)
  {
    if (columns == null || columns.size() <= 1)
    {
      return new Multimap<String, IncrementingColumn>();
    }

    final Pattern pattern = Pattern.compile("(.*[^0-9])([0-9]+)");

    final Map<String, Integer> incrementingColumnsMap = new HashMap<>();
    for (final Column column: columns)
    {
      final String columnName = Utility.convertForComparison(column.getName());
      incrementingColumnsMap.put(columnName, 1);
      final Matcher matcher = pattern.matcher(columnName);
      if (matcher.matches())
      {
        final String columnNameBase = matcher.group(1);
        if (incrementingColumnsMap.containsKey(columnNameBase))
        {
          incrementingColumnsMap
            .put(columnNameBase, incrementingColumnsMap.get(columnNameBase) + 1);
        }
        else
        {
          incrementingColumnsMap.put(columnNameBase, 1);
        }
      }
    }

    final Set<String> columnNameBases = new HashSet<>(incrementingColumnsMap
      .keySet());
    for (final String columnNameBase: columnNameBases)
    {
      if (incrementingColumnsMap.get(columnNameBase) == 1)
      {
        incrementingColumnsMap.remove(columnNameBase);
      }
    }

    final Multimap<String, IncrementingColumn> incrementingColumns = new Multimap<>();

    for (final Column column: columns)
    {
      final String columnName = Utility.convertForComparison(column.getName());
      if (incrementingColumnsMap.containsKey(columnName))
      {
        incrementingColumns
          .add(columnName, new IncrementingColumn("0", column));
      }
      final Matcher matcher = pattern.matcher(columnName);
      if (matcher.matches())
      {
        final String columnNameBase = matcher.group(1);
        final String columnIncrement = matcher.group(2);
        if (incrementingColumnsMap.containsKey(columnNameBase))
        {
          incrementingColumns.add(columnNameBase,
                                  new IncrementingColumn(columnIncrement,
                                                         column));
        }
      }
    }

    return incrementingColumns;
  }
}
