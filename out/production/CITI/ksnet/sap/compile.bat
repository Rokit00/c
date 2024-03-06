@set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_251\bin

@set CLASSPATH=../lib/sapjco3.jar;../lib/jsch-0.1.55.jar;../lib/bcpg-jdk15on-150.jar;../lib/bcprov-jdk15on-150.jar

"%JAVA_HOME%"\javac -d .. -classpath %CLASSPATH% *.java
