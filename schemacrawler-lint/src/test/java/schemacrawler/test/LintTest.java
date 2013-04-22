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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import org.junit.Test;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintedDatabase;
import schemacrawler.tools.lint.LinterConfigs;

public class LintTest
  extends BaseDatabaseTest
{

  private static final String LINTS_OUTPUT = "lints_output/";

  @Test
  public void lints()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(".*FOR_LINT",
                                                InclusionRule.NONE));

    final Database database = getDatabase(schemaCrawlerOptions);
    assertNotNull(database);
    assertEquals(1, database.getSchemas().size());
    final Schema schema = database.getSchema("PUBLIC.FOR_LINT");
    assertNotNull("FOR_LINT schema not found", schema);
    assertEquals("FOR_LINT tables not found", 5, database.getTables(schema)
      .size());

    final LintedDatabase lintedDatabase = new LintedDatabase(database,
                                                             new LinterConfigs());
    final LintCollector lintCollector = lintedDatabase.getCollector();
    assertEquals(25, lintCollector.size());

    final File testOutputFile = File.createTempFile("schemacrawler.lints.",
                                                    ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {
      for (final Lint<?> lint: lintCollector)
      {
        writer.println(lint);
      }
    }

    final List<String> failures = TestUtility
      .compareOutput(LINTS_OUTPUT + "schemacrawler.lints.txt", testOutputFile);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
