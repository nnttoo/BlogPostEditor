package application;
   
 

import netscape.javascript.JSObject; 
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State; 
import javafx.event.EventHandler;
import javafx.scene.Scene;  
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine; 
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView; 
import javafx.stage.Stage; 
import javafx.stage.WindowEvent; 

public class WebViewPage {
	String htmlfile;
	public WebEngine webengine;
	WebView mywebview;
	public Stage stage;
	
	public WebViewPage(Stage stageIn,String localUrl, final JsListener jsInput){ 
		stage = stageIn;
		
		mywebview = new WebView(); 
		webengine = mywebview.getEngine(); 
		mywebview.setCache(false);
		Scene myscane = new Scene(mywebview);
		stage.setScene(myscane); 
		
		stage.getIcons().add(
				   new Image(
						   WebViewPage.class.getResourceAsStream( "icon.png" ))); 
		/* 
		
		URI htmlUrl;
		try {
			htmlUrl =  ClassLoader.getSystemClassLoader().getResource("").toURI();
			//System.out.println(htmlUrl);
			URI realhtmlurl = (htmlUrl.toString().endsWith("/bin/"))? htmlUrl.resolve(".."):htmlUrl;
			 System.out.println(realhtmlurl);
			webengine.load(realhtmlurl + "html/" + localUrl); 
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		*/
		String urlString = ClassLoader.getSystemResource(localUrl).toExternalForm();
		webengine.load(urlString); 
		
		 
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent arg0) {
				System.out.println("closing webview ");
				webengine.load("about: blank");   
				System.gc();
				
			}
			 
		});
		 
		 webengine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

			public void changed(ObservableValue<? extends State> observable,
					State oldValue, State newValue) { 

				 if(newValue == State.SUCCEEDED){
			 JSObject jsobj = (JSObject) webengine.executeScript("window");						 
			 jsobj.setMember("java", jsInput);    
			 }
			}
		 
		}); 
		
		webengine.setOnAlert(new EventHandler<WebEvent<String>>() {

			public void handle(WebEvent<String> event) {
				new AlertClass(event.getData(), stage);		
				
			}
			
			 
		});
		 
	}
	

	
	public void closeWeb(){
		System.out.println("closing webview");
		webengine.load("about: blank");   
		System.gc();
		stage.close();
	}
	 
}
