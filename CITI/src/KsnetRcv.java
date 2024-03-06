package ksnet.sap;

import java.io.*;
import java.net.*;
import java.util.*;

import java.sql.*;
import java.time.*;  
import java.time.temporal.ChronoUnit;  

public class KsnetRcv extends Thread{
	public static final long 	SLEEP_TIME	= 30*1000L;
	public static String 		Encoding 	= null;
	public static LocalTime startTime = LocalTime.now();
	public static LocalTime endTime = LocalTime.now();

	public static void main(String[] args) throws Exception {
		CUtil.setConfig(args[0]);

		String	recvDir	= CUtil.get("RECV_DIR");
		String	sendDir	= CUtil.get("SEND_DIR");
		String	usedDir	= CUtil.get("USED_DIR");
		String	errDir	= CUtil.get("ERR_DIR");
		String	SftpRecvDir	= CUtil.get("SFTP_RECV_DIR");
		String	SftpSendDir	= CUtil.get("SFTP_SEND_DIR");
		String	PgpPrivateKey	= CUtil.get("PGP_PRIVATEKEY");
		String	PgpPrivatePasswd	= CUtil.get("PGP_PASSWD");
		String liveInterval = CUtil.get("LIVE_INTERVAL");
		String	SftpRecvOrgDir	= "" ;
		String	SftpRecvTargetDir	= "" ;
		
		long	curr_sec ;
		long	time_gap ;
		boolean rtn;
		
		Encoding = CUtil.get("ENCODING");
		if (Encoding == null || Encoding.length() == 0) Encoding = "ksc5601"; 


    HttpApiUtil.httpApiSend("DAEMON", "DAEMON", "START", "DAEMON START", "BOA DAEMON START");
		LUtil.println("BRCV", "KsnetSndBt start");
	
		//test
		LUtil.println("BRCV", "recvDir:["+recvDir+"], usedDir : ["+usedDir+"], errDir : ["+errDir+"]");
		
		new KSFPETimer().start();

    while(true){
    	try{

				/* ���������� �α��ۼ�  ���� */
				endTime = LocalTime.now();
				Duration duration = Duration.between(startTime, endTime);
				
				if (duration.isNegative())
				{
					startTime = LocalTime.now();
				}
				else {
					if (Integer.parseInt(liveInterval) <= duration.getSeconds())
					{
						startTime = LocalTime.now();
						HttpApiUtil.httpApiSend("DAEMON", "DAEMON", "LIVE", "Proceeding", "BOA DAEMON Proceeding");
						LUtil.println("BRCV", "CNB ALIVE");
					}
				}
				/* ���������� �α��ۼ�  �� */

    		if (SFTPRecvUtil.init())
    		{
					SftpRecvOrgDir	= CUtil.get("SFTP_RECV_DIR") ;
					SftpRecvTargetDir	=  CUtil.get("SFTP_RECV_DIR") + "backup/" ;
					
					LUtil.println("BRCV", "SFTP Recv Start..." + SftpRecvOrgDir);
    			SFTPRecvUtil.multiDownload(SftpRecvOrgDir, SftpRecvTargetDir, recvDir);

    			SFTPRecvUtil.disconnection();
					LUtil.println("BRCV", "SFTP Recv End...");
    		}
				LUtil.println("BRCV", "Recv File Check...");

    		String[] fileNames = LUtil.getFileList(recvDir);
    		
    		//test
    		if(fileNames != null && fileNames.length > 0 ){
					for(int i=0; i<fileNames.length; i++){
						
						curr_sec	= System.currentTimeMillis();
						File file	= new File(recvDir + System.getProperty("file.separator") + fileNames[i]);
						
						//check
						LUtil.println("BRCV", "RECV FILENAME=["+recvDir+System.getProperty("file.separator")+fileNames[i]+"]");

						LUtil.println("BRCV", "SELECTING...");
						
						rtn = SendToSap(fileNames[i], recvDir);

						if (rtn)
						{ 	
							LUtil.fileMove(recvDir, recvDir+System.getProperty("file.separator")+"backupOrg", fileNames[i], "Y");
							Thread.sleep(1000);
						}
						else {
							LUtil.fileMove(recvDir, recvDir+System.getProperty("file.separator")+"error", fileNames[i], "Y");
							HttpApiUtil.httpApiSend("DAEMON", "SAP", "ERROR", "RESULT ERROR", "[main] fileNames : [" + fileNames[i] + "] rtn = false");
						}
					}
				}

				LUtil.println("BRCV", "Send File Check...");

    		String[] snedFileNames = LUtil.getFileList(sendDir);
    		
    		//test
    		if(snedFileNames != null && snedFileNames.length > 0 ){

	    		if (!SFTPSendUtil.init())
	    		{
  					LUtil.println("BRCV", "SFTPSendUtil Init Error...");
							
	    			SFTPSendUtil.disconnection();
	    			return;
	    		}
	    		else
	    		{
  					LUtil.println("BRCV", "SFTP Send Start...");
						for(int i=0; i<snedFileNames.length; i++){
						
							//check
							LUtil.println("BRCV", "SEND FILENAME=["+sendDir+System.getProperty("file.separator")+snedFileNames[i]+"]");
												
						LUtil.println("BRCV", "SftpSendDir : "+SftpSendDir);
						LUtil.println("BRCV", "snedFileNames : "+sendDir + System.getProperty("file.separator") + snedFileNames[i]);
			    			rtn = SFTPSendUtil.upload(SftpSendDir, sendDir + System.getProperty("file.separator") + snedFileNames[i]);

								if (rtn) {
									LUtil.fileMove(sendDir, sendDir + System.getProperty("file.separator") + "backup", snedFileNames[i], "Y");
								}

						}
  					LUtil.println("BRCV", "SFTP Send End...");
	    			SFTPSendUtil.disconnection();
					}	
				}
				Thread.sleep(SLEEP_TIME);
    	}catch(Exception e){
    		LUtil.println("BRCV", "ERROR=["+e.getMessage()+"]");
				Thread.sleep(SLEEP_TIME);
    	}
    }
	}

	public static boolean SendToSap(String fileNames, String recvDir) throws Exception{
		String function_nm      = CUtil.get("jco.client.function")	;
		String fpath_param      = CUtil.get("jco.client.dir_nm")	;
		String fname_param      = CUtil.get("jco.client.file_nm")	;
		String tab_param		= CUtil.get("jco.client.table_nm")	;
		String tabcol_param		= CUtil.get("jco.client.table_col")	;
		String ret_param		= CUtil.get("jco.client.ret_param")	;
		String ret_msg			= "" ;

		boolean rtn 			= false ;
		if (function_nm == null)
			return false;
		try{
			JcoClient3 client = new JcoClient3();

			LUtil.println("BRCV", "function_nm:["+function_nm+"], file_nm:["+fname_param+"], table_nm:["+tab_param+"], table_col:["+tabcol_param+"], ret_param:["+ret_param+"]");
			
			ret_msg = client.JCoClientBatchCall(function_nm, recvDir, fileNames, fpath_param, fname_param, tab_param, tabcol_param, ret_param );

			if (ret_msg.equals("S")) rtn = true;
			
			LUtil.println("BRCV", "DEBUG  JcoClientBatchCall rtn ["+rtn+"]");
		}catch (Exception e) {
			LUtil.println("BRCV", e.getMessage());
    	HttpApiUtil.httpApiSend("DAEMON", "SAP", "ERROR", "RESULT ERROR", e.getMessage());
			return false;
		}

		return rtn;
	}

}

