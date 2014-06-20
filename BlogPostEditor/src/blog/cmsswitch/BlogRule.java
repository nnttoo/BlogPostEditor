package blog.cmsswitch;
 
import netscape.javascript.JSObject;
import tools.FileReader; 
import blog.AllConfig.FromServerInfo;
import blog.AllConfig.UserConfig;  
import blog.postDBContent;

public class BlogRule { 
	public UserConfig userconfig; 
	public FileReader filereader; 
	
	
	public BlogRule( UserConfig userconfigIn){ 
		userconfig = userconfigIn;   
		
		filereader = new FileReader();
	}
	
	public void syncronizeBlog(JSObject callbackIn){  }
	
	public void getRecentPost(FromServerInfo fromservinfo,String max, JSObject callbackIn){ }
	
	public void getPostById(FromServerInfo fromservinfo,String id, JSObject callbackIn){ } 
	
	public void uploadImage(FromServerInfo fromservinfo,String imgbase64,String filename,String filetype, JSObject callbackIn){  }

	public void publish(FromServerInfo fromservinfo,postDBContent param, JSObject callback) { }
	
	public void deletePost(FromServerInfo fromservinfo,String postid,JSObject callback){}
	
} 
