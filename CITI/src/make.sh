#!/usr/bin/sh
JAVA_HOME=/usr/java6
CLASSPATH=../lib/sapjco3.jar

$JAVA_HOME/bin/javac -d .. -classpath $CLASSPATH *.java