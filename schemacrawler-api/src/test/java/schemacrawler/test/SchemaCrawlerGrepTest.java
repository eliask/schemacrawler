/*
 * SchemaCrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Test;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;

public class SchemaCrawlerGrepTest
  extends BaseDatabaseTest
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerGrepTest.class.getName());

  @Test
  public void grepColumns()
    throws Exception
  {
    final TestWriter out = new TestWriter();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\..*\\.BOOKID", ""));

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      out.println("schema: " + schema.getFullName());
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        out.println("  table: " + table.getFullName());
        final Column[] columns = table.getColumns().toArray(new Column[0]);
        Arrays.sort(columns);
        for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
        {
          final Column column = columns[columnIdx];
          out.println("    column: " + column.getFullName());
        }
      }
    }

    out.close();
    out.assertEquals(TestUtility.currentMethodFullName());
  }

  @Test
  public void grepColumnsAndIncludeChildTables()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.BOOKAUTHORS\\..*", ""));

    Database database;
    Schema schema;
    Table table;

    database = getDatabase(schemaCrawlerOptions);
    schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull("Schema PUBLIC.BOOKS not found", schema);
    assertEquals(1, database.getTables(schema).size());
    table = database.getTable(schema, "BOOKAUTHORS");
    assertNotNull("Table BOOKAUTHORS not found", table);

    schemaCrawlerOptions.setParentTableFilterDepth(1);
    database = getDatabase(schemaCrawlerOptions);
    schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull("Schema PUBLIC.BOOKS not found", schema);
    assertEquals(3, database.getTables(schema).size());
    table = database.getTable(schema, "BOOKAUTHORS");
    assertNotNull("Table BOOKAUTHORS not found", table);
    table = database.getTable(schema, "BOOKS");
    assertNotNull("Table BOOKS not found", table);
    table = database.getTable(schema, "AUTHORS");
    assertNotNull("Table AUTHORS not found", table);

  }

  @Test
  public void grepCombined()
    throws Exception
  {
    final TestWriter out = new TestWriter();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\..*\\.BOOKID", ""));
    schemaCrawlerOptions
      .setGrepDefinitionInclusionRule(new InclusionRule(".*book author.*", ""));

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      out.println("schema: " + schema.getFullName());
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        out.println("  table: " + table.getFullName());
        final Column[] columns = table.getColumns().toArray(new Column[0]);
        Arrays.sort(columns);
        for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
        {
          final Column column = columns[columnIdx];
          out.println("    column: " + column.getFullName());
        }
      }
    }

    out.close();
    out.assertEquals(TestUtility.currentMethodFullName());
  }

  @Test
  public void grepDefinitions()
    throws Exception
  {
    final TestWriter out = new TestWriter();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepDefinitionInclusionRule(new InclusionRule(".*book author.*", ""));

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      out.println("schema: " + schema.getFullName());
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        out.println("  table: " + table.getFullName());
        final Column[] columns = table.getColumns().toArray(new Column[0]);
        Arrays.sort(columns);
        for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
        {
          final Column column = columns[columnIdx];
          out.println("    column: " + column.getFullName());
        }
      }
    }

    out.close();
    out.assertEquals(TestUtility.currentMethodFullName());
  }

  @Test
  public void grepProcedures()
    throws Exception
  {
    final TestWriter out = new TestWriter();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepRoutineColumnInclusionRule(new InclusionRule(".*\\.B_COUNT", ""));

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      out.println("schema: " + schema.getFullName());
      final Routine[] routines = database.getRoutines(schema)
        .toArray(new Routine[0]);
      for (int routineIdx = 0; routineIdx < routines.length; routineIdx++)
      {
        final Routine routine = routines[routineIdx];
        out.println("  routine: " + routine.getFullName());
        final ProcedureColumn[] columns = routine.getColumns()
          .toArray(new ProcedureColumn[0]);
        for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
        {
          final ProcedureColumn column = columns[columnIdx];
          out.println("    parameter: " + column.getFullName());
        }
      }
    }

    out.close();
    out.assertEquals(TestUtility.currentMethodFullName());

  }

}
