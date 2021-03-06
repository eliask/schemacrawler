package schemacrawler.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class SchemaCrawlerCommandLineToolsTest
  extends BaseDatabaseTest
{

  private static final String INFO_LEVEL_OUTPUT = "info_level_output/";
  private static final String GREP_OUTPUT = "grep_output/";

  @Test
  public void compareInfoLevelOutput()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();

    final File additionalProperties = copyResourceToTempFile("/hsqldb.INFORMATION_SCHEMA.config.properties");

    for (final InfoLevel infoLevel: InfoLevel.values())
    {
      if (infoLevel == InfoLevel.unknown)
      {
        continue;
      }
      for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
        .values())
      {

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

        failures.addAll(compareOutput(INFO_LEVEL_OUTPUT + referenceFile,
                                      testOutputFile,
                                      outputFormat.name()));
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
    final List<String> failures = new ArrayList<>();

    final String[][] grepArgs = new String[][] {
        new String[] {
            "-grepcolumns=.*\\.STREET|.*\\.PRICE", "-routines=",
        },
        new String[] {
            "-grepcolumns=.*\\..*NAME", "-routines=",
        },
        new String[] {
            "-grepdef=.*book authors.*", "-routines=",
        },
        new String[] {
            "-tables=", "-grepinout=.*\\.B_COUNT",
        },
        new String[] {
            "-tables=", "-grepinout=.*\\.B_OFFSET",
        },
        new String[] {
            "-grepcolumns=.*\\.STREET|.*\\.PRICE",
            "-grepdef=.*book authors.*",
            "-routines=",
        },
    };
    for (int i = 0; i < grepArgs.length; i++)
    {
      final String[] grepArgsForRun = grepArgs[i];

      final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.details;
      final InfoLevel infoLevel = InfoLevel.detailed;
      final File additionalProperties = File
        .createTempFile("hsqldb.INFORMATION_SCHEMA.config", ".properties");
      final Writer writer = new PrintWriter(additionalProperties, "UTF-8");
      final Properties properties = new Properties();
      properties.load(this.getClass()
        .getResourceAsStream("/hsqldb.INFORMATION_SCHEMA.config.properties"));
      properties.store(writer, this.getClass().getName());

      final String referenceFile = String.format("grep%02d.txt", i + 1);

      final File testOutputFile = File
        .createTempFile("schemacrawler." + referenceFile + ".", ".test");
      testOutputFile.delete();

      final OutputFormat outputFormat = OutputFormat.text;

      final List<String> args = new ArrayList<>(Arrays.asList(new String[] {
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
      }));
      args.addAll(Arrays.asList(grepArgsForRun));

      Main.main(args.toArray(new String[args.size()]));

      failures.addAll(compareOutput(GREP_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.name()));
    }

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }
}
