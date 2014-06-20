package application; 
import java.io.File;   
import java.util.ArrayList; 
import java.util.List; 
 



import netscape.javascript.JSObject;  
import blog.AllConfig;
import blog.AllConfig.FromServerInfo;
import blog.AllConfig.UserConfig;
import blog.PostDb;    
import blog.postDBContent;
import blog.cmsswitch.BlogRule;
import blog.cmsswitch.BlogSwitch;
import tools.FileDirReader;
import tools.FileReader;
import ui_page.AddBlog;  
import ui_page.Delete_page;
import ui_page.Editor_page; 
import javafx.application.Application;  
import javafx.stage.Stage;

public class MainFrame extends Application{
	WebViewPage wp;
	public FileDirReader curFolder;
	public FileReader fr;
	
	public static void main(String[] args) {
	 launch(args);

	}

	public void start(Stage primaryStage) throws Exception {	
		curFolder = new FileDirReader();
		fr = new FileReader();
		
		wp = new WebViewPage(primaryStage, "index.html", new MainJsListener());
		wp.stage.setWidth(700);
		wp.stage.setHeight(700);
		wp.stage.setTitle("Blog Post Editor");
		wp.stage.show();

	 
	}  
	 
	
	public class MainJsListener extends JsListener{  
		public void addBlog(JSObject callback,String path){
			new AddBlog(MainFrame.this,callback,path);
		} 
		

		public String accList(){ 
			return fr.read(curFolder.acclist);	 
		}
		
		public String readBlogConfig(String blogDir){
			return fr.read(blogDir + "/config.json");
		}
		
		public String readBlogAccInfo(String blogdir){
			return fr.read(blogdir  + "/fromserverinfo.json");
		} 
		public void openEditor(  
				String blogdir,
				int jsonfromserverpos,
				String dbpostIdIn,
				JSObject callbackIn
				
				){
			
			UserConfig user = AllConfig.getUserConfig(blogdir);
			FromServerInfo fromserv = AllConfig.getFromServerInfo(blogdir, jsonfromserverpos);
			new Editor_page(user,fromserv,dbpostIdIn,callbackIn );
		}
		
		public void openDelete_page(
				String blogdir,
				int jsonfromserverpos,
				String dbpostIdIn,
				JSObject callbackIn
				){
			UserConfig user = AllConfig.getUserConfig(blogdir);
			FromServerInfo fromserv = AllConfig.getFromServerInfo(blogdir, jsonfromserverpos);
			new Delete_page(user, fromserv, dbpostIdIn, callbackIn);
		}
		
		public void syncronizeBlog(String blogdir, final JSObject callback){  
			final BlogRule connector = BlogSwitch.getBlogRule(AllConfig.getUserConfig(blogdir));
 
			
			new Thread(new Runnable() {				
				public void run() {
					connector.syncronizeBlog(callback);
				}
			}).start();
		}
		
		public void getRecentPost( 
				String blogdir,
				int jsonfromserverid,
				final String max,
				final JSObject callback
				){
			 
				
			final BlogRule connector = BlogSwitch.getBlogRule(AllConfig.getUserConfig(blogdir)); 
			final FromServerInfo fromserverinfo = AllConfig.getFromServerInfo(blogdir, jsonfromserverid);
			new Thread(new Runnable() {				
				public void run() {
					connector.getRecentPost(fromserverinfo, max, callback);				
				}
			}).start();
			
		}
		
		 
		public List<postDBContent> getPostList(String dbfile,String startpage,String type){
			List<postDBContent> result = new ArrayList<postDBContent>();
			File teFile = new File(dbfile);
			if(teFile.exists()){
				
				PostDb db = new PostDb(dbfile);   
					result = db.getRecentPost(startpage,type);  
			
			} 
			return result;
		} 
		

		public String getCountPost(String dbfile,String type){
			String result = "0";
			File teFile = new File(dbfile);
			if(teFile.exists()){
				
				PostDb db = new PostDb(dbfile);  
				result = db.countPost(type);
			
			} 
			
			return result;
		}
	}

}
