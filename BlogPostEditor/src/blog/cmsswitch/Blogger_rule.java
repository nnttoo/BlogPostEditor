package blog.cmsswitch;
 
import netscape.javascript.JSObject;
import blog.AllConfig.FromServerInfo;
import blog.AllConfig.UserConfig;  
import blog.postDBContent;
import blog.blogger.Blogger_api;
import blog.blogger.Blogger_checklogin;

public class Blogger_rule extends BlogRule { 
	public Blogger_rule(UserConfig userclassIn) {		
		super(userclassIn);  
	}
	
	@Override
	public void syncronizeBlog(final JSObject callback) { 
		class SyncBlog extends Blogger_checklogin{
			public SyncBlog() {
				super(userconfig, callback); 
			}			
			@Override
			public void afterLogin() { 
				super.afterLogin();
				new Blogger_api(userconfig,access_token).getBlogInfo(callback);
			}			
		}
		new SyncBlog().startConnect();
	}
	
	@Override
	public void getRecentPost(final FromServerInfo fromservInfo,final String max,final JSObject callback) {
		class GetRecentPostClass extends Blogger_checklogin{ 
			public GetRecentPostClass() {
				super(userconfig, callback); 
			}
			@Override
			public void afterLogin() { 
				super.afterLogin();
				new Blogger_api(userconfig, access_token).getRecentPost(fromservInfo, max, callback);
			} 
		}		
		new GetRecentPostClass().startConnect();
	}
	@Override
	public void publish(final FromServerInfo fromservinfo, final postDBContent param, final JSObject callback) { 
		class PublishClass extends Blogger_checklogin{
			public PublishClass(){
				super(userconfig, callback); 
			}
			@Override
			public void afterLogin() { 
				super.afterLogin();
				new Blogger_api(userconfig, access_token).publish(fromservinfo, param, callback);
			} 
		}
		new PublishClass().startConnect();
	}
	
 
	@Override
	public void uploadImage(FromServerInfo fromservinfo, final String imgbase64,	final String filename, final String filetype,final JSObject callback) { 
		class UploadImageLogin extends Blogger_checklogin{
			public UploadImageLogin(){
				super(userconfig, callback); 
			}
			@Override
			public void afterLogin() { 
				super.afterLogin();
				new Blogger_api(userconfig, access_token).uploadImage(imgbase64, filename, filetype, callback);
			}
		}	 
		new UploadImageLogin().startConnect();
	} 
	
	@Override
	public void deletePost(final FromServerInfo fromservinfo, final String postid,
			final JSObject callback) { 
		class DeleteReq extends Blogger_checklogin{
			public DeleteReq(){
				super(userconfig, callback); 
			}
			@Override
			public void afterLogin() { 
				super.afterLogin();
				new Blogger_api(userconfig, access_token).deletePost(fromservinfo, postid, callback);
			}
			
		}
		new DeleteReq().startConnect(); 
	}
	 
}
