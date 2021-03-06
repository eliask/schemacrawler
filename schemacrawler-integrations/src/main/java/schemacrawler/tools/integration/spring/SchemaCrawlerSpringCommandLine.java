/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.integration.spring;


import java.io.File;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.executable.Executable;
import sf.util.ObjectToString;

public class SchemaCrawlerSpringCommandLine
  implements CommandLine
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerSpringCommandLine.class.getName());

  private final SpringOptions springOptions;

  public SchemaCrawlerSpringCommandLine(final String[] args)
    throws SchemaCrawlerException
  {
    final SpringOptionsParser springOptionsParser = new SpringOptionsParser();
    final String[] remainingArgs = springOptionsParser.parse(args);
    springOptions = springOptionsParser.getOptions();

    if (remainingArgs.length > 0)
    {
      throw new SchemaCrawlerException("Too many command line arguments provided: "
                                       + ObjectToString.toString(remainingArgs));
    }
  }

  @Override
  public void execute()
    throws Exception
  {
    final File contextFile = new File(springOptions.getContextFileName());
    final ApplicationContext appContext = new FileSystemXmlApplicationContext(contextFile
      .toURI().toString());

    final DataSource dataSource = (DataSource) appContext.getBean(springOptions
      .getDataSourceName());
    try (Connection connection = dataSource.getConnection();)
    {
      LOGGER.log(Level.INFO, "Opened database connection, " + connection);

      final Executable executable = (Executable) appContext
        .getBean(springOptions.getExecutableName());
      executable.execute(connection);
    }
  }

}
