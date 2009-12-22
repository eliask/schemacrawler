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

package schemacrawler.tools.text.operation;


import java.sql.Connection;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.Executable;
import schemacrawler.tools.ExecutionException;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.text.base.Crawler;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public final class OperationExecutable
  extends Executable<OperationOptions>
{

  /**
   * {@inheritDoc}
   * <p>
   * Operations are crawl handlers that rely on query execution and
   * result set formatting. A connection is needed for the schema
   * crawling, and for executing the query. "Query-over" queries are
   * executed once per table, after variables are substituted. Other
   * queries are executed just once.
   * 
   * @see schemacrawler.tools.Executable#execute(Connection)
   */
  @Override
  public void execute(final Connection connection)
    throws ExecutionException
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }
    adjustSchemaInfoLevel();

    try
    {
      final OperationHandler handler = new OperationHandler(toolOptions,
                                                            connection);

      final SchemaCrawler schemaCrawler = new SchemaCrawler(connection);
      final Database database = schemaCrawler.crawl(schemaCrawlerOptions);
      final Crawler crawler = new Crawler(database);
      crawler.crawl(handler);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new ExecutionException("Could not execute operation", e);
    }
  }

  @Override
  public void initializeToolOptions(final String command,
                                    final Config config,
                                    final OutputOptions outputOptions)
    throws ExecutionException
  {

    Operation operation;
    OperationOptions operationOptions;
    try
    {
      operation = Operation.valueOf(command);
      operationOptions = new OperationOptions(config, outputOptions, operation);
    }
    catch (final IllegalArgumentException e)
    {
      final String queryName = command;
      operationOptions = new OperationOptions(config, outputOptions, queryName);
    }
    toolOptions = operationOptions;
  }

}
