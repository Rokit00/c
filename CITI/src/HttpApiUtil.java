package ksnet.sap;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.*;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;

class HttpApiUtil {

	static void httpApiSend(String Type1, String Type2, String Type3, String Title, String Context) {
				
				
		//http ��û �� url �ּҿ� �Ķ���� �����͸� �����ϱ� ���� ���� ����
		String UrlData = CUtil.get("API_URL"); 
		String SYSID = CUtil.get("API_SYSID");
		String SYSNAME = CUtil.get("API_SYSNAME");
		
		String totalUrl = "";
		if(Type1 != null && Type1.length() > 0 && !Type1.equals("") && !Type1.contains("null")) { //�Ķ���� ���� �ΰ��� �ƴ��� Ȯ��
			//totalUrl = UrlData.trim().toString() + "?" + "sysid="+ SYSID+"&sysname="+ new String(SYSNAME.getBytes(), StandardCharsets.UTF_8) ;
			//totalUrl = totalUrl + "&type1="+ new String(Type1.getBytes(), StandardCharsets.UTF_8) ;
			//totalUrl = totalUrl + "&type2="+ new String(Type2.getBytes(), StandardCharsets.UTF_8) ;
			//totalUrl = totalUrl + "&type3="+ new String(Type3.getBytes(), StandardCharsets.UTF_8) ;
			//totalUrl = totalUrl + "&title="+ new String(Title.getBytes(), StandardCharsets.UTF_8) ;
			//totalUrl = totalUrl + "&context="+ new String(Context.getBytes(), StandardCharsets.UTF_8) ;

			//totalUrl = UrlData.trim().toString() + "?" + "sysid="+ SYSID+"&sysname="+ SYSNAME.getBytes("utf-8") ;
			//totalUrl = totalUrl + "&type1="+ Type1.getBytes("utf-8") ;
			//totalUrl = totalUrl + "&type2="+ Type2.getBytes("utf-8") ;
			//totalUrl = totalUrl + "&type3="+ Type3.getBytes("utf-8") ;
			//totalUrl = totalUrl + "&title="+ Title.getBytes("utf-8") ;
			//totalUrl = totalUrl + "&context="+ Context.getBytes("utf-8") ;

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

		//http ����� �ϱ����� ��ü ���� �ǽ�
		URL url = null;
		HttpURLConnection conn = null;
	    
		//http ��� ��û �� ���� ���� �����͸� ��� ���� ����
		String responseData = "";	    	   
		BufferedReader br = null;
		StringBuffer sb = null;
	    
		//�޼ҵ� ȣ�� ������� ��ȯ�ϱ� ���� ����
		String returnData = "";
	 
		try {
			//�Ķ���ͷ� ���� url�� ����� connection �ǽ�
			url = new URL(totalUrl);	
			conn = (HttpURLConnection) url.openConnection();
	        
			//http ��û�� �ʿ��� Ÿ�� ���� �ǽ�
			//conn.setRequestProperty("Accept", "application/json");	               
			conn.setRequestProperty("Accept", "application/xml,text/xml,application/xhtml+xml");	               
			conn.setRequestProperty("Accept-Charset", "UTF-8");	               
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");	               
			conn.setRequestMethod("GET");	        	              
	        
			//http ��û �ǽ�
			conn.connect();
			LUtil.println("ApiUtil", "http ��û ��� : "+"GET");
			LUtil.println("ApiUtil", "http ��û Ÿ�� : "+"application/json");
			LUtil.println("ApiUtil", "http ��û �ּ� : "+UrlData);
			LUtil.println("ApiUtil", "http ��û ������ : "+totalUrl);

			//System.out.println("http ��û ��� : "+"GET");
			//System.out.println("http ��û Ÿ�� : "+"application/json");
			//System.out.println("http ��û �ּ� : "+UrlData);
			//System.out.println("http ��û ������ : "+totalUrl);
			//System.out.println("");
	        
			//http ��û �� ���� ���� �����͸� ���ۿ� �״´�
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));	
			sb = new StringBuffer();	       
			while ((responseData = br.readLine()) != null) {
				sb.append(responseData); //StringBuffer�� ������� ������ ���������� ���� �ǽ�
			}
	 
			//�޼ҵ� ȣ�� �Ϸ� �� ��ȯ�ϴ� ������ ���� ������ ���� �ǽ�
			returnData = sb.toString(); 
			
			//http ��û ���� �ڵ� Ȯ�� �ǽ�
			String responseCode = String.valueOf(conn.getResponseCode());
			LUtil.println("ApiUtil", "http ���� �ڵ� : "+responseCode);
			LUtil.println("ApiUtil", "http ���� ������ : "+returnData);
			//System.out.println("http ���� �ڵ� : "+responseCode);
			//System.out.println("http ���� ������ : "+returnData);
	 
		} catch (IOException e) {
			e.printStackTrace();
		} finally { 
			//http ��û �� ���� �Ϸ� �� BufferedReader�� �ݾ��ݴϴ�
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
}//Ŭ���� ����