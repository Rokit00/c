#!/usr/bin/sh

JAVA_HOME=/usr/java6
INSTALL_HOME=`pwd`
CONFIG_FILE=$INSTALL_HOME/conf/config.ini

CLASSPATH=$INSTALL_HOME:$INSTALL_HOME/lib/sapjco.jar

nohup $JAVA_HOME/bin/java -DKSNET_BT_RCV_SAP -classpath $CLASSPATH ksnet.sap.KsnetRcv $CONFIG_FILE & 
nohup $JAVA_HOME/bin/java -DKSNET_BT_JCO_SVR -classpath $CLASSPATH ksnet.sap.KsnetJcoSvr $CONFIG_FILE & 
