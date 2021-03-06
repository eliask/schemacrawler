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

package schemacrawler.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;

public class SchemaCrawlerXmlOutputTest
  extends BaseDatabaseTest
{

  private static final String XML_OUTPUT = "xml_output/";

  @Test
  public void validCountXMLOutput()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();

    checkValidXmlOutput(SchemaTextDetailType.details.name(), failures);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void validXMLOutput()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();

    checkValidXmlOutput(Operation.count.name(), failures);
    checkValidXmlOutput(Operation.dump.name(), failures);
    checkValidXmlOutput(SchemaTextDetailType.list.name(), failures);
    checkValidXmlOutput(SchemaTextDetailType.schema.name(), failures);
    checkValidXmlOutput(SchemaTextDetailType.details.name(), failures);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private void checkValidXmlOutput(final String command,
                                   final List<String> failures)
    throws IOException, Exception, SchemaCrawlerException
  {
    final String referenceFile = command + ".html";
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.html.name(),
                                                          testOutputFile);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);

    final SchemaCrawlerOptions options = executable.getSchemaCrawlerOptions();
    options.setSchemaInfoLevel(SchemaInfoLevel.minimum());

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setAlphabeticalSortForTables(true);

    executable.setAdditionalConfiguration(textOptions.toConfig());
    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    failures.addAll(compareOutput(XML_OUTPUT + referenceFile,
                                  testOutputFile,
                                  OutputFormat.html.name()));
  }

}
