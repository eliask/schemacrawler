import java.util.Properties;

import javax.sql.DataSource;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;
import schemacrawler.utility.datasource.PropertiesDataSource;

public final class ApiExample
{

  public static void main(final String[] args)
    throws Exception
  {
    // Create a database connection
    final DataSource dataSource = makeDataSource();

    // Create the options
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setShowStoredProcedures(false);
    options.setAlphabeticalSortForTableColumns(true);

    // Get the schema definition
    final Database database = SchemaCrawlerUtility.getDatabase(dataSource
      .getConnection(), options);

    for (final Catalog catalog: database.getCatalogs())
    {
      for (final Schema schema: catalog.getSchemas())
      {
        System.out.println(catalog + "." + schema);
        for (final Table table: schema.getTables())
        {
          System.out.print("o--> " + table);
          if (table instanceof View)
          {
            System.out.println(" (VIEW)");
          }
          else
          {
            System.out.println();
          }

          for (final Column column: table.getColumns())
          {
            System.out.println("     o--> " + column + " (" + column.getType()
                               + ")");
          }
        }
      }
    }

  }

  private static DataSource makeDataSource()
  {
    final String datasourceName = "schemacrawler";

    final Properties connectionProperties = new Properties();
    connectionProperties.setProperty(datasourceName + ".driver",
                                     "org.hsqldb.jdbcDriver");
    connectionProperties
      .setProperty(datasourceName + ".url",
                   "jdbc:hsqldb:hsql://localhost:9001/schemacrawler");
    connectionProperties.setProperty(datasourceName + ".user", "sa");
    connectionProperties.setProperty(datasourceName + ".password", "");

    return new PropertiesDataSource(connectionProperties, datasourceName);
  }

}
