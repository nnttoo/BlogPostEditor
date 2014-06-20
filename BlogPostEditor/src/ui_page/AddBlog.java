package ui_page;
 
import java.io.File;

import netscape.javascript.JSObject;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import application.JsListener;
import application.MainFrame;
import application.WebViewPage;

public class AddBlog {
	MainFrame mf;
	WebViewPage wp;
	JSObject jsCallback;
	String folderpath;
	
	public AddBlog(MainFrame mfIn,JSObject jsCallbackin,String pathIn){
		jsCallback = jsCallbackin;
		mf = mfIn;
		wp = new WebViewPage(new Stage(), "addblog.html", new AddBlogJs());
		wp.stage.setWidth(500);
		wp.stage.setHeight(600);
		wp.stage.setTitle("Add New Blog");
		wp.stage.show();
		folderpath = pathIn;
	}	
	
	public class AddBlogJs extends JsListener{
		public String browseFolder(){
			String result = null;
				DirectoryChooser dc = new DirectoryChooser();
				dc.setInitialDirectory(mf.curFolder.workspace);
				File selectedFolder = dc.showDialog(wp.stage);
				if(selectedFolder !=null && selectedFolder.exists() && selectedFolder.isDirectory()){
					
					result = selectedFolder.getPath();
				}
			
			return result;
		}
		
		public boolean checkBlogDir(String blogDirPath){
			boolean result = false;
			File testFile = new File(blogDirPath);
			if(testFile.exists()){
				result = true;
			}			 
			return result;
		}
		
		public String jsonBloglist(){
			return mf.fr.read(mf.curFolder.acclist)	;	
		}
		
		public String readFile(String filepath){
			return mf.fr.read(filepath);
		}
		
		public void saveBlog(String folderBlog,String mainConf, String blogParam){
			mf.fr.write(mf.curFolder.acclist, mainConf);
			mf.fr.write(folderBlog + "/config.json",  blogParam); 
			jsCallback.call("run");
			wp.stage.close();
		}
		
		public String getFolderPath(){
			return folderpath;
		}
		
		public void closePage(){ 
			wp.closeWeb();
		}
	}
}
