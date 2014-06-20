package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class FileReader {
	public void write(String path,String input){  
		FileOutputStream fos = null;
     	File file = new File(path);		
     	try {
			fos = new FileOutputStream(file);
			fos.write(input.getBytes());
	        fos.flush();
	        fos.close(); 
		} catch (Exception e) { 
			e.printStackTrace();
		}    	        
	} 
	
	public String read(String filepath){
		String readString = null;
		
		try{
		FileInputStream fis = new FileInputStream(filepath); 
        InputStreamReader isr = new InputStreamReader(fis);        
        StringBuilder sb = new StringBuilder();
        
        char[] data = new char[2048];
        int l;
        while ((l = isr.read(data)) != -1) {
        	sb.append(data, 0, l);
        }         
        readString =  sb.toString();
        fis.close();
		} catch(Exception e){
			
			//e.printStackTrace();
		}
	return readString;
	}
}
