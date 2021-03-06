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
package schemacrawler.tools.analysis.associations;


import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;

/**
 * Represents a single column mapping from a primary key column to a
 * foreign key column.
 * 
 * @author Sualeh Fatehi
 */
public final class WeakAssociation
  implements ColumnReference, Comparable<ColumnReference>
{

  private static final long serialVersionUID = -4411771492159843382L;

  private final Column foreignKeyColumn;
  private final Column primaryKeyColumn;

  WeakAssociation(final Column primaryKeyColumn, final Column foreignKeyColumn)
  {
    this.primaryKeyColumn = primaryKeyColumn;
    this.foreignKeyColumn = foreignKeyColumn;
  }

  @Override
  public int compareTo(final ColumnReference o)
  {
    int compare = 0;
    if (compare == 0)
    {
      compare = primaryKeyColumn.compareTo(o.getPrimaryKeyColumn());
    }
    if (compare == 0)
    {
      compare = foreignKeyColumn.compareTo(o.getForeignKeyColumn());
    }
    return compare;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final WeakAssociation other = (WeakAssociation) obj;
    if (foreignKeyColumn == null)
    {
      if (other.foreignKeyColumn != null)
      {
        return false;
      }
    }
    else if (!foreignKeyColumn.equals(other.foreignKeyColumn))
    {
      return false;
    }
    if (primaryKeyColumn == null)
    {
      if (other.primaryKeyColumn != null)
      {
        return false;
      }
    }
    else if (!primaryKeyColumn.equals(other.primaryKeyColumn))
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnReference#getForeignKeyColumn()
   */
  @Override
  public Column getForeignKeyColumn()
  {
    return foreignKeyColumn;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnReference#getPrimaryKeyColumn()
   */
  @Override
  public Column getPrimaryKeyColumn()
  {
    return primaryKeyColumn;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (foreignKeyColumn == null? 0: foreignKeyColumn.hashCode());
    result = prime * result
             + (primaryKeyColumn == null? 0: primaryKeyColumn.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return primaryKeyColumn + " --> " + foreignKeyColumn;
  }

}
