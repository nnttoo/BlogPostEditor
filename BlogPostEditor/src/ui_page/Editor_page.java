package ui_page;
 
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
 

import tools.Calljsfrombg;
import tools.FileDirReader;
import tools.FileReader;
import netscape.javascript.JSObject;
import blog.AllConfig;
import blog.AllConfig.FromServerInfo;
import blog.AllConfig.UserConfig;
import blog.PostDb;   
import blog.postDBContent;
import blog.cmsswitch.BlogRule;
import blog.cmsswitch.BlogSwitch;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;  
import javafx.stage.WindowEvent;
import application.JsListener; 
import application.WebViewPage;

public class Editor_page{
	public WebViewPage wp; 
	PostDb db;   
	JSObject callback;
	
	UserConfig userconfig;
	FromServerInfo fromserv;
	FileReader filereader;
	
	postDBContent currentContent;
	
	public Editor_page(UserConfig userConfIn,FromServerInfo fromservIn, String postIdin, JSObject callbackIn){ 
		callback = callbackIn;
		userconfig = userConfIn;
		fromserv = fromservIn; 
			
		   
		wp = new WebViewPage(new Stage(), "editor.html", new EditorJs());
		wp.stage.setTitle("Post Editor");
		wp.stage.setHeight(600);
		wp.stage.setWidth(700);
		wp.stage.show();
		
		prepareDB(postIdin); 

		filereader = new FileReader();	
		prepareOnclose();
	}
	
	private void prepareOnclose(){
		wp.stage.setOnCloseRequest(new  EventHandler<WindowEvent>() {
			
			public void handle(WindowEvent arg0) { 
				System.out.println("call bt"); 
				wp.webengine.load("about: blank");   
				callback.call("finish");
			}
		});
	}
	
	private void prepareDB(String dbpostId){ 		
		 
		String dbpath = userconfig.blogdir + fromserv.getDBFilename();
		db = new PostDb(dbpath);  
		
		if(!dbpostId.isEmpty()){
			currentContent = db.getPostById(dbpostId);
			System.out.println(currentContent.postId);
		} else {
			currentContent = new postDBContent();
		}
	}
	
	public class EditorJs extends JsListener{  
		
		public void openImageFileDialog(JSObject jsobj){ 
			FileChooser myfileChooser = new FileChooser();
			FileChooser.ExtensionFilter imgFilter= new FileChooser.ExtensionFilter("Image ", "*.png","*.jpg"); 			
			myfileChooser.getExtensionFilters().add(imgFilter);
			File selected = myfileChooser.showOpenDialog(wp.stage);
			
			if(selected != null && selected.exists()){ 
				imageBase64(selected.toString(),jsobj);
			}
			 
		} 
		
		public String getCategory(){
			String result = null;
			String categorypath =  userconfig.blogdir + fromserv.getCatFilename();  
			result = filereader.read(categorypath);			
			return result;
		}
		
		public String readFile(String path){
			return filereader.read(path);
		}
		
		public String listAccount(){
			FileDirReader filedir = new FileDirReader();	
			return  filereader.read(filedir.acclist);
		}
		 
		
		public void imageBase64(String filepath,JSObject jsObj){
			File imgFile = new File(filepath);
			String baseImage = null;
			String filename = null;
			try{
			if(imgFile.exists()){
				filename = imgFile.getName();
				String mimeType  = Files.probeContentType(imgFile.toPath()); 
				
				baseImage = "data:"+mimeType+";base64,";
				byte[] mybase = Base64.getEncoder().encode(FileUtils.readFileToByteArray(imgFile));
				baseImage += new String(mybase); 
				
			}
			} catch(Exception e){e.printStackTrace();}
			
			jsObj.setMember("baseimage", baseImage);
			jsObj.setMember("pathimage", filepath); 
			jsObj.setMember("filename", filename); 
		}
		
		public void closeEditor(){
			callback.call("finish");
			wp.closeWeb();
		}

		public postDBContent getCurrentPost(){  
			return currentContent;
		}   
		public void savetoDraft(JSObject param,final JSObject saveCallback){ 			
			String title = (String) param.getMember("title");
			String body = (String)	param.getMember("body");
			String keyword = (String) param.getMember("keyword");
			String category = (String) param.getMember("category");
			String date = (String) param.getMember("date");  

			String error = ""; 
			 
			error +=(!title.equals("undefined"))? "":" title undefined<br/>";
			error +=(!body.equals("undefined"))? "":" body undefined<br/>";
			error +=(!keyword.equals("undefined"))? "":" keyword undefined<br/>";
			error +=(!category.equals("undefined"))? "":" category undefined<br/>";
			error +=(!date.equals("undefined"))? "":" date undefined<br/>";  
			
			if(error.length() < 1){  
							currentContent.title = title;
							currentContent.content = body;
							currentContent.category = category;
							currentContent.keyword = keyword;
							currentContent.date = date;   
							currentContent.post_status = "Locally Saved"; 
						  
			new Thread(new Runnable() {				
				public void run() {   
					currentContent.dbid = db.SaveNewPost(currentContent); 
					System.out.println("dbidnya"+currentContent.dbid);
					Calljsfrombg.call(saveCallback, "finish", null);
				}
			}).start(); 
			} else {
				Calljsfrombg.call(saveCallback, "report", error);
			}
		}
		
		public void publish(final JSObject publishcallback){  
			final BlogRule connector = BlogSwitch.getBlogRule(userconfig);  
			new Thread(new Runnable() {				
				public void run() {  
					connector.publish(fromserv,currentContent, publishcallback); 
					Calljsfrombg.call(publishcallback,"finish",null);
				}
			}).start();  
		}
		
		public void uploadImage(
				String mode, 
				int fromserver_index,
				final JSObject imageOBJ,
				final JSObject callback
				){
			
			
			UserConfig userConfigUploadImg = null;
			FromServerInfo fromserverInfoForUpload = null;
				if(mode.equals("default")){
					userConfigUploadImg = userconfig;
					fromserverInfoForUpload = fromserv;
				} else { 
					userConfigUploadImg = AllConfig.getUserConfig(mode);
					fromserverInfoForUpload = AllConfig.getFromServerInfo(mode, fromserver_index);
				}
		if(userConfigUploadImg !=null && fromserverInfoForUpload !=null){
			final BlogRule blogrule = BlogSwitch.getBlogRule(userConfigUploadImg);
			final FromServerInfo fromServerFinal = fromserverInfoForUpload;
			new Thread(new Runnable() {
				
				public void run() {  
					String baseImage = (String) imageOBJ.getMember("baseimage");
					String filename = (String) imageOBJ.getMember("filename");
					String filetype = (String) imageOBJ.getMember("filetype");
					
					blogrule.uploadImage(fromServerFinal,baseImage, filename, filetype,callback);	  
				}
			}).start();
			
			
		}
		}
	}
	
	
}