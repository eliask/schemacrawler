@echo off
del /f /q *.class
javac -classpath ../schemacrawler-7.2.jar ApiExample.java
java -classpath ../schemacrawler-7.2.jar;../hsqldb.jar;. ApiExample
