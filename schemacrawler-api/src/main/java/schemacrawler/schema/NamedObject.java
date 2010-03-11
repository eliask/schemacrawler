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

package schemacrawler.schema;


import java.io.Serializable;
import java.util.Map;

/**
 * Represents a named object.
 * 
 * @author Sualeh Fatehi
 */
public interface NamedObject
  extends Serializable, Comparable<NamedObject>
{

  /**
   * Gets an attribute.
   * 
   * @param name
   *        Attribute name.
   * @return Attribute value.
   */
  Object getAttribute(String name);

  /**
   * Gets an attribute.
   * 
   * @param name
   *        Attribute name.
   * @return Attribute value.
   */
  <T> T getAttribute(String name, T defaultValue);

  /**
   * Gets all attributes.
   * 
   * @return Map of attributes
   */
  Map<String, Object> getAttributes();

  /**
   * Getter for fully qualified name of object.
   * 
   * @return Fully qualified of the object
   */
  String getFullName();

  /**
   * Getter for name of object.
   * 
   * @return Name of the object
   */
  String getName();

  /**
   * Getter for remarks.
   * 
   * @return Remarks
   */
  String getRemarks();

  /**
   * Sets an attribute.
   * 
   * @param name
   *        Attribute name
   * @param value
   *        Attribute value
   */
  void setAttribute(String name, Object value);

}
