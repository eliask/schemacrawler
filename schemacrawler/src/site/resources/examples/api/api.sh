rm -f *.class
javac -classpath ../_schemacrawler/lib/schemacrawler-9.1.jar ApiExample.java
java -classpath ../_schemacrawler/lib/schemacrawler-9.1.jar:../_schemacrawler/lib/hsqldb-2.2.8.jar:. ApiExample
