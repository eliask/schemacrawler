/*
 * SchemaCrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.execute;


import java.sql.ResultSet;

import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Handler for SQL executor.
 */
public interface DataHandler
{

  /**
   * Handles the begin of the execution.
   * 
   * @throws QueryExecutorException
   *         On an exception
   */
  void begin()
    throws SchemaCrawlerException;

  /**
   * Handles the end of the execution.
   * 
   * @throws QueryExecutorException
   *         On an exception
   */
  void end()
    throws SchemaCrawlerException;

  /**
   * Handles information on the database schema.
   * 
   * @param database
   *        Database information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(DatabaseInfo database)
    throws SchemaCrawlerException;

  /**
   * Handles actual data.
   * 
   * @param rows
   *        Data from the execution.
   * @throws QueryExecutorException
   *         On an exception
   */
  void handleData(final String title, final ResultSet rows)
    throws SchemaCrawlerException;

}
