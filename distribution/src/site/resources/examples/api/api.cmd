@echo off
del /f /q *.class
javac -classpath ../schemacrawler-7.7.jar ApiExample.java
java -classpath ../schemacrawler-7.7.jar;../hsqldb.jar;. ApiExample
