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
package schemacrawler.integration.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.integration.spring.Main;
import schemacrawler.tools.options.OutputFormat;

public class SpringIntegrationCommandLineTest
  extends BaseDatabaseTest
{

  @Test
  public void springCommandLine()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();

    final File contextFile = copyResourceToTempFile("/context.xml");

    final String executableName = "executableForSchema";
    final String referenceFile = executableName + ".txt";
    final File testOutputFile = new File("scOutput.txt");
    testOutputFile.delete();

    final OutputFormat outputFormat = OutputFormat.text;
    Main.main(new String[] {
        "-c", contextFile.getAbsolutePath(), "-x=" + executableName,
    });

    failures.addAll(compareOutput(referenceFile,
                                  testOutputFile,
                                  outputFormat.name()));
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }
}
