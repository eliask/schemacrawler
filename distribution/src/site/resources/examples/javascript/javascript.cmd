@java -classpath ../schemacrawler-8.1.jar;../hsqldb.jar schemacrawler.Main -g schemacrawler.config.properties -c hsqldb -command script -infolevel=maximum -sorttables=false -outputformat %1