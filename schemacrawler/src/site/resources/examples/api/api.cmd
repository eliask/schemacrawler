@echo off
del /f /q *.class
javac -classpath ..\_schemacrawler\lib\schemacrawler-8.17.jar ApiExample.java
java -classpath ..\_schemacrawler\lib\schemacrawler-8.17.jar;..\_schemacrawler\lib\hsqldb-2.2.8.jar;. ApiExample
