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
package schemacrawler.tools.executable;


import java.io.File;
import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;

public final class CommandChainExecutable
  extends BaseCommandChainExecutable
{

  private static final String COMMAND = "chain";

  public CommandChainExecutable()
    throws SchemaCrawlerException
  {
    super(COMMAND);
  }

  public final Executable addNext(final String command,
                                  final String outputFormat,
                                  final String outputFileName)
    throws SchemaCrawlerException
  {
    try
    {
      final Executable executable = addNext(command);
      if (executable != null)
      {
        final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                              new File(outputFileName));
        executable.setOutputOptions(outputOptions);
      }
      return executable;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException(String.format("Cannot chain executable, unknown command, %s - %s - %s",
                                                     command,
                                                     outputFormat,
                                                     outputFileName));
    }
  }

  @Override
  public void executeOn(final Database database, final Connection connection)
    throws Exception
  {
    executeChain(database, connection);
  }

}
