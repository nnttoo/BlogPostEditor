package blog; 

import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONObject;

import tools.FileReader; 

	public class AllConfig{ 
		
		public class UserConfig{
			public String cms;
			public String title;
			public String url;
			public String username; 
			public String password;
			public String blogdir;
		}
		
		public class FromServerInfo{
			public String blogName;
			public String xmlrpc;
			public String blogid;
			public String url;  
			
			public String getDBFilename(){ 
				return  "/" + url.replaceAll("[^a-zA-Z0-9]", "")+ ".sqlite"; 
			}
			
			public String getCatFilename(){
				return "/"+url.replaceAll("[^a-zA-Z0-9]", "") + "_cat.json"; 
			}
			
		}
		//"blogName":"Gadis Boss","xmlrpc":"","isAdmin":"1","blogid":"1226056143529077365","url":"http://gadisbos.blogspot.com/"
		//{"cms":"Blogspot","title":"Dita","url":"http://","username":"ditaditaliana@yahoo.com","password":"v3agustin"}		
		
		public <NamaClass> void jsonToclass(NamaClass iniclass, JSONObject json){
			try{ 
				Field[] fields = iniclass.getClass().getFields();
				for(Field field : fields){
					
					String key =  field.getName();
					String value = json.getString(key);
					field.set(iniclass, value);						
				}
				
				}catch(Exception e){e.printStackTrace();}
		}
		
		public static UserConfig getUserConfig(String blogdir){
			AllConfig uf = new AllConfig();
			UserConfig result = uf.new UserConfig();
				FileReader fr = new FileReader();
				String jsonconfig = fr.read(blogdir + "/config.json");
				
				if(jsonconfig !=null && jsonconfig.length() > 5){		
					JSONObject jsonobj = new JSONObject(jsonconfig);					
					uf.jsonToclass(result, jsonobj);
				}		
				result.blogdir = blogdir;
			return result;
		}
		
		public static FromServerInfo getFromServerInfo(String blogdir, int jsonId){
			AllConfig uf = new AllConfig();
			FromServerInfo result = uf.new FromServerInfo();
			FileReader fr = new FileReader();
			String jsonstring = fr.read(blogdir + "/fromserverinfo.json");
			
			if(jsonstring !=null && jsonstring.length() > 5){
				JSONArray jsArray = new JSONArray(jsonstring); 
				
				if(jsArray.length() > jsonId){
					JSONObject selectedObj = (JSONObject) jsArray.get(jsonId);
					uf.jsonToclass(result,selectedObj);			
				}
				
			}		
			
			return result;
		} 
		
		
		
		
}
