@java -classpath ..\_schemacrawler\lib\schemacrawler-8.9.jar;..\_schemacrawler\lib\hsqldb-2.2.4.jar schemacrawler.Main -g schemacrawler.config.properties -c hsqldb -infolevel=standard -command script -infolevel=maximum -sorttables=false -outputformat %1