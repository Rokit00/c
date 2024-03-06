package ksnet.sap;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.*;


class HttpApiUtil {

	static void httpApiSend(String Type1, String Type2, String Type3, String Title, String Context) {
		String UrlData = CUtil.get("API_URL"); 
		String SYSID = CUtil.get("API_SYSID");
		String SYSNAME = CUtil.get("API_SYSNAME");
		
		String totalUrl = "";
		if(Type1 != null && Type1.length() > 0 && !Type1.equals("") && !Type1.contains("null")) { //�Ķ���� ���� �ΰ��� �ƴ��� Ȯ��
			String paramSYSNAME = "";
			String paramType1 = "";
			String paramType2 = "";
			String paramType3 = "";
			String paramTitle = "";
			String paramContext = "";
			
			paramSYSNAME = paramEncode(SYSNAME) ;
			paramType1 = paramEncode(Type1) ;
			paramType2 = paramEncode(Type2) ;
			paramType3 = paramEncode(Type3) ;
			paramTitle = paramEncode(Title) ;
			paramContext = paramEncode(Context) ;

			totalUrl = UrlData.trim().toString() + "?" + "sysid="+ SYSID+"&sysname="+ paramSYSNAME ;
			totalUrl = totalUrl + "&type1="+ paramType1 ;
			totalUrl = totalUrl + "&type2="+ paramType2 ;
			totalUrl = totalUrl + "&type3="+ paramType3 ;
			totalUrl = totalUrl + "&title="+ paramTitle ;
			totalUrl = totalUrl + "&context="+ paramContext ;
		}
		else {
			return;
		}
		
			System.out.println("http ��û ������ : "+totalUrl);

		URL url = null;
		HttpURLConnection conn = null;

		String responseData = "";	    	   
		BufferedReader br = null;
		StringBuffer sb = null;

		String returnData = "";
	 
		try {
			url = new URL(totalUrl);	
			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestProperty("Accept", "application/xml,text/xml,application/xhtml+xml");	               
			conn.setRequestProperty("Accept-Charset", "UTF-8");	               
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");	               
			conn.setRequestMethod("GET");	        	              

			conn.connect();
			LUtil.println("ApiUtil", "http ��û ��� : "+"GET");
			LUtil.println("ApiUtil", "http ��û Ÿ�� : "+"application/json");
			LUtil.println("ApiUtil", "http ��û �ּ� : "+UrlData);
			LUtil.println("ApiUtil", "http ��û ������ : "+totalUrl);

			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));	
			sb = new StringBuffer();	       
			while ((responseData = br.readLine()) != null) {
				sb.append(responseData);
			}

			returnData = sb.toString(); 

			String responseCode = String.valueOf(conn.getResponseCode());
			LUtil.println("ApiUtil", "http ���� �ڵ� : "+responseCode);
			LUtil.println("ApiUtil", "http ���� ������ : "+returnData);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	 		
	}

	static String paramEncode(String param) {
		try {
			return URLEncoder.encode(param,"UTF-8");
		}
		catch (Exception ex)
		{
			return param;
		}
	}
}