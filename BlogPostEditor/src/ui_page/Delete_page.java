package ui_page;
 
import tools.Calljsfrombg;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import application.JsListener;
import application.WebViewPage;
import blog.AllConfig.FromServerInfo;
import blog.AllConfig.UserConfig;
import blog.PostDb;
import blog.postDBContent;
import blog.cmsswitch.BlogRule;
import blog.cmsswitch.BlogSwitch;

public class Delete_page {
	UserConfig userconfig;
	FromServerInfo fromserverinfo;
	String postid;
	JSObject callback;
	
	PostDb postdb;
	WebViewPage wp;
	
	String BlogPostId;
	String titlePost;
	
	public Delete_page(UserConfig userConfIn,FromServerInfo fromservIn, String postIdin, JSObject callbackIn){
		userconfig = userConfIn;
		fromserverinfo = fromservIn;
		postid = postIdin;
		callback = callbackIn;
		
		postdb = new PostDb(userconfig.blogdir + fromserverinfo.getDBFilename()); 
		setBlogPostId();
		
		wp = new WebViewPage(new Stage(), "delete_post.html", new DeleteJSListener()); 
		wp.stage.setHeight(300);
		wp.stage.setWidth(600);
		wp.stage.setTitle("Delete Post");
		wp.stage.show();
		
	}
	
	private void setBlogPostId(){ 
		postDBContent postfromdb =  postdb.getPostById(postid);   
			BlogPostId = postfromdb.postId;
			titlePost = postfromdb.title; 
	}
	
	public class DeleteJSListener extends JsListener{ 
		
		public String titlepost = titlePost;
		public String postid = BlogPostId;
		
		public void DeleteOnline(final JSObject callbackDelete){
			final BlogRule blogrule = BlogSwitch.getBlogRule(userconfig);
			new Thread(new Runnable() {				
				public void run() { 
					blogrule.deletePost(fromserverinfo, BlogPostId, callbackDelete);
					postdb.deletePostByBlogPostId(BlogPostId);
				}
			}).start();
		}
		
		public void DeleteLocally(final JSObject callbackDelLocally){
			new Thread(new Runnable() {				
				public void run() { 
					postdb.deletePostByBlogPostId(BlogPostId);
					Calljsfrombg.call(callbackDelLocally, "finish", null);
				}
			}).start();
		}
		
		public void closeDelPage(){
			callback.call("finish");
			wp.closeWeb();
		}
	}
	 
}
