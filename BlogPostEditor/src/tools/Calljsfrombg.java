package tools;

import javafx.application.Platform;
import netscape.javascript.JSObject;

public class Calljsfrombg{
	
	public static <E>void call(final JSObject jsobj, final String fungtionname,final E argument){
		Platform.runLater(new Runnable() {	
			public void run() {
				if(argument !=null){
					jsobj.call(fungtionname,argument);	
				} else {
					jsobj.call(fungtionname);		
				}
			}
		}); 
		
	}
}