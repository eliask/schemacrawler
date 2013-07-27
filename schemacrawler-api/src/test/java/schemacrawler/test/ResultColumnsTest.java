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


import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.SchemaCrawlerUtility;

public class ResultColumnsTest
  extends BaseDatabaseTest
{

  private static final Logger LOGGER = Logger.getLogger(ResultColumnsTest.class
    .getName());

  @Test
  public void columns()
    throws Exception
  {

    final TestWriter out = new TestWriter();

    final String sql = ""
                       + "SELECT                                                                    "
                       + " PUBLIC.BOOKS.BOOKS.TITLE AS BOOK,                                        "
                       + " PUBLIC.BOOKS.AUTHORS.FIRSTNAME + ' ' + PUBLIC.BOOKS.AUTHORS.LASTNAME,    "
                       + " PUBLIC.BOOKS.BOOKS.PRICE                                                 "
                       + "FROM                                                                      "
                       + " PUBLIC.BOOKS.BOOKS                                                       "
                       + " INNER JOIN PUBLIC.BOOKS.BOOKAUTHORS                                      "
                       + "   ON PUBLIC.BOOKS.BOOKS.ID = PUBLIC.BOOKS.BOOKAUTHORS.BOOKID             "
                       + " INNER JOIN PUBLIC.BOOKS.AUTHORS                                          "
                       + "   ON PUBLIC.BOOKS.AUTHORS.ID = PUBLIC.BOOKS.BOOKAUTHORS.AUTHORID         ";

    try (final Connection connection = getConnection();
        final Statement statement = connection.createStatement();
        final ResultSet resultSet = statement.executeQuery(sql);)
    {

      final ResultsColumns resultColumns = SchemaCrawlerUtility
        .getResultColumns(resultSet);

      assertNotNull("Could not obtain result columns", resultColumns);
      final ResultsColumn[] columns = resultColumns.getColumns()
        .toArray(new ResultsColumn[0]);
      for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
      {
        final ResultsColumn column = columns[columnIdx];
        LOGGER.log(Level.FINE, column.toString());
        out.println("column: " + column.getFullName());
        out.println("  database type: "
                    + column.getColumnDataType().getDatabaseSpecificTypeName());
        out.println("  type: " + column.getColumnDataType().getTypeName());
      }
    }

    out.close();
    out.assertEquals(TestUtility.callingMethodFullName());
    ;
  }

}
