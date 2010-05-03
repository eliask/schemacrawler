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

package schemacrawler.crawl;


import schemacrawler.schema.Column;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;

/**
 * Represents an index on a database table.
 * 
 * @author Sualeh Fatehi
 */
class MutableIndex
  extends AbstractDependantObject
  implements Index
{

  private static final long serialVersionUID = 4051326747138079028L;

  private final NamedObjectList<MutableIndexColumn> columns = new NamedObjectList<MutableIndexColumn>();
  private boolean isUnique;
  private IndexType type;
  private int cardinality;
  private int pages;

  MutableIndex(final Table parent, final String name)
  {
    super(parent, name);
    // Default values
    type = IndexType.unknown;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note: Since indexes are not always explicitly named in databases,
   * the sorting routine orders the indexes by the names of the columns
   * in the index.
   * </p>
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null)
    {
      return -1;
    }

    final Index other = (Index) obj;
    int comparison = 0;
    final Column[] thisColumns = getColumns();
    final Column[] otherColumns = other.getColumns();

    if (comparison == 0)
    {
      comparison = thisColumns.length - otherColumns.length;
    }
    if (comparison == 0)
    {
      for (int i = 0; i < thisColumns.length; i++)
      {
        final Column thisColumn = thisColumns[i];
        final Column otherColumn = otherColumns[i];
        if (comparison == 0)
        {
          comparison = thisColumn.compareTo(otherColumn);
        }
        else
        {
          break;
        }
      }
    }
    if (comparison == 0)
    {
      comparison = super.compareTo(other);
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getCardinality()
   */
  public final int getCardinality()
  {
    return cardinality;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getColumns()
   */
  public IndexColumn[] getColumns()
  {
    return columns.values().toArray(new IndexColumn[columns.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getPages()
   */
  public final int getPages()
  {
    return pages;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getType()
   */
  public final IndexType getType()
  {
    return type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#isUnique()
   */
  public boolean isUnique()
  {
    return isUnique;
  }

  void addColumn(final MutableIndexColumn column)
  {
    columns.add(column);
  }

  final void setCardinality(final int cardinality)
  {
    this.cardinality = cardinality;
  }

  final void setPages(final int pages)
  {
    this.pages = pages;
  }

  final void setType(final IndexType type)
  {
    this.type = type;
  }

  final void setUnique(final boolean unique)
  {
    isUnique = unique;
  }

}
