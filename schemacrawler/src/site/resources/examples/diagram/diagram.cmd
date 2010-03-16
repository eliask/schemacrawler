@echo off
del /q /f database-diagram.pdf
java -classpath ../schemacrawler-8.2-SNAPSHOT.jar;../hsqldb.jar schemacrawler.Main -c hsqldb -infolevel=lint -command=graph -outputformat=pdf -outputfile=database-diagram.pdf
echo Database diagram is in database-diagram.pdf