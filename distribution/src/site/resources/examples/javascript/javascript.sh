java -classpath ../schemacrawler-7.5.1.jar:../hsqldb.jar schemacrawler.tools.integration.scripting.Main -g schemacrawler.config.properties -c hsqldb -command verbose_schema -outputformat $1