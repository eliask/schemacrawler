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

package schemacrawler.test;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class SchemaCrawlerTextCommandsOutputTest
  extends BaseDatabaseTest
{

  private static final String COMMAND_OUTPUT = "command_output/";

  @Test
  public void countOutput()
    throws Exception
  {
    textOutputTest(Operation.count.name(), new Config());
  }

  @Test
  public void dumpOutput()
    throws Exception
  {
    textOutputTest(Operation.dump.name(), new Config());
  }

  @Test
  public void queryOutput()
    throws Exception
  {
    final String queryCommand = "all_tables";
    final Config config = new Config();
    config
      .put(queryCommand,
           "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES ORDER BY TABLE_SCHEM, TABLE_NAME");

    textOutputTest(queryCommand, config);
  }

  @Test
  public void queryOverOutput()
    throws Exception
  {
    final String queryCommand = "dump_tables";
    final Config config = new Config();
    config
      .put(queryCommand,
           "SELECT ${orderbycolumns} FROM ${table} ORDER BY ${orderbycolumns}");

    textOutputTest(queryCommand, config);
  }

  @Test
  public void schemaOutput()
    throws Exception
  {
    textOutputTest(SchemaTextDetailType.list.name(), new Config());
  }

  @Test
  public void streamedOutput()
    throws Exception
  {
    final String command = SchemaTextDetailType.list.name();

    final String referenceFile = command + ".txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();
    assertTrue(!testOutputFile.exists());

    final File dummyOutputFile = new File(System.getProperty("java.io.tmpdir"),
                                          "dummy.txt");
    dummyOutputFile.delete();
    assertTrue(!dummyOutputFile.exists());

    final Writer writer = new PrintWriter(testOutputFile, "UTF-8");
    final OutputOptions outputOptions = new OutputOptions(OutputFormat.text.name(),
                                                          dummyOutputFile);
    outputOptions.setWriter(writer);

    final BaseTextOptionsBuilder baseTextOptions = new BaseTextOptionsBuilder();
    baseTextOptions.hideInfo();
    baseTextOptions.hideHeader();
    baseTextOptions.hideFooter();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(baseTextOptions.toConfig());
    executable.execute(getConnection());

    writer.close();
    assertTrue(!dummyOutputFile.exists());

    final List<String> failures = compareOutput(COMMAND_OUTPUT + referenceFile,
                                                testOutputFile,
                                                outputOptions.getOutputFormat()
                                                  .name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private void textOutputTest(final String command, final Config config)
    throws Exception
  {
    final String referenceFile = command + ".txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.text.name(),
                                                          testOutputFile);

    final BaseTextOptionsBuilder baseTextOptions = new BaseTextOptionsBuilder();
    baseTextOptions.hideInfo();
    baseTextOptions.hideHeader();
    baseTextOptions.hideFooter();
    config.putAll(baseTextOptions.toConfig());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setAdditionalConfiguration(config);
    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    final List<String> failures = compareOutput(COMMAND_OUTPUT + referenceFile,
                                                testOutputFile,
                                                outputOptions.getOutputFormat()
                                                  .name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }
}
