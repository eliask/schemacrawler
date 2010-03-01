package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.ObjectToString;
import sf.util.Utility;

public class LinterTableWithIncrementingColumns
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final HashMap<String, List<Column>> incrementingColumns = findIncrementingColumns(table
        .getColumns());
      if (!incrementingColumns.isEmpty())
      {
        addLint(table, new Lint("incrementing columns", incrementingColumns)
        {

          private static final long serialVersionUID = -9152369844685463520L;

          @Override
          public String getLintValueAsString()
          {
            final StringBuilder buffer = new StringBuilder();
            for (final List<Column> incrementingColumnsGroup: incrementingColumns
              .values())
            {
              final List<String> columnNames = new ArrayList<String>();
              for (final Column column: incrementingColumnsGroup)
              {
                columnNames.add(column.getName());
              }
              buffer.append(ObjectToString.toString(columnNames))
                .append(Utility.NEWLINE);
            }
            return buffer.toString();
          }
        });
      }
    }
  }

  private HashMap<String, List<Column>> findIncrementingColumns(final Column[] columns)
  {
    if (columns == null || columns.length <= 1)
    {
      return new HashMap<String, List<Column>>();
    }

    final Pattern pattern = Pattern.compile("([^0-9]*)([0-9]+)");

    final Map<String, Integer> incrementingColumnsMap = new HashMap<String, Integer>();
    for (final Column column: columns)
    {
      final String columnName = column.getName();
      incrementingColumnsMap.put(columnName, 1);
      final Matcher matcher = pattern.matcher(columnName);
      if (matcher.matches())
      {
        final String columnNameBase = matcher.group(1);
        if (incrementingColumnsMap.containsKey(columnNameBase))
        {
          incrementingColumnsMap.put(columnNameBase, incrementingColumnsMap
            .get(columnNameBase) + 1);
        }
        else
        {
          incrementingColumnsMap.put(columnNameBase, 1);
        }
      }
    }

    final HashSet<String> columnNameBases = new HashSet<String>(incrementingColumnsMap
      .keySet());
    for (final String columnNameBase: columnNameBases)
    {
      if (incrementingColumnsMap.get(columnNameBase) == 1)
      {
        incrementingColumnsMap.remove(columnNameBase);
      }
    }

    final HashMap<String, List<Column>> incrementingColumns = new HashMap<String, List<Column>>();
    for (final String columnNameBase: incrementingColumnsMap.keySet())
    {
      incrementingColumns.put(columnNameBase, new ArrayList<Column>());
    }

    for (final Column column: columns)
    {
      final String columnName = column.getName();

      if (incrementingColumnsMap.containsKey(columnName))
      {
        incrementingColumns.get(columnName).add(column);
      }

      final Matcher matcher = pattern.matcher(columnName);
      if (matcher.matches())
      {
        final String columnNameBase = matcher.group(1);
        if (incrementingColumnsMap.containsKey(columnNameBase))
        {
          incrementingColumns.get(columnNameBase).add(column);
        }
      }
    }

    return incrementingColumns;
  }

}