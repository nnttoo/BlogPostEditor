package blog.cmsswitch;

import java.util.HashMap;

import javafx.stage.Stage;

import org.apache.http.HttpResponse; 
import org.json.JSONArray; 

import tools.Calljsfrombg;
import tools.my_httpreq;
import netscape.javascript.JSObject;
import application.AlertClass;
import blog.AllConfig;
import blog.AllConfig.FromServerInfo;
import blog.AllConfig.UserConfig;
import blog.PostDb;   
import blog.postDBContent;
import blog.wordpress.WP_multi_xmlpost_todb;
import blog.wordpress.WP_single_xmlpost_todb; 
import blog.wordpress.WP_xmlparse_afterpost;
import blog.wordpress.WP_xmlparsetojson;
import blog.wordpress.WordpresXMLRPC;
import blog.wordpress.Wp_xmlparse_imgupload;

public class Wordpress_rule extends BlogRule {

	public Wordpress_rule(UserConfig userconfigIn) {
		super(userconfigIn);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void syncronizeBlog( JSObject callback) { 

		Calljsfrombg.call(callback,"report", "connecting to blog");
		
		WordpresXMLRPC rpc = new WordpresXMLRPC(userconfig);
		String response = rpc.getBlogInfo(); 
		
		
		WP_xmlparsetojson parser = new WP_xmlparsetojson();
		String json = parser.parsetoJson(response);
		String jsonPath = userconfig.blogdir + "/fromserverinfo.json"; 		
		filereader.write(jsonPath, json);
		try{

			JSONArray blogjason = new JSONArray(json);
			if(blogjason != null && blogjason.length() > 0){
				for(int i=0;i<blogjason.length();i++){  
					
					FromServerInfo fromserverinfo_for_cat = AllConfig.getFromServerInfo(userconfig.blogdir, i);
					
					HttpResponse catresponse = rpc.getCategory(fromserverinfo_for_cat);
					my_httpreq httpreq = new my_httpreq();
					String resultkey = httpreq.resPonsToString(catresponse);				
					String jsonkey = parser.parsetoJson(resultkey); 
	 
					String categoryPath = userconfig.blogdir +  fromserverinfo_for_cat.getCatFilename();
					filereader.write(categoryPath, jsonkey);
					
				}
			} else {
				new AlertClass("ada masalah dengan koneksi blog<br/> periska password, username, dan koneksi internet", new Stage()); 
			}
		}catch(Exception e){e.printStackTrace();}
		

		Calljsfrombg.call(callback,"finish", null);
	}
	
	@Override
	public void getRecentPost(FromServerInfo fromservInfo,String max,JSObject callback) { 
		Calljsfrombg.call(callback,"report", "connecting to blog");
		WordpresXMLRPC rpc = new WordpresXMLRPC(userconfig);
		HttpResponse response  = rpc.getRecentPost(max,fromservInfo);
		
		
		Calljsfrombg.call(callback,"report", "parse response from server"); 
		

		PostDb postdb = new PostDb(userconfig.blogdir + fromservInfo.getDBFilename());
		WP_multi_xmlpost_todb multixmltodb = new WP_multi_xmlpost_todb();
		
		multixmltodb.postTodb(postdb, response,callback);
		Calljsfrombg.call(callback,"finish", null);
	}
	
	@Override
	public void uploadImage(FromServerInfo fromservInfo,String imgbase64, String filename,
				String filetype,JSObject callback) {
		
		Calljsfrombg.call(callback,"report","uploading " + filename);
		String result = null;
		WordpresXMLRPC rpc = new WordpresXMLRPC(userconfig);
		String imgupload = rpc.uploadImage(fromservInfo,imgbase64, filename, filetype);
		 
		HashMap<String, String> uploadResponse = Wp_xmlparse_imgupload.parse(imgupload);
		result = uploadResponse.get("url");				
		Calljsfrombg.call(callback,"finish",result);
	}
	
	@Override
	public void publish(FromServerInfo fromservinfo,postDBContent param, JSObject callback) { 
		Calljsfrombg.call(callback,"report", "publish post... please wait");
		WordpresXMLRPC rpc = new WordpresXMLRPC(userconfig);
		String xmlresponse = rpc.publish(fromservinfo,param);  
		String editedPostid = param.postId;
		
		if(editedPostid.isEmpty()){
			editedPostid = WP_xmlparse_afterpost.parse(xmlresponse);
		} 
		Calljsfrombg.call(callback,"report", "save edited post to database");
		
		HttpResponse getEditedpost = rpc.getPostByID(fromservinfo,editedPostid);
		WP_single_xmlpost_todb singlexmltodb = new WP_single_xmlpost_todb();
		
		String dbpath = userconfig.blogdir + fromservinfo.getDBFilename(); 
		singlexmltodb.postTodb(new PostDb(dbpath), getEditedpost,callback,param);
		
	}
	
	@Override
	public void deletePost(FromServerInfo fromservinfo,String postid,JSObject callback) { 
		Calljsfrombg.call(callback, "report", "delete post id :" + postid);
		WordpresXMLRPC rpc = new WordpresXMLRPC(userconfig);
		rpc.deletePost(fromservinfo, postid);
		Calljsfrombg.call(callback, "finish", null);
	}
	
	 
}
