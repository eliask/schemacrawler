/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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

package schemacrawler.tools.text.schema;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.DatabaseWithAssociations;
import schemacrawler.tools.analysis.lint.LintedDatabase;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaTextExecutable
  extends BaseExecutable
{

  private SchemaTextOptions schemaTextOptions;

  public SchemaTextExecutable(final String command)
  {
    super(command);
  }

  public final SchemaTextOptions getSchemaTextOptions()
  {
    final SchemaTextOptions schemaTextOptions;
    if (this.schemaTextOptions == null)
    {
      schemaTextOptions = new SchemaTextOptions(additionalConfiguration);
    }
    else
    {
      schemaTextOptions = this.schemaTextOptions;
    }
    return schemaTextOptions;
  }

  public final void setSchemaTextOptions(final SchemaTextOptions schemaTextOptions)
  {
    this.schemaTextOptions = schemaTextOptions;
  }

  private SchemaFormatter getDatabaseTraversalHandler()
    throws SchemaCrawlerException
  {
    final SchemaFormatter formatter;
    SchemaTextDetailType schemaTextDetailType;
    try
    {
      schemaTextDetailType = SchemaTextDetailType.valueOf(command);
    }
    catch (final IllegalArgumentException e)
    {
      schemaTextDetailType = SchemaTextDetailType.schema;
    }
    final SchemaTextOptions schemaTextOptions = getSchemaTextOptions();

    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == OutputFormat.json)
    {
      formatter = new SchemaJsonFormatter(schemaTextDetailType,
                                          schemaTextOptions,
                                          outputOptions);
    }
    else
    {
      formatter = new SchemaTextFormatter(schemaTextDetailType,
                                          schemaTextOptions,
                                          outputOptions);
    }

    return formatter;
  }

  @Override
  protected void executeOn(final Database db, final Connection connection)
    throws Exception
  {
    // Determine what decorators to apply to the database
    InfoLevel infoLevel;
    try
    {
      infoLevel = InfoLevel.valueOf(getSchemaCrawlerOptions()
        .getSchemaInfoLevel().getTag());
    }
    catch (final Exception e)
    {
      infoLevel = InfoLevel.unknown;
    }

    Database database = db;
    if (infoLevel.ordinal() >= InfoLevel.lint.ordinal())
    {
      database = new DatabaseWithAssociations(database);
      database = new LintedDatabase(database);
    }

    final SchemaFormatter formatter = getDatabaseTraversalHandler();

    formatter.begin();

    formatter.handleInfoStart();
    formatter.handle(database.getSchemaCrawlerInfo());
    formatter.handle(database.getDatabaseInfo());
    formatter.handle(database.getJdbcDriverInfo());
    formatter.handleInfoEnd();

    final List<ColumnDataType> columnDataTypes = new ArrayList<ColumnDataType>();
    final List<Table> tables = new ArrayList<Table>();
    final List<Procedure> procedures = new ArrayList<Procedure>();

    columnDataTypes.addAll(Arrays.asList(database.getSystemColumnDataTypes()));
    for (final Schema schema: database.getSchemas())
    {
      columnDataTypes.addAll(Arrays.asList(schema.getColumnDataTypes()));
      tables.addAll(Arrays.asList(schema.getTables()));
      procedures.addAll(Arrays.asList(schema.getProcedures()));
    }

    if (!columnDataTypes.isEmpty())
    {
      formatter.handleColumnDataTypesStart();
      for (final ColumnDataType columnDataType: columnDataTypes)
      {
        formatter.handle(columnDataType);
      }
      formatter.handleColumnDataTypesEnd();
    }

    if (!tables.isEmpty())
    {
      formatter.handleTablesStart();
      for (final Table table: tables)
      {
        formatter.handle(table);
      }
      formatter.handleTablesEnd();
    }

    if (!procedures.isEmpty())
    {
      formatter.handleProceduresStart();
      for (final Procedure procedure: procedures)
      {
        formatter.handle(procedure);
      }
      formatter.handleProceduresEnd();
    }

    formatter.end();

  }

}
