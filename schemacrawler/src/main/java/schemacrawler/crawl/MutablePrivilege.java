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

package schemacrawler.crawl;


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Privilege;

/**
 * Represents a privilege of a table or column.
 * 
 * @author Sualeh Fatehi
 */
final class MutablePrivilege
  extends AbstractDependantObject
  implements Privilege
{

  private static final long serialVersionUID = -1117664231494271886L;

  private String grantor;
  private String grantee;
  private boolean isGrantable;

  MutablePrivilege(final DatabaseObject parent, final String name)
  {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Privilege#getGrantee()
   */
  public String getGrantee()
  {
    return grantee;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Privilege#getGrantor()
   */
  public String getGrantor()
  {
    return grantor;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Privilege#isGrantable()
   */
  public boolean isGrantable()
  {
    return isGrantable;
  }

  void setGrantable(final boolean grantable)
  {
    isGrantable = grantable;
  }

  void setGrantee(final String grantee)
  {
    this.grantee = grantee;
  }

  void setGrantor(final String grantor)
  {
    this.grantor = grantor;
  }

}
