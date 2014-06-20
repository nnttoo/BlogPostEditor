package application;
  
 

import java.awt.Desktop;
import java.net.URI;

import javafx.stage.Stage; 
  
public class JsListener { 
	
	public void log(String input){
		
		System.out.println(input);
	} 
	
	public JsListener openListener(){
		return new JsListener();
	}

	public void openPage(String htmlfilename,String title,double width,double height){
		WebViewPage openwp = new WebViewPage(new Stage(), htmlfilename, new JsListener());
		openwp.stage.setHeight(height);
		openwp.stage.setWidth(width);
		openwp.stage.setTitle(title);
		openwp.stage.show();
	}
	
	public void openOnline(final String url){
		new Thread(new Runnable() { 
			public void run() {
				try {
					URI uritoopen = new URI(url);
					 Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) { 
					        desktop.browse(uritoopen); 
					    }
				} catch (Exception e) { 
					e.printStackTrace();
				} 
			}
		}).start();
		
	}
}
