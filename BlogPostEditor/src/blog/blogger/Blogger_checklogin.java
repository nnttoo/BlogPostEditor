package blog.blogger;

import java.io.File;

import org.json.JSONObject;

import netscape.javascript.JSObject;
import tools.Calljsfrombg;
import tools.FileReader;
import blog.AllConfig.UserConfig;
import blog.blogger.GoogleLogin.CallbackLogin;

public class Blogger_checklogin {
	String tokenfilepath;
	UserConfig userconfig;
	JSObject callback;
	public String access_token;
	
	public Blogger_checklogin(UserConfig userconfigIn,JSObject callbackin){
		userconfig = userconfigIn;
		tokenfilepath = userconfig.blogdir + "/token.json";
		callback = callbackin;
	}
	
	
	private boolean checkExpire(String filepath){ // true == belum expire
		File tokenfile = new File(filepath);
		if(!tokenfile.exists()){
			return false;
		} else { 
			Long filetime = tokenfile.lastModified() / 1000;
			Long timenowSecond = System.currentTimeMillis() / 1000; 
			Long waktuExpire = (long) (59 * 60); 
				 filetime = (long)( filetime + waktuExpire); 
			if(filetime > timenowSecond){
				return true;
			} else {
				return false;
			}
		}
	} 
	
	private void setToken(){
		
		FileReader fr = new FileReader();
		String tokenjson = fr.read(tokenfilepath);
		JSONObject tokenobj = new JSONObject(tokenjson);
		access_token = tokenobj.getString("access_token");
	} 
	public void afterLogin(){
		setToken();
		
	}
	
	private void loginTogoogle(){
		new GoogleLogin(tokenfilepath,new CallbackLogin() {				
			public void call() {
				afterLogin();
			}

			public void cancel() { 
				Calljsfrombg.call(callback,"finish",null);
			}
		},userconfig);
	}
	
	public void startConnect(){

		Calljsfrombg.call(callback,"report","check token");
		if(!checkExpire(tokenfilepath)){
			loginTogoogle();
			Calljsfrombg.call(callback,"report","token expire, please login");
		} else {
			afterLogin();
		}
	} 
}
