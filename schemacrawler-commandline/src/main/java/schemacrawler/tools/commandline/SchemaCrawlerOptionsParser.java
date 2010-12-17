/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class SchemaCrawlerOptionsParser
  extends BaseOptionsParser<SchemaCrawlerOptions>
{

  private final StringOption optionInfoLevel = new StringOption(Option.NO_SHORT_FORM,
                                                                "infolevel",
                                                                "standard");

  private final StringOption optionSchemas = new StringOption(Option.NO_SHORT_FORM,
                                                              "schemas",
                                                              InclusionRule.NONE);

  private final StringOption optionTableTypes = new StringOption(Option.NO_SHORT_FORM,
                                                                 "table_types",
                                                                 SchemaCrawlerOptions.DEFAULT_TABLE_TYPES);

  private final StringOption optionTables = new StringOption(Option.NO_SHORT_FORM,
                                                             "tables",
                                                             InclusionRule.ALL);
  private final StringOption optionExcludeColumns = new StringOption(Option.NO_SHORT_FORM,
                                                                     "excludecolumns",
                                                                     InclusionRule.NONE);

  private final StringOption optionProcedures = new StringOption(Option.NO_SHORT_FORM,
                                                                 "procedures",
                                                                 InclusionRule.ALL);
  private final StringOption optionExcludeProcedureColumns = new StringOption(Option.NO_SHORT_FORM,
                                                                              "excludeinout",
                                                                              InclusionRule.NONE);

  private final StringOption optionGrepColumns = new StringOption(Option.NO_SHORT_FORM,
                                                                  "grepcolumns",
                                                                  InclusionRule.NONE);
  private final StringOption optionGrepProcedureColumns = new StringOption(Option.NO_SHORT_FORM,
                                                                           "grepinout",
                                                                           InclusionRule.NONE);
  private final StringOption optionGrepDefinitions = new StringOption(Option.NO_SHORT_FORM,
                                                                      "grepdef",
                                                                      InclusionRule.NONE);
  private final BooleanOption optionGrepInvertMatch = new BooleanOption('v',
                                                                        "invert-match");

  private final BooleanOption optionSortTables = new BooleanOption(Option.NO_SHORT_FORM,
                                                                   "sorttables");
  private final BooleanOption optionSortColumns = new BooleanOption(Option.NO_SHORT_FORM,
                                                                    "sortcolumns");
  private final BooleanOption optionSortInout = new BooleanOption(Option.NO_SHORT_FORM,
                                                                  "sortinout");

  private final SchemaCrawlerOptions options;

  SchemaCrawlerOptionsParser(final String[] args, final Config config)
  {
    super(args);
    options = new SchemaCrawlerOptions(config);
  }

  @Override
  protected SchemaCrawlerOptions getOptions()
    throws SchemaCrawlerException
  {
    parse(new Option[] {
        optionInfoLevel,
        optionSchemas,
        optionTableTypes,
        optionTables,
        optionExcludeColumns,
        optionProcedures,
        optionExcludeProcedureColumns,
        optionGrepColumns,
        optionGrepProcedureColumns,
        optionGrepDefinitions,
        optionGrepInvertMatch,
        optionSortTables,
        optionSortColumns,
        optionSortInout,
    });

    if (optionInfoLevel.isFound())
    {
      try
      {
        final String infoLevel = optionInfoLevel.getValue();
        final SchemaInfoLevel schemaInfoLevel = InfoLevel.valueOf(infoLevel)
          .getSchemaInfoLevel();
        options.setSchemaInfoLevel(schemaInfoLevel);
      }
      catch (final IllegalArgumentException e)
      {
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
      }
    }
    else
    {
      throw new SchemaCrawlerException("No infolevel specified");
    }

    if (optionSchemas.isFound())
    {
      final InclusionRule schemaInclusionRule = new InclusionRule(optionSchemas.getValue(),
                                                                  InclusionRule.NONE);
      options.setSchemaInclusionRule(schemaInclusionRule);
    }

    if (optionTableTypes.isFound())
    {
      options.setTableTypes(optionTableTypes.getValue());
    }

    if (optionTables.isFound())
    {
      final InclusionRule tableInclusionRule = new InclusionRule(optionTables.getValue(),
                                                                 InclusionRule.NONE);
      options.setTableInclusionRule(tableInclusionRule);
    }
    if (optionExcludeColumns.isFound())
    {
      final InclusionRule columnInclusionRule = new InclusionRule(InclusionRule.ALL,
                                                                  optionExcludeColumns
                                                                    .getValue());
      options.setColumnInclusionRule(columnInclusionRule);
    }

    if (optionProcedures.isFound())
    {
      final InclusionRule procedureInclusionRule = new InclusionRule(optionProcedures
                                                                       .getValue(),
                                                                     InclusionRule.NONE);
      options.setProcedureInclusionRule(procedureInclusionRule);
    }
    if (optionExcludeProcedureColumns.isFound())
    {
      final InclusionRule procedureColumnInclusionRule = new InclusionRule(InclusionRule.ALL,
                                                                           optionExcludeProcedureColumns
                                                                             .getValue());
      options.setProcedureColumnInclusionRule(procedureColumnInclusionRule);
    }

    if (optionGrepInvertMatch.isFound())
    {
      options.setGrepInvertMatch(optionGrepInvertMatch.getValue());
    }

    if (optionGrepColumns.isFound())
    {
      final InclusionRule grepColumnInclusionRule = new InclusionRule(optionGrepColumns
                                                                        .getValue(),
                                                                      InclusionRule.NONE);
      options.setGrepColumnInclusionRule(grepColumnInclusionRule);
    }

    if (optionGrepProcedureColumns.isFound())
    {
      final InclusionRule grepProcedureColumnInclusionRule = new InclusionRule(optionGrepProcedureColumns
                                                                                 .getValue(),
                                                                               InclusionRule.NONE);
      options
        .setGrepProcedureColumnInclusionRule(grepProcedureColumnInclusionRule);
    }

    if (optionGrepDefinitions.isFound())
    {
      final InclusionRule grepDefinitionInclusionRule = new InclusionRule(optionGrepDefinitions
                                                                            .getValue(),
                                                                          InclusionRule.NONE);
      options.setGrepDefinitionInclusionRule(grepDefinitionInclusionRule);
    }

    if (optionSortColumns.isFound())
    {
      options.setAlphabeticalSortForTableColumns(optionSortColumns.getValue());
    }
    if (optionSortTables.isFound())
    {
      options.setAlphabeticalSortForTables(optionSortTables.getValue());
    }
    if (optionSortInout.isFound())
    {
      options
        .setAlphabeticalSortForProcedureColumns(optionSortInout.getValue());
    }

    return options;
  }
}
