package ksnet.sap;
import java.io.*;
import java.net.*;
import java.util.*;

class LUtil
{
	static String LOG_DIR = null;

	static Calendar LOG_CAL     = Calendar.getInstance();
	static String  LOG_DT       = null;
	static String  LOG_FILE     = null;

	private static synchronized boolean init()
	{
		if (null == LOG_FILE || CUtil.isNew())
		{
			LOG_DIR     = CUtil.get("LOG_DIR_PATH");

			if (null == LOG_DIR) return false;
	
		}
	
		return true;
	}



	private static synchronized void day_check(String curr_dt8)
	{
		if (LOG_DT == null || !LOG_DT.equals(curr_dt8))
		{
			File log_dir_file = new File(LOG_DIR);
			if (!log_dir_file.exists())log_dir_file.mkdirs();

			StringBuffer sb = new StringBuffer();

			sb.append(LOG_DIR);
			if (!LOG_DIR.endsWith("/") && !LOG_DIR.endsWith("\\")) sb.append("/");
			sb.append(curr_dt8);
			sb.append(".log");

			LOG_FILE = sb.toString();

			LOG_DT = curr_dt8;
		}
	}


	public static void println(Object pstr)
	{
		if (!init())
		{
			System.out.println("LOG_DIR_PATH Setting Error");
			return;
		}

		String curr_date = SUtil.getCurrDate();

		day_check(curr_date.substring(0,8));

		File openFile = new File(LOG_FILE);
		PrintStream out = null;

		try{
			if(openFile.exists()){
				out = new PrintStream(new FileOutputStream(LOG_FILE, true), true);
			}else{
				out = new PrintStream(new FileOutputStream(LOG_FILE), true);
			}

			if (pstr instanceof Throwable)
			{
				Throwable tw = (Throwable)pstr;
				tw.printStackTrace(out);
				out.println();
			}else{
				StringBuffer sb = new StringBuffer();

				sb.append("[");
				sb.append(curr_date.substring(8,10)).append(":").append(curr_date.substring(10,12)).append(":").append(curr_date.substring(12,14));
				sb.append("]" );
				sb.append(pstr);

				out.println(sb.toString());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			try{if (out != null) out.close();}catch(Exception e){};
		}
	}

	public static void println(String svc_type, Object pstr)
	{
		if (!init())
		{
			System.out.println("LOG_DIR_PATH Setting Error");
			return;
		}

		String curr_date = SUtil.getCurrDate();

		day_check(curr_date.substring(0,8));

		File openFile = new File(LOG_FILE);
		PrintStream out = null;

		try{
			if(openFile.exists()){
				out = new PrintStream(new FileOutputStream(LOG_FILE, true), true);
			}else{
				out = new PrintStream(new FileOutputStream(LOG_FILE), true);
			}

			if (pstr instanceof Throwable)
			{
				Throwable tw = (Throwable)pstr;
				tw.printStackTrace(out);
				out.println();
			}else{
				StringBuffer sb = new StringBuffer();

				sb.append("[");
				sb.append(curr_date.substring(8,10)).append(":").append(curr_date.substring(10,12)).append(":").append(curr_date.substring(12,14));
				sb.append("][" );
				sb.append(svc_type);
				sb.append("]" );
				sb.append(pstr);

				out.println(sb.toString());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			try{if (out != null) out.close();}catch(Exception e){};
		}
	}

	public static void createFile(String dir, String file, byte[] content){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dir + "/" + file);
			fos.write(content, 0, 300);
			
		} catch (Throwable e) {
			e.printStackTrace(); 
		} finally{
			if(fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void deleteFile(String dir, String file){
		File f = new File(dir + "/" + file);
		try {
			f.delete();
			
		} catch (Throwable e) {
			e.printStackTrace(); 
		} finally{
			
		}
	}
	public static void deleteFile(String file){
		File f = new File(file);
		try {
			f.delete();
			
		} catch (Throwable e) {
			e.printStackTrace(); 
		} finally{
			
		}
	}
   	public static void saveData(String fileName, String msg)
	{
		PrintStream out = null;

		try {
			out = new PrintStream(new FileOutputStream(fileName), true);

			out.println(msg);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			out.close();
		}
	}
	public static String[] getFileList(String dir){
		String[] fList = null;
		ArrayList<String> arrFile = new ArrayList<String>();
		File fDir = new File(dir);
		File[] files = fDir.listFiles();
		if(files != null){
			for(int i=0; i<files.length;i++){
				if(files[i].isFile()){
					arrFile.add(files[i].getName());
				}
			}
			
			fList = arrFile.toArray(new String[0]);
		}
		
		return fList;
	}
	
	public static byte[] readFile(String dir, String file){
		StringBuffer sb = new StringBuffer();
		try {
	    	BufferedReader in = new BufferedReader(new FileReader(dir +System.getProperty("file.separator")+ file));
	    	String s;

	    	while ((s = in.readLine()) != null) {
	    		sb.append(s);
	    	}
	    	in.close();
	    	
		} catch (IOException e) {
			e.printStackTrace(); 
		} finally{
			
		}
		
		if(sb.length() > 0){
			return sb.toString().getBytes();
    	}else{
    		return null;
    	}
	}

	public static void fileMove( String src_path, String tgt_path, String fileName, String dateYn){
		try{		

	    Calendar cal = Calendar.getInstance();
	    String dateString;
	
	    dateString = String.format("%04d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);

			String path = "";
			
			if (dateYn == "Y")
				path = tgt_path+System.getProperty("file.separator")+dateString; //폴더 경로			
			else
				path = tgt_path; //폴더 경로			
			
			File Folder = new File(path);
		
			// 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
			if (!Folder.exists()) {
				try{
				    Folder.mkdir(); //폴더 생성합니다.
				    System.out.println("폴더가 생성되었습니다.");
			  } 
			  catch(Exception e){
				    e.getStackTrace();
				}        
		  }else {
				System.out.println("이미 폴더가 생성되어 있습니다.");
			}
      String src_name = src_path+System.getProperty("file.separator")+fileName;
      String tgt_name = path+System.getProperty("file.separator")+fileName;
      
			File srcFile  = new File (src_name);
			File tgtFile  = new File (tgt_name);

			if(tgtFile.exists()){
				String tempName =tgt_name+"."+SUtil.getCurrDate(); ;
				File tempFile = new File (tempName);
				if(!srcFile.renameTo(tempFile))
					LUtil.println("fileMove", "["+src_name+"] -> ["+tempName+"] Move Fail");
				else
					LUtil.println("fileMove", "["+src_name+"] -> ["+tempName+"] Move Ok");
			}
			else{
				if(!srcFile.renameTo(tgtFile))
					LUtil.println("fileMove", "["+src_name+"] -> ["+tgt_name+"] Move Fail");
				else
					LUtil.println("fileMove", "["+src_name+"] -> ["+tgt_name+"] Move Ok");
			}
		}catch(Exception e){
			LUtil.println("fileMove", "ERROR=["+e.getMessage()+"]");
		}
	}
	
	public static boolean checkFileState(String path){
		boolean endflag 	= false	;
		int	endflag_cnt 	= 0	;
		try{
			BufferedReader	br	= new BufferedReader(new FileReader(path));
			while(true){
				String	line	= null;
				if((line = br.readLine()) == null)	break;
				else{
					if(line.substring(0, 1).equals("E") || line.substring(0, 1).equals("T") || line.substring(0, 1).equals("3")){
						endflag = true;
						endflag_cnt++;
					}
					else
						endflag = false;				
				}
			}//while end
			
			br.close();
			
			if(!endflag && endflag_cnt != 1)	return false;
			else					return true;
		}catch(Exception e){
			LUtil.println("checkFileState", "ERROR=["+e.getMessage()+"]");
			return false;
		}
	}
}
