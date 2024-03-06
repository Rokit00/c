#!/usr/bin/sh
ps -ef|grep KSNET_BT_RCV_SAP |grep -v grep|awk '{print $2}'|xargs kill
ps -ef|grep KSNET_BT_JCO_SVR |grep -v grep|awk '{print $2}'|xargs kill
