package blog.blogger;
 
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 

import org.apache.http.message.BasicNameValuePair;  

import blog.AllConfig.UserConfig;
import tools.my_httpreq;
import javafx.application.Platform; 
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;  
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;  
import javafx.stage.WindowEvent;

public class GoogleLogin{
	WebView webview;
	WebEngine webEngine;
	Stage webstage;
	String tokenfile;
	
	String oauthurl;
	String scope;
	String clientid;
	String redirect;
	String clientsecret;
	String validate_code;
	CallbackLogin callbacklogin;

	UserConfig userconfig;
	
	boolean started = false;
	
	public GoogleLogin(String tokenfileIn, CallbackLogin callbackloginIn,UserConfig userconfIn){
		callbacklogin = callbackloginIn;
		userconfig = userconfIn;
		
		tokenfile = tokenfileIn;
		oauthurl = "https://accounts.google.com/o/oauth2/auth?";
		validate_code = "https://accounts.google.com/o/oauth2/token";
		scope = "https://www.googleapis.com/auth/userinfo.profile https://picasaweb.google.com/data/ https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/blogger";
		clientid = "1051450073876-thnb6o2r6sege048cqibdmmmhhonvvb7.apps.googleusercontent.com";
		redirect = "http://localhost";
		clientsecret = "D0YDZKO1l4ZYkymRq-PSyHIO";
		
		Platform.runLater(new Runnable() {
			public void run() {
				startWeb();
			}
		});
	}
	
	public interface CallbackLogin{
		public void call();
		public void cancel();
	}
	
	private void getTokenBycode(String code){
		my_httpreq httpreq = new my_httpreq();
		
		List<BasicNameValuePair> parampost = new ArrayList<BasicNameValuePair>();
		parampost.add(new BasicNameValuePair("code", code));
		parampost.add(new BasicNameValuePair("client_id", clientid));
		parampost.add(new BasicNameValuePair("client_secret", clientsecret));
		parampost.add(new BasicNameValuePair("redirect_uri", redirect));
		parampost.add(new BasicNameValuePair("grant_type", "authorization_code"));
		try{
		httpreq.postToFile(validate_code,tokenfile,  parampost); 
		} catch(Exception e){e.printStackTrace();}
		
		callbacklogin.call();
	}
	private void startWeb(){ 	
		webstage = new Stage();
		webview = new WebView();
		webEngine = webview.getEngine();
		
		webstage.setScene(new Scene(webview));
		webstage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent event) { 
				if(!started){
				callbacklogin.cancel();
				}
			}
		});
		
		String url =  oauthurl + "scope=" + scope + "&client_id=" + clientid + "&redirect_uri=" + redirect + "&response_type=code";
		webEngine.load(url);
		webstage.show();
		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

			public void changed(ObservableValue<? extends State> observable,
					State oldValue, State newValue) {
				
					if(newValue == State.SUCCEEDED){ 
						
						String currentUrl = webEngine.getLocation();
						boolean matchG = currentUrl.matches("https:\\/\\/accounts.google.com\\/ServiceLogin\\?service.*"); 
						if(matchG){
						System.out.println(currentUrl);
						webEngine.executeScript("document.getElementById('Email').value='"+userconfig.username+"'; "
										+ "document.getElementById('Passwd').value='"+userconfig.password+"'; "
										+ "document.getElementById('gaia_loginform').submit(); "
										+ ""); 
						}
						
						if(currentUrl.matches("https://accounts.google.com/o/oauth2/a.*")){
							
							webEngine.executeScript(""
									+ "var buttonClick = document.getElementById('submit_approve_access'); "
									+ "setInterval(function(){" 
									+ " if(!buttonClick.hasAttribute('disable')){"
									+ " buttonClick.click();"
									+ "} "
									+ "},1000);"); 
						}
						
					}
					 
					String location = webEngine.getLocation();
					
					Pattern regex = Pattern.compile("http:\\/\\/localhost\\/\\?code=(.*)");
 
					Matcher match = regex.matcher(location);
					if(match.find()){
						final String code = match.group(1);
						new Thread(new Runnable() {
							
							public void run() {
								if(!started){
									started = true;
									getTokenBycode(code);		
									System.out.println("start");
								
								}
							}
						}).start();
						webEngine.load("about: blank");   
						webstage.close();
					}
			}
		 
		}); 
	}  
}