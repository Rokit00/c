@set SVC_NAME_KRCV=KUMHO_CITI_SFTP_RCV_SAP
@set SVC_NAME_JSVR=KUMHO_CITI_SFTP_JCO_SVR

@set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_251\bin
@set JRE_HOME=C:\Program Files\Java\jdk1.8.0_251\jre\bin
@set INSTALL_HOME=C:\Biznbank\CITI
@set JAVASERVICE=%INSTALL_HOME%\JavaService-2.0.10\JavaService_64bit.exe
@set CONIFG_FILE=%INSTALL_HOME%\conf\config.ini

@set CLASSPATH=%INSTALL_HOME%;%INSTALL_HOME%/lib/sapjco3.jar;%INSTALL_HOME%/lib/jsch-0.1.55.jar;%INSTALL_HOME%/lib/bcpg-jdk15on-150.jar;%INSTALL_HOME%/lib/bcprov-jdk15on-150.jar

@%JAVASERVICE% -install %SVC_NAME_KRCV% "%JRE_HOME%"/server/jvm.dll -Djava.class.path=%CLASSPATH% -start ksnet.sap.KsnetRcv -params %CONIFG_FILE% -out %INSTALL_HOME%/out.log -err %INSTALL_HOME%/err.log -description "KUMHO CITI SFTP RCV"
@%JAVASERVICE% -install %SVC_NAME_JSVR% "%JRE_HOME%"/server/jvm.dll -Djava.class.path=%CLASSPATH% -start ksnet.sap.KsnetJcoSvr -params %CONIFG_FILE% -out %INSTALL_HOME%/out.log -err %INSTALL_HOME%/err.log -description "KUMHO CIT SFTP JCO SVR"

@pause

