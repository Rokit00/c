package ksnet.sap;

import java.io.*;
import java.net.*;
import java.util.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.ServerDataProvider;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;
import com.sap.conn.jco.server.JCoServerTIDHandler;
import com.sap.conn.jco.JCoTable;

import java.security.NoSuchProviderException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import ksnet.sap.PgpHelper;
import ksnet.sap.RSAKeyPairGenerator;

public class KsnetJcoSvr
{
    static String SERVER_NAME = "BOA_SFTP_SVR_DEV";
    static String DESTINATION_NAME = "BOA_SFTP_SVR_POOL_DEV";
    static MyTIDHandler myTIDHandler = null;

    public static void main(String[] args) throws Exception
    {
			CUtil.setConfig(args[0]);
			
			SetConfig();
			
			startServer();
			
			LUtil.println("JSVR", "START JcoSftpSvr");
    }

    static void SetConfig()
    {
		Properties connectProperties = new Properties();
			
		String sap_type = CUtil.get("jco.sap.type");

		if (sap_type.equals("MSGSVR")) {
		        connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, CUtil.get("jco.server.mshost"));
        		connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV, CUtil.get("jco.server.msserv"));
        		connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, CUtil.get("jco.server.r3name"));
        		connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, CUtil.get("jco.server.group"));
		
			LUtil.println("JCLI","INFO jco.sap.type ["+sap_type +"] : " + CUtil.get("jco.server.mshost") + " : "+CUtil.get("jco.server.msserv") + " : " + CUtil.get("jco.server.r3name") + " : " +CUtil.get("jco.server.group"));
		}
		//else {
			connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, CUtil.get("jco.server.ashost"));
	        	connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, CUtil.get("jco.server.sysnr"));

			LUtil.println("JCLI","INFO jco.sap.type ["+sap_type +"] : " + CUtil.get("jco.server.ashost") + " : "+ CUtil.get("jco.server.sysnr"));
		//}
			connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,CUtil.get("jco.server.client"));
			connectProperties.setProperty(DestinationDataProvider.JCO_USER, CUtil.get("jco.server.user"));
			connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, CUtil.get("jco.server.passwd"));
			connectProperties.setProperty(DestinationDataProvider.JCO_LANG, CUtil.get("jco.server.lang"));
			
			connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, CUtil.get("jco.server.capacity"));
			connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, CUtil.get("jco.server.limit"));
			createDataFile(DESTINATION_NAME, "jcoDestination", connectProperties);
			
			Properties servertProperties = new Properties();
			servertProperties.setProperty(ServerDataProvider.JCO_GWHOST, CUtil.get("jco.server.gwhost"));
			servertProperties.setProperty(ServerDataProvider.JCO_GWSERV, CUtil.get("jco.server.gwserv"));
			servertProperties.setProperty(ServerDataProvider.JCO_PROGID, CUtil.get("jco.server.progid"));
			servertProperties.setProperty(ServerDataProvider.JCO_REP_DEST, DESTINATION_NAME);
			
			servertProperties.setProperty(ServerDataProvider.JCO_CONNECTION_COUNT, CUtil.get("jco.server.conn_cnt"));
			createDataFile(SERVER_NAME, "jcoServer", servertProperties);
    }
    
    static void createDataFile(String name, String suffix, Properties properties)
    {
        File cfg = new File(name + "." + suffix);
        
        if (cfg.exists()) LUtil.deleteFile(name + "." + suffix);
        if(!cfg.exists())
        {
            try
            {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
            }
            catch(Exception e)
            {
                throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
            }
        }
    }
    
    static class StfcConnectionHandler implements JCoServerFunctionHandler
    {
        public void handleRequest(JCoServerContext serverCtx, JCoFunction function)
        {
/*
            System.out.println("----------------------------------------------------------------");
            System.out.println("call              : " + function.getName());
            System.out.println("ConnectionId      : " + serverCtx.getConnectionID());
            System.out.println("SessionId         : " + serverCtx.getSessionID());
            System.out.println("TID               : " + serverCtx.getTID());
            System.out.println("repository name   : " + serverCtx.getRepository().getName());
            System.out.println("is in transaction : " + serverCtx.isInTransaction());
            System.out.println("is stateful       : " + serverCtx.isStatefulSession());
            System.out.println("----------------------------------------------------------------");
            System.out.println("gwhost: " + serverCtx.getServer().getGatewayHost());
            System.out.println("gwserv: " + serverCtx.getServer().getGatewayService());
            System.out.println("progid: " + serverCtx.getServer().getProgramID());
            System.out.println("----------------------------------------------------------------");
            System.out.println("attributes  : ");
            System.out.println(serverCtx.getConnectionAttributes().toString());
            System.out.println("----------------------------------------------------------------");
            System.out.println("CPIC conversation ID: " + serverCtx.getConnectionAttributes().getCPICConversationID());
            System.out.println("----------------------------------------------------------------");
*/			
			
						LUtil.println("JSVR", "DEBUG function name :: "+function.getName());
						try {
								String function_nm      = CUtil.get("jco.server.function");
								String fname_param      = CUtil.get("jco.server.file_nm");
								String tab_param      	= CUtil.get("jco.server.table_nm");
								String tabcol_param     = CUtil.get("jco.server.table_col");
								String ret_param      	= CUtil.get("jco.server.ret_param");

LUtil.println("JSVR", "DEBUG function name :: "+ function_nm   ); 
LUtil.println("JSVR", "DEBUG fname_param :: "+ fname_param   ); 
LUtil.println("JSVR", "DEBUG tab_param :: "+ tab_param  ); 
LUtil.println("JSVR", "DEBUG tabcol_param :: "+ tabcol_param   ); 
LUtil.println("JSVR", "DEBUG ret_param :: "+ ret_param  ); 
							
								String filename = function.getImportParameterList().getString(fname_param);
				
								LUtil.println("JSVR", "["+function.getName()+"] filename["+filename+"]");
								
								String save_fileName = CUtil.get("USED_DIR") + System.getProperty("file.separator") + filename;
								
								JCoTable table = function.getTableParameterList().getTable(tab_param);
								
								
								OutputStreamWriter out = SUtil.openOutputStreamWriter(save_fileName, "UTF-8");  
								//out = new PrintStream(new FileOutputStream(save_fileName), true);
								LUtil.println("JSVR", "FILE PATH : ["+save_fileName+"]");
				
								for (int i = 0; i < table.getNumRows(); i++) 
								{
									table.setRow(i);
									
									System.out.println(table.getString(tabcol_param));
									//table.getString() -> row value + CRLF return
									out.write(table.getString(tabcol_param)+"\r\n");
								}

								function.getExportParameterList().setValue(ret_param, "S");

								if(myTIDHandler != null)
									myTIDHandler.execute(serverCtx);
				
								out.close();
								
								LUtil.fileMove(CUtil.get("USED_DIR"), CUtil.get("SEND_DIR"), filename, "N");

						}
						catch (Exception e) {
							LUtil.println("JSVR", e.getMessage());
						}
        }
    }

    static class MyThrowableListener implements JCoServerErrorListener, JCoServerExceptionListener
    {
        
        public void serverErrorOccurred(JCoServer jcoServer, String connectionId, JCoServerContextInfo serverCtx, Error error)
        {
            System.out.println(">>> Error occured on " + jcoServer.getProgramID() + " connection " + connectionId);
            error.printStackTrace();
        }
        
        public void serverExceptionOccurred(JCoServer jcoServer, String connectionId, JCoServerContextInfo serverCtx, Exception error)
        {
            System.out.println(">>> Error occured on " + jcoServer.getProgramID() + " connection " + connectionId);
            error.printStackTrace();
        }
    }
    
    static class MyStateChangedListener implements JCoServerStateChangedListener
    {
        public void serverStateChangeOccurred(JCoServer server, JCoServerState oldState, JCoServerState newState)
        {
            
            // Defined states are: STARTED, DEAD, ALIVE, STOPPED;
            // see JCoServerState class for details.
            // Details for connections managed by a server instance
            // are available via JCoServerMonitor
            System.out.println("Server state changed from " + oldState.toString() + " to " + newState.toString() + " on server with program id "
                    + server.getProgramID());
        }
    }
    
    static void startServer()
    {
        JCoServer server;
        try
        {
            server = JCoServerFactory.getServer(SERVER_NAME);
        }
        catch(JCoException ex)
        {
            throw new RuntimeException("Unable to create the server " + SERVER_NAME + ", because of " + ex.getMessage(), ex);
        }
        System.out.println("DEBUG startServer function " + CUtil.get("jco.server.function"));
        
        JCoServerFunctionHandler stfcConnectionHandler = new StfcConnectionHandler();
        DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
        factory.registerHandler(CUtil.get("jco.server.function"), stfcConnectionHandler);
        server.setCallHandlerFactory(factory);
        
        // additionally to step 1
        MyThrowableListener eListener = new MyThrowableListener();
        server.addServerErrorListener(eListener);
        server.addServerExceptionListener(eListener);
        
        MyStateChangedListener slistener = new MyStateChangedListener();
        server.addServerStateChangedListener(slistener);
        
        server.start();
        System.out.println("The program can be stoped using <ctrl>+<c>");
    }
    
    static class MyTIDHandler implements JCoServerTIDHandler
    {
        
        Map<String, TIDState> availableTIDs = new Hashtable<String, TIDState>();
        
        public boolean checkTID(JCoServerContext serverCtx, String tid)
        {
            // This example uses a Hashtable to store status information. But usually
            // you would use a database. If the DB is down, throw a RuntimeException at
            // this point. JCo will then abort the tRFC and the R/3 backend will try
            // again later.
            
            System.out.println("TID Handler: checkTID for " + tid);
            TIDState state = availableTIDs.get(tid);
            if(state == null)
            {
                availableTIDs.put(tid, TIDState.CREATED);
                return true;
            }
            
            if(state == TIDState.CREATED || state == TIDState.ROLLED_BACK)
                return true;

            return false;
            // "true" means that JCo will now execute the transaction, "false" means
            // that we have already executed this transaction previously, so JCo will
            // skip the handleRequest() step and will immediately return an OK code to R/3.
        }
        
        public void commit(JCoServerContext serverCtx, String tid)
        {
            System.out.println("TID Handler: commit for " + tid);
            
            // react on commit e.g. commit on the database
            // if necessary throw a RuntimeException, if the commit was not
            // possible
            availableTIDs.put(tid, TIDState.COMMITTED);
        }
        
        public void rollback(JCoServerContext serverCtx, String tid)
        {
            System.out.println("TID Handler: rollback for " + tid);
            availableTIDs.put(tid, TIDState.ROLLED_BACK);
            
            // react on rollback e.g. rollback on the database
        }
        
        public void confirmTID(JCoServerContext serverCtx, String tid)
        {
            System.out.println("TID Handler: confirmTID for " + tid);
            
            try
            {
                // clean up the resources
            }
            // catch(Throwable t) {} //partner wont react on an exception at
            // this point
            finally
            {
                availableTIDs.remove(tid);
            }
        }
        
        public void execute(JCoServerContext serverCtx)
        {
            String tid = serverCtx.getTID();
            if(tid != null)
            {
                System.out.println("TID Handler: execute for " + tid);
                availableTIDs.put(tid, TIDState.EXECUTED);
            }
        }
        
        private enum TIDState
        {
            CREATED, EXECUTED, COMMITTED, ROLLED_BACK, CONFIRMED;
        }
    }
}

