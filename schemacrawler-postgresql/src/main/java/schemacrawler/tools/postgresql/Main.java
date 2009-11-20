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

package schemacrawler.tools.postgresql;


import schemacrawler.tools.main.SchemaCrawlerCommandLine;
import schemacrawler.tools.main.SchemaCrawlerMain;
import schemacrawler.tools.main.dbconnector.BundledDriverHelpOptions;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   */
  public static void main(final String[] args)
  {
    try
    {
      final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(args,
                                                                                new BundledDriverHelpOptions("SchemaCrawler for PostgreSQL",
                                                                                                             "/help/Connections.postgresql.txt"),
                                                                                "/schemacrawler-postgresql.config.properties");
      SchemaCrawlerMain.schemacrawler(commandLine);
    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }
  }

  private Main()
  {
    // Prevent instantiation
  }

}
