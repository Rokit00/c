package ksnet.sap;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
 
public class  SFTPRecvUtil {
    private static Session sessionRecv = null;
    private static Channel channelRecv = null;
    private static ChannelSftp channelSftpRecv = null;

    public static boolean init() {
 
        JSch jSch = new JSch();
        try {

						String sftp_host = CUtil.get("SFTP_HOST");
						String sftp_port = CUtil.get("SFTP_PORT");
						String sftp_userName = CUtil.get("SFTP_USERNAME");
						String sftp_privateKey = CUtil.get("SFTP_PRIVATEKEY");
						String sftp_password = "";


            if(sftp_privateKey != null) {//키가 존재한다면
LUtil.println("SFTP ksjj1", "1"+sftp_privateKey);
                jSch.addIdentity(sftp_privateKey);
LUtil.println("SFTP ksjj2", "1");
             }
 
            sessionRecv = jSch.getSession(sftp_userName, sftp_host, Integer.parseInt(sftp_port));
 
            if(sftp_privateKey == null) {//키가 없다면
                sessionRecv.setPassword(sftp_password);
            }
 
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            sessionRecv.setConfig(config);
            sessionRecv.connect();
 LUtil.println("SFTP ksjj3", "connect");
            channelRecv = sessionRecv.openChannel("sftp");
            channelRecv.connect();
	    LUtil.println("SFTP init", "Connect Success!!!");
            
        } catch (JSchException e) {
    			LUtil.println("SFTP init", "ERROR=["+e.getMessage()+"]");
  				HttpApiUtil.httpApiSend("DAEMON", "VAN", "ERROR", "CONNECT ERROR", "[RECV Init] Init Failed...");
          e.printStackTrace();
          return false;
        }
 
        channelSftpRecv = (ChannelSftp) channelRecv;
 
        return true;
    }

    public static void mkdir(String dir, String mkdirName) {
 
        try {
          channelSftpRecv.cd(dir);
          channelSftpRecv.mkdir(mkdirName);
        } catch (SftpException e) {
    			LUtil.println("SFTP mkdir", "SFTP ERROR=["+e.getMessage()+"]");
          e.printStackTrace();
        } catch (Exception e) {
    			LUtil.println("SFTP mkdir", "ERROR=["+e.getMessage()+"]");
           e.printStackTrace();
        }
    }

    public static void upload(String dir, String filePath) {
 
        FileInputStream in = null;
        try {
						File file = new File(filePath);
        	
            in = new FileInputStream(file);
            channelSftpRecv.cd(dir);
            channelSftpRecv.put(in, file.getName());
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

		@SuppressWarnings("unchecked")
    public static void multiDownload(String dir, String targetDir, String path) {
        try {
	        List<ChannelSftp.LsEntry> fileList = channelSftpRecv.ls(dir);
	        if (fileList != null) {
	            for (ChannelSftp.LsEntry entry : fileList) {
	                if (".".equals(entry.getFilename()) || "..".equals(entry.getFilename())) {
	                    continue;
	                }
    							LUtil.println("SFTP multiDownload", "FileName=["+entry.getFilename()+"]");
	                download(dir, entry.getFilename(), path);
	            }
	        }
        } catch (SftpException e) {
    			LUtil.println("SFTP multiDownload", "ERROR=["+e.getMessage()+"]");
          e.printStackTrace();
        }
    }

    public static void download(String dir, String downloadFileName, String path) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            channelSftpRecv.cd(dir);
            in = channelSftpRecv.get(downloadFileName);
            
        } catch (SftpException e) {
    			LUtil.println("SFTP download Get", "ERROR=["+e.getMessage()+"]");
          e.printStackTrace();
        }
 
        try {
            out = new FileOutputStream(new File(path+"\\"+downloadFileName));
            int i;
 
            while ((i = in.read()) != -1) {
                out.write(i);
            }
        } catch (IOException e) {
    			LUtil.println("SFTP download write", "ERROR=["+e.getMessage()+"]");
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
 
        }
 
    }

    public static void disconnection() {
        channelSftpRecv.quit();
        sessionRecv.disconnect();
 
    }
 
}