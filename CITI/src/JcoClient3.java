package ksnet.sap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class JcoClient3
{
    public JcoClient3()
    {
        Properties connectProperties = new Properties();
	String sap_type = CUtil.get("jco.sap.type");
	
	if (sap_type.equals("MSGSVR")) {
	        connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, CUtil.get("jco.client.mshost"));
        	connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV, CUtil.get("jco.client.msserv"));
        	connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, CUtil.get("jco.client.r3name"));
        	connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, CUtil.get("jco.client.group"));

		LUtil.println("JCLI","INFO jco.sap.type ["+sap_type +"] : " + CUtil.get("jco.client.mshost") + " : "+CUtil.get("jco.client.msserv") + " : " + CUtil.get("jco.client.r3name") + " : " +CUtil.get("jco.client.group"));
	}
	else {
        	connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, CUtil.get("jco.client.ashost"));
        	connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  CUtil.get("jco.client.sysnr"));

		LUtil.println("JCLI","INFO jco.sap.type ["+sap_type +"] : " + CUtil.get("jco.client.ashost") + " : "+ CUtil.get("jco.client.sysnr"));
	}
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, CUtil.get("jco.client.client"));
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   CUtil.get("jco.client.user"));
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, CUtil.get("jco.client.passwd"));
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   CUtil.get("jco.client.lang"));
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,    "10");
        createDataFile(CUtil.get("jco.client.pool_name"), "jcoDestination", connectProperties);
    }
    
    static void createDataFile(String name, String suffix, Properties properties){
        File cfg = new File(name+"."+suffix);
        if (cfg.exists()) LUtil.deleteFile(name + "." + suffix);
        if(!cfg.exists()){
            try{
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
            }catch (Exception e){
                throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
            }
        }
    }
    
    public String JCoClientBatchCall(String function_name, String recvdir, String file_name, String fpath_name, String fname_param, String tab_param, String tabcol_param, String ret_param) throws JCoException{
    	
    	//connection information
    	JCoDestination destination = JCoDestinationManager.getDestination(CUtil.get("jco.client.pool_name"));
    	
    	StringBuilder	sb	= new StringBuilder();
    	String msg;
    	String ret_msg;

    	//remote function call
    	JCoFunction function = destination.getRepository().getFunction(function_name);
    	
        if(function == null) {
					LUtil.println("JCLI", "JCoClientCall : " + function_name+" not found in SAP.");
			
    			HttpApiUtil.httpApiSend("DAEMON", "SAP", "ERROR", "RESULT NOT RESPONSE", "[JCoClientCall-1] function_name : [" + function_name + "] null...");
          throw new RuntimeException("JCoClientCall : " + function_name+" not found in SAP.");
        }
    	
    	try{
    		//ABRRYYYYMMDD_SENDRECV***.001 : *** Setting
    		function.getImportParameterList().setValue(fname_param, file_name);
    		function.getImportParameterList().setValue(fpath_name, recvdir);
    		
    		//check file full path
    		sb.append(recvdir).append(System.getProperty("file.separator")).append(file_name);
    		
    		InputStreamReader input = SUtil.openInputStreamReader(recvdir+System.getProperty("file.separator")+file_name, "UTF-8");
    		BufferedReader in = new BufferedReader(input);
    		
    		//check file full path
    		LUtil.println("JCLI", "RECV FILENAME=["+sb.toString()+"]");
    		
    		JCoTable batch_tbl = function.getTableParameterList().getTable(tab_param);
    		
    		while ((msg = in.readLine()) != null){
    			batch_tbl.appendRow();
    			
    			byte[]	conv	= msg.getBytes(CUtil.get("ENCODING"));
    			String	cmsg	= new String(conv, CUtil.get("ENCODING"));
    			
    			//key, values setting
    			batch_tbl.setValue(tabcol_param, msg);
    			
    			/*
    			//key, values setting
    			batch_tbl.setValue(tabcol_param, msg);
    			System.out.println ("file data ["+msg+"]");
    			*/
    		}
    		
    		function.execute(destination);
    		input.close();
    		
    	}catch(Exception e){
    		LUtil.println("JCLI","AbapException=["+e.toString()+"]");
    	  HttpApiUtil.httpApiSend("DAEMON", "SAP", "ERROR", "RESULT ERROR", "[JCoClientCall-2] : "+e.toString());
    		return null;	
    	}
    	
    	ret_msg = function.getExportParameterList().getString(ret_param);
    	function.getExportParameterList().getString(ret_param);
    	
    	System.out.println("JCoClientBatchCall finished:"+ret_param);
    	System.out.println("ret_msg: " + ret_msg);
    	
    	LUtil.println("JCLI", "JCoClientBatchCall finished=["+ret_param+"]");
    	LUtil.println("JCLI", "ret_msg=["+ret_msg+"]");
    	
    	return ret_msg;
    }
}
