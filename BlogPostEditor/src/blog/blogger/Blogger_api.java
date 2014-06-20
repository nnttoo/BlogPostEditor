package blog.blogger;
 
import java.util.Base64; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse; 
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity; 
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import blog.PostDb; 
import blog.AllConfig.FromServerInfo;
import blog.AllConfig.UserConfig;  
import blog.postDBContent;
import tools.Calljsfrombg;
import tools.FileReader;
import netscape.javascript.JSObject;

public class Blogger_api { 
	Blogger_http b_http;
	FileReader fr;
	UserConfig userconfig;
	
	public Blogger_api(UserConfig userconfigIn,String acc_token){
		b_http = new Blogger_http(acc_token); 
		fr = new FileReader();
		userconfig = userconfigIn;
	}
	
	public void getBlogInfo(final JSObject callback){   
		final String bloginfopath = userconfig.blogdir + "/fromserverinfo.json"; 
		Calljsfrombg.call(callback,"report","get blogs from google");
		HttpResponse response =  b_http.get("https://www.googleapis.com/blogger/v3/users/self/blogs");
		String response_string = b_http.resPonsToString(response);
		
		try { 
			JSONObject jsobject = new JSONObject(response_string);
			JSONArray items = jsobject.getJSONArray("items"); 
			JSONArray resultjson = new JSONArray();
			
			for(int i=0; i<items.length();i++){
				JSONObject tiapitem = items.getJSONObject(i);
				
				JSONObject itemObj = new JSONObject();
				String blogname =  tiapitem.getString("name");
				
				String blogidtiap = tiapitem.getString("id");
				
				String url = tiapitem.getString("url");
				

				itemObj.put("blogName", blogname);
				itemObj.put("xmlrpc", "");
				itemObj.put("isAdmin", "1");
				itemObj.put("blogid", blogidtiap);
				itemObj.put("url", url);
				
				resultjson.put(itemObj);
			}
			
			String resultjsonstring = resultjson.toString();
			fr.write(bloginfopath, resultjsonstring);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		

		Calljsfrombg.call(callback,"report","finish..");
		Calljsfrombg.call(callback,"finish",null); 
		 
	}
	
	public void getRecentPost(final FromServerInfo fromservInfo,final String max,final JSObject callback){ 
		final jsonToDb jsontodb = new jsonToDb(new PostDb(userconfig.blogdir + fromservInfo.getDBFilename()));
		class getRecent{
			int maxpage = 0;
			public getRecent(){
				maxpage = Integer.parseInt(max);
			}
			
			public void startGEt(String pagetoken){
				if(maxpage > 0 && pagetoken!=null){
					maxpage = maxpage - 10;
					Calljsfrombg.call(callback, "report", "get page : " + maxpage); 
					String nextUrlParam = (pagetoken.equals("start"))? "":"?pageToken=" + pagetoken;					
					HttpResponse response = b_http.get("https://www.googleapis.com/blogger/v3/blogs/"+fromservInfo.blogid+"/posts" + nextUrlParam);
						String result = b_http.resPonsToString(response);  
						JSONObject postObj = new JSONObject(result);
						JSONArray items = null;
						String nextpageToken = null;
								try{
									nextpageToken= postObj.getString("nextPageToken");
									items = postObj.getJSONArray("items");
								}catch(Exception e){} 
								
						if(items !=null){
							Calljsfrombg.call(callback, "report", "Save post to db"); 
							for(int i=0;i<items.length();i++){
								JSONObject tiapitem = items.getJSONObject(i);
								if(tiapitem !=null){
									jsontodb.save(tiapitem,new postDBContent());								
								}
							}
							 
							startGEt(nextpageToken);
						}
				}	 
			}
			
		}
	
		new getRecent().startGEt("start");
		Calljsfrombg.call(callback, "finish", null); 
	}
	
	public void deletePost(FromServerInfo fromservinfo,String blogpostid, JSObject callback){
		String deleteUrl = "https://www.googleapis.com/blogger/v3/blogs/"+fromservinfo.blogid+"/posts/"+blogpostid;
		HttpResponse response = b_http.delete(deleteUrl);
		System.out.println(response);
		Calljsfrombg.call(callback, "finish", null);
	}
	
	public void publish( FromServerInfo fromservInfo, postDBContent param, JSObject callback){ 		
		jsonToDb jsontodb = new jsonToDb(new PostDb(userconfig.blogdir + fromservInfo.getDBFilename()));
		JSONObject postObj = new JSONObject();
		postObj.put("kind", "blogger#post");
			JSONObject blogobj = new JSONObject();
			blogobj.put("id",fromservInfo.blogid);
		postObj.put("blog", blogobj);
		postObj.put("title", param.title);
		postObj.put("content", param.content);
		
		if(!param.keyword.isEmpty()){
			String[] keyword = param.keyword.split(",");			
			postObj.put("labels", keyword);
		}
		
		String editUrl = "";
		if(!param.postId.isEmpty()){
			editUrl = param.postId;
		}
		
		String jsonstring = postObj.toString(); 
		String url = "https://www.googleapis.com/blogger/v3/blogs/"+fromservInfo.blogid+"/posts/" + editUrl;

		
		try{
		ByteArrayEntity entity = new ByteArrayEntity(jsonstring.getBytes());
		entity.setContentType("application/json");
		
		HttpResponse response = null;
		if(param.postId.isEmpty()){
			response = b_http.post(url, entity);
		} else {
			response =  b_http.put(url, jsonstring);
		}
			String result =  b_http.resPonsToString(response); 
			
			if(result.length() > 5){
				JSONObject resultObj = new JSONObject(result); 
				jsontodb.save(resultObj,param);
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void uploadImage(final String baseImage,	final String filename, final String filetype, final JSObject callback ){
		
		class UploadImageClass{			
			public UploadImageClass(){
				String albumidPath = userconfig.blogdir + "/picasa_album.json";
				String albumidFromfile = fr.read(albumidPath); 
				
				if(albumidFromfile ==null){
					String albumname = "blogtoolimage"; 
					String  xmlcreate = "<entry xmlns='http://www.w3.org/2005/Atom' xmlns:media='http://search.yahoo.com/mrss/' xmlns:gphoto='http://schemas.google.com/photos/2007'>"+
							  "<title type='text'>"+albumname+"</title>"+
							  "<summary type='text'>"+albumname+"</summary>"+
							  "<gphoto:access>public</gphoto:access>"+
							  "<category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/photos/2007#album'></category>"+
							"</entry>"; 
					try{ 
						Calljsfrombg.call(callback,"report","Create new Album :" + albumname);
						ByteArrayEntity entity = new ByteArrayEntity(xmlcreate.getBytes());
						entity.setContentType("application/atom+xml");						
						HttpResponse response = b_http.post("https://picasaweb.google.com/data/feed/api/user/" + userconfig.username, entity);  
						Header[] headers = response.getAllHeaders();
						for (Header header : headers) {
							String headername = header.getName();
							String value = header.getValue(); 
							if(headername.equals("Content-Location")){
								Matcher match = Pattern.compile("albumid\\/(.*)?\\/").matcher(value);
								if(match.find()){
									String albumid = match.group(1);
									if(albumid !=null){
										fr.write(albumidPath, albumid);
										uploadImage(albumid);
									}
								}
								
							
							}
						}
						
					}catch(Exception e){
						
						Calljsfrombg.call(callback, "report", "upload image error");
						Calljsfrombg.call(callback, "finish", null);
						e.printStackTrace();
						
					}
					
				} else {
					try {
						uploadImage(albumidFromfile);
					} catch (Exception e) { 
						Calljsfrombg.call(callback, "report", "upload image error");
						Calljsfrombg.call(callback, "finish", null);
						e.printStackTrace();
					}
				}
			}
			
			private void uploadImage(String albumid) throws Exception{
				System.out.println("album id: " + albumid);
				Calljsfrombg.call(callback,"report","uploading " + filename);
				byte[] imageByte = Base64.getDecoder().decode(baseImage.getBytes());
				ByteArrayEntity entity = new ByteArrayEntity(imageByte);
				entity.setContentType(filetype); 
				String albumurl= "https://picasaweb.google.com/data/feed/api/user/" + userconfig.username + "/albumid/" + albumid;
				
				HttpPost 	post = new HttpPost(albumurl);
							post.setHeader("Authorization", "Bearer " + b_http.access_token);  
							post.setHeader("Content-Type",filetype);
							post.setHeader("Slug",filename);
							post.setEntity(entity);
				

				HttpClient client   =  HttpClientBuilder.create().build();	
				HttpResponse response =  client.execute(post);  
				Header[] header = response.getAllHeaders();
				for(Header head : header){

					if(head.getName().equals("Content-Location")){ 
						HttpResponse getUrlofImage = b_http.get(head.getValue()); 
						String resultOfurlofimage = b_http.resPonsToString(getUrlofImage); 
						Matcher match = Pattern.compile("src.*?https:\\/\\/(.*?\\.googleusercontent\\.com\\/.*?)['\"]").matcher(resultOfurlofimage);
						if(match.find()){ 
							String imageURl = match.group(1);
							imageURl = imageURl.replaceFirst("(.*)\\/(.*)$", "http://$1/s1500/$2");
							Calljsfrombg.call(callback, "finish", imageURl);
							
						}
					} 
				}
				
			}
			
		}
		new UploadImageClass();
	} 
	
	class jsonToDb{
		public String[] jsonkey; 
		PostDb dbpost; 
		
		public jsonToDb(PostDb dbpostIn){ 
			dbpost = dbpostIn; 
		} 
		public void save(JSONObject json,postDBContent dbcontent){  
			try{ dbcontent.postId = json.getString("id");  }catch(Exception e){e.printStackTrace();}
			try{ dbcontent.title = json.getString("title");  }catch(Exception e){e.printStackTrace();}
			try{ dbcontent.content = json.getString("content");  }catch(Exception e){e.printStackTrace();}
			try{ dbcontent.date = json.getString("published");  }catch(Exception e){e.printStackTrace();}
			try{ dbcontent.permalink = json.getString("url");  }catch(Exception e){e.printStackTrace();}
			
			 
			dbcontent.post_status = ""; 
			String keyword = "";
			try{
			JSONArray keywordjson = json.getJSONArray("labels");
			if(keywordjson !=null){
				for(int ki=0;ki<keywordjson.length();ki++){
					String keywordkurent = keywordjson.getString(ki);
					if(keywordkurent !=null){
						String sp = (keyword.length()>1)? ",":"";											
						keyword += sp + keywordkurent;
						
					}
				}
				
			}			 
			dbcontent.keyword = keyword;
			
			
			} catch(Exception e){ }  
			dbpost.SaveNewPost(dbcontent); 
		}  
	} 
	
}
