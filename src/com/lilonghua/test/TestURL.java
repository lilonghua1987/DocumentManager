package com.lilonghua.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class TestURL {
	public static void main(String[] args) throws Exception{
		String queryString=URLEncoder.encode("中国银行","utf-8");
	   String urlStr = "http://127.0.0.1:8080/bankcode/doSearch?queryString="+queryString+"&pageIndex=1&pageSize=5";
	   System.out.println(urlStr);
	   URL url=new URL(urlStr);
	   URLConnection URLconnection = url.openConnection();  
	   HttpURLConnection httpConnection = (HttpURLConnection)URLconnection;  
	   int responseCode = httpConnection.getResponseCode(); 
	   //表示连接成功 值为200
	   if(responseCode==HttpURLConnection.HTTP_OK){
		   InputStream urlStream = httpConnection.getInputStream();  //HttpInputStream
		   BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlStream,"utf-8"));  
		   String currentLine = "";  
           StringBuffer totalString=new StringBuffer();
           while ((currentLine = bufferedReader.readLine()) != null) {  
               totalString.append(currentLine);
           }  
           System.out.println(totalString.toString());
	   }
	}
}
