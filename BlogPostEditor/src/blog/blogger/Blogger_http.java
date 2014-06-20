package blog.blogger;
 
 
import org.apache.http.HttpResponse; 
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class Blogger_http{
	public String access_token;
	
	public Blogger_http(String acc_tokenIn){ 
		access_token = acc_tokenIn;
	}
	
	public HttpResponse delete(String url){
		HttpClient client   =  HttpClientBuilder.create().build();	
		HttpDelete delete = new HttpDelete(url);
		HttpResponse result = null;
		delete.setHeader("Authorization", "Bearer " + access_token);
		try{
			result = client.execute(delete);
			
		}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
	public HttpResponse get(String url){
		HttpClient client   =  HttpClientBuilder.create().build();	
		System.out.println("url: " + url);
		System.out.println("token : "+access_token);
		HttpGet get = new HttpGet(url);
		get.setHeader("Authorization", "Bearer " + access_token); 
		HttpResponse result = null;
		try {
			result =  client.execute(get);
		} catch (Exception e) { 
			e.printStackTrace();
		}  
	return result;
	} 
	
	HttpResponse post(String url,ByteArrayEntity entity){
		HttpClient client   =  HttpClientBuilder.create().build();	
		HttpResponse result = null;
		HttpPost post = new HttpPost(url);
		post.setHeader("Authorization", "Bearer " + access_token);  
		try{				
			post.setEntity(entity);
			result = client.execute(post);
			
		}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
	
	HttpResponse put(String url,String json){
		HttpClient client   =  HttpClientBuilder.create().build();	
		HttpResponse result = null;
		HttpPut put = new HttpPut(url);
		put.setHeader("Authorization", "Bearer " + access_token);
		
		try{
			StringEntity entity = new StringEntity(json);
			entity.setContentType("application/json");
			put.setEntity(entity);
			result = client.execute(put);
			
		}catch(Exception e){e.printStackTrace();}
		
		return result;
	} 
	
	public String resPonsToString(HttpResponse response){ 
        String responseString = "";         
        try{
	        responseString = EntityUtils.toString(response.getEntity());
        } catch(Exception e){e.printStackTrace();}
        return responseString;		
	} 
}