java -classpath ../schemacrawler-7.6.jar:../hsqldb.jar schemacrawler.tools.integration.scripting.Main -g schemacrawler.config.properties -c hsqldb -command verbose_schema -outputformat $1