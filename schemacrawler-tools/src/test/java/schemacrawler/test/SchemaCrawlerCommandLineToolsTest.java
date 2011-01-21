package schemacrawler.test;


import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.tools.commandline.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.utility.TestDatabase;

public class SchemaCrawlerCommandLineToolsTest
{

  private static final String INFO_LEVEL_OUTPUT = "info_level_output/";
  private static final String GREP_OUTPUT = "grep_output/";

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.startDatabase(true);
  }

  @Test
  public void compareInfoLevelOutput()
    throws Exception
  {
    final List<String> failures = new ArrayList<String>();
    for (final InfoLevel infoLevel: InfoLevel.values())
    {
      if (infoLevel == InfoLevel.unknown)
      {
        continue;
      }
      for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
        .values())
      {
        final File additionalProperties = File
          .createTempFile("hsqldb.INFORMATION_SCHEMA.config", ".properties");
        final Writer writer = new BufferedWriter(new FileWriter(additionalProperties));
        final Properties properties = new Properties();
        properties.load(SchemaCrawlerOutputTest.class
          .getResourceAsStream("/hsqldb.INFORMATION_SCHEMA.config.properties"));
        properties.store(writer, this.getClass().getName());

        final String referenceFile = schemaTextDetailType + "_" + infoLevel
                                     + ".txt";

        final File testOutputFile = File.createTempFile("schemacrawler."
                                                            + referenceFile
                                                            + ".",
                                                        ".test");
        testOutputFile.delete();

        final OutputFormat outputFormat = OutputFormat.text;
        Main.main(new String[] {
            "-driver=org.hsqldb.jdbc.JDBCDriver",
            "-url=jdbc:hsqldb:hsql://localhost/schemacrawler",
            "-user=sa",
            "-password=",
            "-g=" + additionalProperties.getAbsolutePath(),
            "-infolevel=" + infoLevel,
            "-command=" + schemaTextDetailType,
            "-outputformat=" + outputFormat,
            "-outputfile=" + testOutputFile.getAbsolutePath(),
        });

        failures.addAll(TestUtility.compareOutput(INFO_LEVEL_OUTPUT
                                                      + referenceFile,
                                                  testOutputFile,
                                                  outputFormat));
      }
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void grep()
    throws Exception
  {
    final List<String> failures = new ArrayList<String>();

    final String[][] grepArgs1 = new String[][] {
        new String[] {
            "-grepcolumns=.*\\.STREET|.*\\.PRICE", "-procedures=",
        }, new String[] {
            "-grepcolumns=.*\\..*NAME", "-procedures=",
        }, new String[] {
            "-grepdef=.*book authors.*", "-procedures=",
        }, new String[] {
            "-tables=", "-grepinout=.*\\.B_COUNT",
        }, new String[] {
            "-tables=", "-grepinout=.*\\.B_OFFSET",
        },
    };
    final String[][] grepArgs = new String[][] {
      new String[] {
          "-grepdef=.*book authors.*", "-procedures=",
      },
    };
    for (int i = 0; i < grepArgs.length; i++)
    {
      final String[] grepArgsForRun = grepArgs[i];

      final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.details;
      final InfoLevel infoLevel = InfoLevel.detailed;
      final File additionalProperties = File
        .createTempFile("hsqldb.INFORMATION_SCHEMA.config", ".properties");
      final Writer writer = new BufferedWriter(new FileWriter(additionalProperties));
      final Properties properties = new Properties();
      properties.load(SchemaCrawlerOutputTest.class
        .getResourceAsStream("/hsqldb.INFORMATION_SCHEMA.config.properties"));
      properties.store(writer, this.getClass().getName());

      final String referenceFile = String.format("grep%02d.txt", (i + 1));

      final File testOutputFile = File
        .createTempFile("schemacrawler." + referenceFile + ".", ".test");
      testOutputFile.delete();

      final OutputFormat outputFormat = OutputFormat.text;

      final List<String> args = new ArrayList<String>(Arrays.asList(new String[] {
          "-driver=org.hsqldb.jdbc.JDBCDriver",
          "-url=jdbc:hsqldb:hsql://localhost/schemacrawler",
          "-user=sa",
          "-password=",
          "-g=" + additionalProperties.getAbsolutePath(),
          "-infolevel=" + infoLevel,
          "-command=" + schemaTextDetailType,
          "-outputformat=" + outputFormat,
          "-outputfile=" + testOutputFile.getAbsolutePath(),
          "-noinfo",
          "-noheader"
      }));
      args.addAll(Arrays.asList(grepArgsForRun));

      Main.main(args.toArray(new String[args.size()]));

      failures.addAll(TestUtility.compareOutput(GREP_OUTPUT + referenceFile,
                                                testOutputFile,
                                                outputFormat));
    }

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }
}