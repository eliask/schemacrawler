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
package schemacrawler.filter;


import schemacrawler.schema.NamedObject;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InclusionRule;

public class InclusionRuleFilter<N extends NamedObject>
  implements NamedObjectFilter<N>
{

  private final InclusionRule inclusionRule;

  public InclusionRuleFilter(final InclusionRule inclusionRule,
                             final boolean inclusive)
  {
    if (inclusionRule != null)
    {
      this.inclusionRule = inclusionRule;
    }
    else
    {
      if (inclusive)
      {
        this.inclusionRule = new IncludeAll();
      }
      else
      {
        this.inclusionRule = new ExcludeAll();
      }
    }
  }

  @Override
  public boolean include(final N namedObject)
  {
    if (namedObject == null)
    {
      return false;
    }
    // Schema names may be null
    if (namedObject.getFullName() == null)
    {
      return false;
    }
    return inclusionRule.include(namedObject.getFullName());
  }

  public boolean isExcludeAll()
  {
    return inclusionRule instanceof ExcludeAll;
  }

}
