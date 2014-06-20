package tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream; 
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine; 
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class my_httpreq{
	 
	public String resPonsToString(HttpResponse response) throws Exception{ 
        String responseString = "";         
        StatusLine statusLine = response.getStatusLine();        
        if(statusLine.getStatusCode() == HttpStatus.SC_OK){

            HttpEntity entity = response.getEntity(); 
            BufferedInputStream bis = new BufferedInputStream(entity.getContent()); 
            
            int inByte; 
            byte data[] = new byte[1024]; 
            while((inByte = bis.read(data)) != -1){			            	
            	responseString +=   new String(data,0,inByte);   
            }

            bis.close();              
        } else{ 
        }
        
        return responseString;		
	}
	
	public void responseToFile(HttpResponse response,String filename) throws Exception{
		File oldfile = new File(filename);
		if(oldfile.exists()){
			oldfile.delete();
		}
		String tempFile = filename + ".temp";
        File tempFileR = new File(tempFile);
        StatusLine statusLine = response.getStatusLine();        
        if(statusLine.getStatusCode() == HttpStatus.SC_OK){

            HttpEntity entity = response.getEntity(); 
            BufferedInputStream bis = new BufferedInputStream(entity.getContent()); 
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFileR));
            int inByte; 
            byte data[] = new byte[1024]; 
            while((inByte = bis.read(data)) != -1){			            	
            	bos.write(data,0,inByte); 
            }

            bis.close(); 
            bos.flush();
            bos.close();
            tempFileR.renameTo(new File(filename));
        } else{ 
        }
         
	}
	
	
	public HttpPost mypost(String url, List<BasicNameValuePair> params) throws  Exception{
		HttpPost request = new HttpPost(url);
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params); 
		request.setEntity(urlEncodedFormEntity); 

		 String userAgent = "Mozilla/5.0 (Windows NT 6.2; rv:24.0) Gecko/20100101 Firefox/24.0";
		 request.setHeader("User-Agent", userAgent);
		request.setHeader("Referer", "http://www.google.com");
		
		return request;
	}
	
	public HttpGet myReq(String url){		
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; rv:2.2) Gecko/20110201";
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", userAgent);
		request.setHeader("Referer", "http://www.google.com");
		
		return request;		
	}
	
	public HttpResponse getResponse(HttpPost post,HttpGet get) throws Exception{
		 
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpResponse response = null;     
		
		if(get != null){
	        response = httpclient.execute(get);  
		} else if(post != null){
	        response = httpclient.execute(post);  
		}
		
		return response;
		
	}	
	
	
	
	
	
	
	
	
	
	
	public String posTstring(String url,List<BasicNameValuePair> params) throws Exception{
		HttpPost post = mypost(url,params);		
		HttpResponse  response = getResponse(post,null);  
		return resPonsToString(response);
	}
	
	public void postToFile(String url,String filename,List<BasicNameValuePair> params) throws Exception{
		HttpPost post = mypost(url,params);		
		HttpResponse  response = getResponse(post,null); 
		responseToFile(response,filename);
	}
	
	public String reqString(String inputUrl) throws Exception{
		 
		HttpResponse response =  getResponse(null,myReq(inputUrl));
        return resPonsToString(response);
	}

	public void getToFile(String url,String filename) throws Exception{
		HttpResponse response =  getResponse(null,myReq(url));
		responseToFile(response,filename);
	}
	public String ListToMyString(List<String[]>  input){	
		String output = "[startdocumentsimplesave]";		
		for(String[] sM:input){ 
			for(String item:sM){ 
				output +=item;
				output +="[dalamitem]"; 
			} 
			output += "[dalamlist]";
			
		}		
		return output;
	}
}