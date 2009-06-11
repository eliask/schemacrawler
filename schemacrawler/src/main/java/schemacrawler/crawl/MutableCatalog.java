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


import schemacrawler.schema.Catalog;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;

/**
 * Represents the database.
 * 
 * @author Sualeh Fatehi
 */
class MutableCatalog
  extends AbstractNamedObject
  implements Catalog
{

  private static final long serialVersionUID = 3258128063743931187L;

  private final Database database;
  private final NamedObjectList<MutableSchema> schemas = new NamedObjectList<MutableSchema>(NamedObjectSort.alphabetical);

  MutableCatalog(final Database database, final String name)
  {
    super(name);
    this.database = database;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Catalog#getDatabase()
   */
  public Database getDatabase()
  {
    return database;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getSchema(java.lang.String)
   */
  public Schema getSchema(final String name)
  {
    return lookupSchema(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getSchemas()
   */
  public Schema[] getSchemas()
  {
    return schemas.getAll().toArray(new Schema[schemas.size()]);
  }

  void addSchema(final MutableSchema schema)
  {
    schemas.add(schema);
  }

  MutableSchema lookupSchema(final String name)
  {
    return schemas.lookup(name);
  }

}
