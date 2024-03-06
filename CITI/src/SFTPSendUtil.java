package ksnet.sap;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import ksnet.sap.PgpHelper;
import ksnet.sap.RSAKeyPairGenerator;


public class  SFTPSendUtil {
    private static Session sessionSend = null;
    private static Channel channelSend = null;
    private static ChannelSftp channelSftpSend = null;

    public static boolean init() {
 
        JSch jSch = new JSch();
        try {

						String sftp_host = CUtil.get("SFTP_HOST");
						String sftp_port = CUtil.get("SFTP_PORT");
						String sftp_userName = CUtil.get("SFTP_USERNAME");
						String sftp_privateKey = CUtil.get("SFTP_PRIVATEKEY");
						String sftp_password = "";


            if(sftp_privateKey != null) {//Ű�� �����Ѵٸ�
LUtil.println("SFTP ksj:", "ERROR=["+sftp_privateKey+"]");
                jSch.addIdentity(sftp_privateKey);
LUtil.println("SFTP ksj2:", "ERROR=["+sftp_privateKey+"]");

            }
 
            sessionSend = jSch.getSession(sftp_userName, sftp_host, Integer.parseInt(sftp_port));
 
            if(sftp_privateKey == null) {//Ű�� ���ٸ�
                sessionSend.setPassword(sftp_password);
            }
 
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            sessionSend.setConfig(config);
            sessionSend.connect();
 LUtil.println("SFTP ksj3:", "connect ");
            channelSend = sessionSend.openChannel("sftp");
            channelSend.connect();
            
        } catch (JSchException e) {
    				LUtil.println("SFTP SEND init", "ERROR=["+e.getMessage()+"]");
	  				HttpApiUtil.httpApiSend("DAEMON", "VAN", "ERROR", "CONNECT ERROR", "[SEND Init] Init Failed...");
            e.printStackTrace();
            return false;
        }
 
        channelSftpSend = (ChannelSftp) channelSend;
 
        return true;
    }

    public static void mkdir(String dir, String mkdirName) {
 
        try {
            channelSftpSend.cd(dir);
            channelSftpSend.mkdir(mkdirName);
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean upload(String dir, String filePath) {
 
        FileInputStream in = null;
        try {
					File file = new File(filePath);
      	
          in = new FileInputStream(file);
          channelSftpSend.cd(dir);
          channelSftpSend.put(in, file.getName());
          
          in.close();
        } catch (SftpException e) {
          e.printStackTrace();
        	return false;
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        	return false;
        } finally {
        	return true;
        }
    }

		@SuppressWarnings("unchecked")
    public static void multiDownload(String dir, String path) {
        try {
	        List<ChannelSftp.LsEntry> fileList = channelSftpSend.ls(dir);
	        if (fileList != null) {
	            for (ChannelSftp.LsEntry entry : fileList) {
	                if (".".equals(entry.getFilename()) || "..".equals(entry.getFilename())) {
	                    continue;
	                }
	                download(dir, entry.getFilename(), path);
	            }
	        }
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public static void download(String dir, String downloadFileName, String path) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            channelSftpSend.cd(dir);
            in = channelSftpSend.get(downloadFileName);
        } catch (SftpException e) {
            e.printStackTrace();
        }
 
        try {
            out = new FileOutputStream(new File(path));
            int i;
 
            while ((i = in.read()) != -1) {
                out.write(i);
            }
        } catch (IOException e) {
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
        channelSftpSend.quit();
        sessionSend.disconnect();
    }
}