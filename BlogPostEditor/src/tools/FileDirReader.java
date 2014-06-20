package tools;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import application.AlertClass;

public class FileDirReader { 
	public File workspace;
	public String acclist;
	
	public FileDirReader(){
		URI currentDir = null;
		try {
			currentDir = ClassLoader.getSystemResource("").toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		File dirFolder = new File(currentDir);
		dirFolder.mkdirs();
		
		if(dirFolder.exists()){
			workspace = new File(currentDir.getPath() + "workspace/");
			if(!workspace.exists()){				
				workspace.mkdirs();
			}
			
		} 
		
		if(!workspace.exists()){
			
			new AlertClass("workspace folder notfound", null);
		} else {
			 acclist = workspace+"/acclist.json";
			
		}
	}

}
