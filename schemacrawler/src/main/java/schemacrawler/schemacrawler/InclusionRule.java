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

package schemacrawler.schemacrawler;


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Specifies inclusion and exclusion patterns that can be applied to the
 * names of database objects.
 * 
 * @author Sualeh Fatehi
 */
public final class InclusionRule
  implements Serializable
{

  /** Exclude nothing */
  public static final String NONE = "";
  /** Include everything. */
  public static final String ALL = ".*";

  /** Exclude nothing */
  private static final Pattern NONE_PATTERN = Pattern.compile(NONE);
  /** Include everything. */
  private static final Pattern ALL_PATTERN = Pattern.compile(ALL);

  private static final Logger LOGGER = Logger.getLogger(InclusionRule.class
    .getName());

  private static final long serialVersionUID = 3443758881974362293L;

  /** Exclude all, include none. */
  public static final InclusionRule EXCLUDE_ALL_RULE = new InclusionRule(NONE_PATTERN,
                                                                         ALL_PATTERN);
  /** Include all. */
  public static final InclusionRule INCLUDE_ALL_RULE = new InclusionRule(ALL_PATTERN,
                                                                         NONE_PATTERN);

  private final Pattern patternInclude;
  private final Pattern patternExclude;

  /**
   * Set include and exclude patterns.
   * 
   * @param patternInclude
   *        Inclusion pattern
   * @param patternExclude
   *        Exclusion pattern
   */
  public InclusionRule(final Pattern patternInclude,
                       final Pattern patternExclude)
  {
    this.patternInclude = patternInclude;
    this.patternExclude = patternExclude;
  }

  /**
   * Set include and exclude patterns.
   * 
   * @param patternInclude
   *        Inclusion pattern
   * @param patternExclude
   *        Exclusion pattern
   */
  public InclusionRule(final String patternInclude, final String patternExclude)
  {
    this(Pattern.compile(patternInclude), Pattern.compile(patternExclude));
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
    final InclusionRule other = (InclusionRule) obj;
    if (patternExclude == null)
    {
      if (other.patternExclude != null)
      {
        return false;
      }
    }
    else if (!patternExclude.equals(other.patternExclude))
    {
      return false;
    }
    if (patternInclude == null)
    {
      if (other.patternInclude != null)
      {
        return false;
      }
    }
    else if (!patternInclude.equals(other.patternInclude))
    {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (patternExclude == null? 0: patternExclude.hashCode());
    result = prime * result
             + (patternInclude == null? 0: patternInclude.hashCode());
    return result;
  }

  /**
   * Checks whether to add a named object after considering the include
   * and exclude patterns.
   * 
   * @param name
   *        Name to check
   * @return Whether the name should be included or not
   */
  public boolean include(final String name)
  {
    boolean include = false;
    if (!(name == null || name.trim().length() == 0))
    {
      if (!patternInclude.matcher(name).matches())
      {
        LOGGER
          .log(Level.FINE, "Excluding " + name
                           + " since it does not match the include pattern");
      }
      else if (patternExclude.matcher(name).matches())
      {
        LOGGER.log(Level.FINE, "Excluding " + name
                               + " since it matches the exclude pattern");
      }
      else
      {
        LOGGER.log(Level.FINE, "Including " + name);
        include = true;
      }
    }
    return include;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("InclusionRule[");
    buffer.append("patternInclude=").append(patternInclude.pattern());
    buffer.append(", patternExclude=").append(patternExclude.pattern());
    buffer.append("]");
    return buffer.toString();
  }

}
