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

package schemacrawler.tools.integration.scripting;


import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.CommandChainExecutable;
import schemacrawler.tools.options.InputReader;
import schemacrawler.tools.options.OutputWriter;
import sf.util.FileUtility;
import sf.util.ObjectToString;

/**
 * Main executor for the scripting engine integration.
 * 
 * @author Sualeh Fatehi
 */
public final class ScriptExecutable
  extends BaseExecutable
{

  private static final Logger LOGGER = Logger.getLogger(ScriptExecutable.class
    .getName());

  static final String COMMAND = "script";

  public ScriptExecutable()
  {
    super(COMMAND);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeOn(final Database database,
                                 final Connection connection)
    throws Exception
  {

    final String scriptFileName = outputOptions.getOutputFormatValue();
    final File scriptFile = new File(scriptFileName);

    // Create a new instance of the engine
    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    final List<ScriptEngineFactory> engineFactories = scriptEngineManager
      .getEngineFactories();
    ScriptEngineFactory scriptEngineFactory = null;
    ScriptEngineFactory javaScriptEngineFactory = null;
    for (final ScriptEngineFactory engineFactory: engineFactories)
    {
      LOGGER.log(Level.FINER, String
        .format("Evaluating script engine: %s %s (%s %s)",
                engineFactory.getEngineName(),
                engineFactory.getEngineVersion(),
                engineFactory.getLanguageName(),
                engineFactory.getLanguageVersion()));
      final List<String> extensions = engineFactory.getExtensions();
      if (extensions.contains(FileUtility.getFileExtension(scriptFile)))
      {
        scriptEngineFactory = engineFactory;
        break;
      }
      if (engineFactory.getLanguageName().equalsIgnoreCase("JavaScript"))
      {
        javaScriptEngineFactory = engineFactory;
      }
    }
    if (scriptEngineFactory == null)
    {
      scriptEngineFactory = javaScriptEngineFactory;
    }
    if (scriptEngineFactory == null)
    {
      throw new SchemaCrawlerException("Script engine not found");
    }

    if (LOGGER.isLoggable(Level.CONFIG))
    {
      LOGGER
        .log(Level.CONFIG,
             String
               .format("Using script engine%n%s %s (%s %s)%nScript engine names: %s%nSupported file extensions: %s",
                       scriptEngineFactory.getEngineName(),
                       scriptEngineFactory.getEngineVersion(),
                       scriptEngineFactory.getLanguageName(),
                       scriptEngineFactory.getLanguageVersion(),
                       ObjectToString.toString(scriptEngineFactory.getNames()),
                       ObjectToString.toString(scriptEngineFactory
                         .getExtensions())));
    }

    final ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
    final CommandChainExecutable chain = new CommandChainExecutable();
    try (final Reader reader = new InputReader(outputOptions);
        final Writer writer = new OutputWriter(outputOptions);)
    {
      // Set up the context
      scriptEngine.getContext().setWriter(writer);
      scriptEngine.put("database", database);
      scriptEngine.put("connection", connection);
      scriptEngine.put("chain", chain);

      // Evaluate the script
      if (scriptEngine instanceof Compilable)
      {
        final CompiledScript script = ((Compilable) scriptEngine)
          .compile(reader);
        script.eval();
      }
      else
      {
        scriptEngine.eval(reader);
      }
    }

  }
}
