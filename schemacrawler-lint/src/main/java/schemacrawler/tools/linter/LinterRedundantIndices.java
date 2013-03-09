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


import static schemacrawler.tools.lint.LintUtility.columns;
import static schemacrawler.tools.lint.LintUtility.listStartsWith;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;

public class LinterRedundantIndices
  extends BaseLinter
{

  public LinterRedundantIndices()
  {
    setSeverity(LintSeverity.high);
  }

  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getSummary()
  {
    return "redundant index";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table != null && !(table instanceof View))
    {
      final Collection<Index> indices = table.getIndices();
      if (indices.size() > 0)
      {
        final Set<Index> redundantIndices = findRedundantIndices(indices);
        for (final Index index: redundantIndices)
        {
          addLint(table, getSummary(), index);
        }
      }
    }
  }

  private Set<Index> findRedundantIndices(final Collection<Index> indices)
  {
    final Set<Index> redundantIndices = new HashSet<>();
    final Map<Index, List<String>> indexColumns = new HashMap<>(indices
      .size());
    for (final Index index: indices)
    {
      indexColumns.put(index, columns(index));
    }

    for (final Entry<Index, List<String>> indexColumnEntry1: indexColumns
      .entrySet())
    {
      for (final Entry<Index, List<String>> indexColumnEntry2: indexColumns
        .entrySet())
      {
        if (!indexColumnEntry1.equals(indexColumnEntry2))
        {
          if (listStartsWith(indexColumnEntry1.getValue(),
                             indexColumnEntry2.getValue()))
          {
            redundantIndices.add(indexColumnEntry2.getKey());
          }
        }
      }
    }
    return redundantIndices;
  }

}
