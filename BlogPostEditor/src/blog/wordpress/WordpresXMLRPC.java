package blog.wordpress;
 
import java.io.InputStream;
import java.io.InputStreamReader; 
  
 



import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse; 
import org.apache.http.client.HttpClient; 
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;  
 









import blog.AllConfig.FromServerInfo;
import blog.AllConfig.UserConfig;  
import blog.postDBContent;
import tools.FileReader;
import tools.my_httpreq;

public class WordpresXMLRPC {
	
	UserConfig userconfig;  
	FileReader fr;
	my_httpreq httpreq; 
	
	public WordpresXMLRPC(UserConfig configIn){ 
		userconfig =  configIn; 
		
		fr = new FileReader();
		httpreq = new my_httpreq(); 
	}
	
	public HttpResponse connect(String xml,String url){
		HttpResponse result = null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(url);
		
		try {
			StringEntity se = new StringEntity(xml);
			se.setContentType("text/xml");
			httpPost.setEntity(se);
			result = client.execute(httpPost);  
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return result; 
	}
	
	private String getXmlSample(String xmlfile){		
		String result = null;
		
		InputStream XMLbufer = ClassLoader.getSystemResourceAsStream("xml_sample/"+xmlfile);
		StringBuilder sb = new StringBuilder();
		try{ 
			InputStreamReader isr  = new InputStreamReader(XMLbufer);	
			char[] data = new char[2048];
			int l;
			while ((l = isr.read(data)) != -1) {
	        	sb.append(data, 0, l);
	        } 			
			result = sb.toString();
			isr.close();
			XMLbufer.close();
		} catch(Exception e){}
		
		return result;
	}
	
	public String getBlogInfo(){
		String result = null; 
		
		String xmlString = getXmlSample("wp_default_sample.xml");
		xmlString = xmlString.replace("--BTOOLS_XML_REQ_TYPE--", "getUsersBlogs");
		xmlString = xmlString.replace("--USERBTOOLS--", userconfig.username);
		xmlString = xmlString.replace("--PASSBTOOLS--", userconfig.password);
		xmlString = xmlString.replace("--MAXRESULT--", "1");
		HttpResponse httpresonse = connect(xmlString,userconfig.url + "/xmlrpc.php");  
		
		try{
		result = httpreq.resPonsToString(httpresonse);   
		}catch(Exception e){e.printStackTrace();}
		return result;
		
	}	 
	
	public HttpResponse getRecentPost(String max,FromServerInfo fromservinfo){
		HttpResponse result = null; 
		
		String xmlString = getXmlSample("wp_default_sample.xml");
		xmlString = xmlString.replace("--BTOOLS_XML_REQ_TYPE--", "getRecentPosts");
		xmlString = xmlString.replace("--USERBTOOLS--", userconfig.username);
		xmlString = xmlString.replace("--PASSBTOOLS--", userconfig.password);
		xmlString = xmlString.replace("--MAXRESULT--", max); 
		result = connect(xmlString,fromservinfo.xmlrpc);   
		return result;
	}
	
	public HttpResponse getPostByID(FromServerInfo fromservinfo,String postId){
		HttpResponse result = null; 
		
		String xmlString = getXmlSample("wp_default_sample.xml");
		xmlString = xmlString.replace("--BTOOLS_XML_REQ_TYPE--", "getPost");
		xmlString = xmlString.replace("--USERBTOOLS--", userconfig.username);
		xmlString = xmlString.replace("--PASSBTOOLS--", userconfig.password);
		xmlString = xmlString.replace("--BTOOLS_ID_REUIRED--", postId); 
		result = connect(xmlString,fromservinfo.xmlrpc);   
		return result;
	}
	
	private String keywordListToXml(String keyword){
		String result = "";
			String[] list = keyword.split(",");
			for(String tiapList : list){
				result += "<value><string><![CDATA["+tiapList+"]]></string></value>";
			}
		
		return result;
	}
	
	public HttpResponse getCategory(FromServerInfo fromservinfo){
		HttpResponse result = null; 
		String xmlString = getXmlSample("wp_getcategory.xml"); 
		xmlString = xmlString.replace("[[rxml]user/]", userconfig.username);
		xmlString = xmlString.replace("[[rxml]pass/]", userconfig.password);
		xmlString = xmlString.replace("[[rxml]blogid/]", fromservinfo.blogid);  
		
		result = connect(xmlString,fromservinfo.xmlrpc);
		return result;
	}
	
	public String publish(FromServerInfo fromservinfo,postDBContent paramPost){ //paramPost : [title,body,keyword,category,date]
		String result = null; 
		String dataOptional = ""; 
		 
		String postType = (paramPost.postId.isEmpty())? "newPost":"editPost"; 
		
		String xmlString = getXmlSample("wp_post.xml");
		xmlString = xmlString.replace("[[rxml]type/]", postType);
		xmlString = xmlString.replace("[[rxml]user/]", userconfig.username);
		xmlString = xmlString.replace("[[rxml]pass/]", userconfig.password);
		
		xmlString = xmlString.replace("[[rxml]id/]", paramPost.postId);
		xmlString = xmlString.replace("[[rxml]title/]", paramPost.title);
		xmlString = xmlString.replace("[[rxml]body/]", paramPost.content);
		xmlString = xmlString.replace("[dataoptional/]", dataOptional);
		xmlString = xmlString.replace("[[rxml]time/]", paramPost.date);
		xmlString = xmlString.replace("[[rxml]allow_comment/]", "0");
		xmlString = xmlString.replace("[[rxml]allow_ping/]", "0");
		xmlString = xmlString.replace("[[rxml]post_type/]", "post");
		xmlString = xmlString.replace("[[rxml]keywords/]", keywordListToXml(paramPost.keyword));
		xmlString = xmlString.replace("[[rxml]cats/]", keywordListToXml(paramPost.category));
		try {
			xmlString = new String(xmlString.getBytes("UTF-8"), "ISO-8859-1"); 
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		 
		HttpResponse httpresonse = connect(xmlString,fromservinfo.xmlrpc);   
		try{
		result = httpreq.resPonsToString(httpresonse);   
		 
		}catch(Exception e){e.printStackTrace();}
		
		
		return result;
		
	}
	
	public void deletePost(FromServerInfo fromserverinfo,String postid){
		String xmlString = getXmlSample("deletepost.xml");
		xmlString = xmlString.replace("[[rxml]blogid/]", fromserverinfo.blogid);
		xmlString = xmlString.replace("[[rxml]postid/]", postid);
		xmlString = xmlString.replace("[[rxml]user/]", userconfig.username);
		xmlString = xmlString.replace("[[rxml]pass/]", userconfig.password);
		connect(xmlString,fromserverinfo.xmlrpc);   
		 
	}
	
	public String uploadImage(FromServerInfo fromservinfo,String imgbase64,String filename,String filetype){
		String result = null;
		
		String xmlreq = getXmlSample("wp_uploadImage.xml");
		xmlreq = xmlreq.replace("[[rxml]user/]",userconfig.username);
		xmlreq = xmlreq.replace("[[rxml]pass/]",userconfig.password);		
		xmlreq = xmlreq.replace("[rxml]filename/]",filename);		
		xmlreq = xmlreq.replace("[[rxml]filetype/]",filetype);		
		xmlreq = xmlreq.replace("[[rxml]base64file/]",imgbase64);
		HttpResponse response = connect(xmlreq,fromservinfo.xmlrpc);
		
		try{
			result = httpreq.resPonsToString(response);
			
		}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
	
	 
}
