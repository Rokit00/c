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
				
				
		//http 요청 시 url 주소와 파라미터 데이터를 결합하기 위한 변수 선언
		String UrlData = CUtil.get("API_URL"); 
		String SYSID = CUtil.get("API_SYSID");
		String SYSNAME = CUtil.get("API_SYSNAME");
		
		String totalUrl = "";
		if(Type1 != null && Type1.length() > 0 && !Type1.equals("") && !Type1.contains("null")) { //파라미터 값이 널값이 아닌지 확인
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
		
			System.out.println("http 요청 데이터 : "+totalUrl);

		//http 통신을 하기위한 객체 선언 실시
		URL url = null;
		HttpURLConnection conn = null;
	    
		//http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
		String responseData = "";	    	   
		BufferedReader br = null;
		StringBuffer sb = null;
	    
		//메소드 호출 결과값을 반환하기 위한 변수
		String returnData = "";
	 
		try {
			//파라미터로 들어온 url을 사용해 connection 실시
			url = new URL(totalUrl);	
			conn = (HttpURLConnection) url.openConnection();
	        
			//http 요청에 필요한 타입 정의 실시
			//conn.setRequestProperty("Accept", "application/json");	               
			conn.setRequestProperty("Accept", "application/xml,text/xml,application/xhtml+xml");	               
			conn.setRequestProperty("Accept-Charset", "UTF-8");	               
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");	               
			conn.setRequestMethod("GET");	        	              
	        
			//http 요청 실시
			conn.connect();
			LUtil.println("ApiUtil", "http 요청 방식 : "+"GET");
			LUtil.println("ApiUtil", "http 요청 타입 : "+"application/json");
			LUtil.println("ApiUtil", "http 요청 주소 : "+UrlData);
			LUtil.println("ApiUtil", "http 요청 데이터 : "+totalUrl);

			//System.out.println("http 요청 방식 : "+"GET");
			//System.out.println("http 요청 타입 : "+"application/json");
			//System.out.println("http 요청 주소 : "+UrlData);
			//System.out.println("http 요청 데이터 : "+totalUrl);
			//System.out.println("");
	        
			//http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));	
			sb = new StringBuffer();	       
			while ((responseData = br.readLine()) != null) {
				sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
			}
	 
			//메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
			returnData = sb.toString(); 
			
			//http 요청 응답 코드 확인 실시
			String responseCode = String.valueOf(conn.getResponseCode());
			LUtil.println("ApiUtil", "http 응답 코드 : "+responseCode);
			LUtil.println("ApiUtil", "http 응답 데이터 : "+returnData);
			//System.out.println("http 응답 코드 : "+responseCode);
			//System.out.println("http 응답 데이터 : "+returnData);
	 
		} catch (IOException e) {
			e.printStackTrace();
		} finally { 
			//http 요청 및 응답 완료 후 BufferedReader를 닫아줍니다
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
}//클래스 종료