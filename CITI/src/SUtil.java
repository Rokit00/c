package ksnet.sap;
import java.io.*;
import java.net.*;
import java.util.*;

class SUtil
{
	static String getCurrDate()
	{
		Calendar cal = Calendar.getInstance();
		StringBuffer sb = new StringBuffer();
		int li_yyyy,li_MM,li_dd,li_hour,li_min,li_sec;

		li_yyyy = cal.get(Calendar.YEAR); li_MM = cal.get(Calendar.MONTH); li_dd = cal.get(Calendar.DATE);
		li_hour = cal.get(Calendar.HOUR_OF_DAY); li_min = cal.get(Calendar.MINUTE); li_sec = cal.get(Calendar.SECOND);

		//sb.append(Integer.toString(li_yyyy)).append(li_MM<9 ? "0" : "").append(Integer.toString(li_MM + 1)).append(li_dd<10 ? "0" : "").append(Integer.toString(li_dd));
		//sb.append(li_yyyy+"").append(li_MM<9 ? "0" : "").append(li_MM + 1+"").append(li_dd<10 ? "0" : "").append(li_dd+""); 
		sb.append(li_yyyy).append(li_MM<9 ? "0" : "").append(li_MM + 1).append(li_dd<10 ? "0" : "").append(li_dd);
		sb.append(li_hour<10 ? "0" : "").append(li_hour).append(li_min<10 ? "0" : "").append(li_min).append(li_sec<10 ? "0" : "").append(li_sec);

		return sb.toString();
	}

	static String toHanX(byte[] bsrc, int idx, int len)
	{
		String str = toHan(bsrc, idx, len);
		if (str == null) return null;

		return str.trim();
	}

	static String toHan(byte[] bsrc, int idx, int len)
	{
		try
		{
			return new String(bsrc, idx, len ,"ksc5601");
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}
	
	static String toHanE(byte[] bsrc, String encoding_type)
	{
		try
		{
			String buf = new String(bsrc, "ksc5601");
			byte[] buf_enc = buf.getBytes(encoding_type);
			
			return new String(bsrc, encoding_type);
			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	static String toHanE(byte[] bsrc, int idx, int len, String encoding_type)
	{
		try
		{
			String buf = new String(bsrc, idx, len, "ksc5601");
			byte[] buf_enc = buf.getBytes(encoding_type);
			
			return new String(bsrc, encoding_type);
			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	static byte[] ConvB2B_Len(byte[] bsrc, int idx, int len, int tgt_len, String src_encoding, String tgt_encoding)
	{
		byte[] nb = new byte[tgt_len];
		
		for (int i=0; i < tgt_len; i++)
		{
			nb[i]= 0x20;
		}

		try
		{
			String buf = new String(bsrc, idx, len, src_encoding);
			byte[] buf_enc = buf.getBytes(tgt_encoding);

			System.arraycopy(buf_enc, 0, nb, 0, buf_enc.length);
			
		//	return new String(bsrc, tgt_encoding);
			return nb;
			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	static byte[] ConvB2B(byte[] bsrc, int idx, int len, String src_encoding, String tgt_encoding)
	{

		try
		{
			String buf = new String(bsrc, idx, len, src_encoding);
			byte[] buf_enc = buf.getBytes(tgt_encoding);

			return buf_enc;
			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	static String ConvB2S(byte[] bsrc, int idx, int len, String src_encoding, String tgt_encoding)
	{
		try
		{
			String buf = new String(bsrc, src_encoding);
			byte[] buf_enc = buf.getBytes(tgt_encoding);

			return new String(buf_enc, tgt_encoding);

			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	static byte[] ConvS2B(String src, int tgt_len, String tgt_encoding)
	{
		byte[] nb = new byte[tgt_len];
		
		for (int i=0; i < tgt_len; i++)
		{
			nb[i]= 0x20;
		}

		try
		{
			byte[] buf_enc = src.getBytes(tgt_encoding);

			System.arraycopy(buf_enc, 0, nb, 0, buf_enc.length);
			
		//	return new String(bsrc, encoding_type);
			return nb;
			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	public static String[] split(String srcStr, char c1)
	{
		return split(srcStr, String.valueOf(c1));
	}

	public static String[] split(String srcStr, String str1)
	{
		if (srcStr == null) return new String[0];

		String[] tokenArr = null;
		if (srcStr.indexOf(str1) == -1)
		{
			tokenArr = new String[1];
			tokenArr[0] = srcStr;

			return tokenArr;
		}

		LinkedList<String> linkedlist = new LinkedList<String>();

		int srcLength    = srcStr.length();
		int tockenLength = str1.length();

		int pos = 0, startPos = 0;
		while(startPos < srcLength)
		{
			pos = srcStr.indexOf(str1, startPos);

			if (-1 == pos) break;

			linkedlist.add(srcStr.substring(startPos, pos));
			startPos = pos + tockenLength;
		}

		if (startPos <= srcLength) linkedlist.add(srcStr.substring(startPos));

		return (String[])linkedlist.toArray(new String[0]);
	}//split
	 public static OutputStreamWriter openOutputStreamWriter(String file, String encoding) {

        FileOutputStream fileOutputStream     = null;
        OutputStreamWriter outputStreamWriter = null;
            
        try {
            fileOutputStream	= new FileOutputStream(file);
            outputStreamWriter	= new OutputStreamWriter(fileOutputStream, encoding);
		} catch (Exception e) {
			System.out.println("openOutputStreamWriter ERROR : " + e.getMessage());
			LUtil.println("openOutputStreamWriter ERROR", "ERROR=["+e.getMessage()+"]");

        }
        return outputStreamWriter;

     }
	 public static InputStreamReader openInputStreamReader(String file, String encoding) {
		 FileInputStream fileInputStream     = null;
		 InputStreamReader inputStreamReader   = null;
		 try {
			 fileInputStream = new FileInputStream(file);
			 inputStreamReader = new InputStreamReader(fileInputStream, encoding);
		}catch (Exception e) {
			System.out.println("openInputStreamReader ERROR : " + e.getMessage());
		}
		 return inputStreamReader;
	}
	 public static boolean isComplete(String path){
		 BufferedReader	br			= null;
		 boolean 		endflag		= false;
		 int			endflag_cnt = 0;
		 try{
			 br	= new BufferedReader(new FileReader(path));
			 while(true){
				 String line	= null;
				 if((line = br.readLine()) == null)	break;
				 else{
					 if(line.substring(0, 1).equals("E") || line.substring(0, 1).equals("3") || line.substring(0, 1).equals("T")){
						 endflag	= true;
						 endflag_cnt++;
					 }
					 else
						 endflag	= false;
				 }
			 }//while end
			 br.close();
			 if(endflag && endflag_cnt == 1)	return true;
			 else								return false;
			 
		 }catch(Exception e){
			 LUtil.println("isComplete", "ERROR : ["+e.getMessage()+"]");
			 return false;
		 }
	 }
}
