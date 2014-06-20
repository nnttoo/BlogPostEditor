package blog.wordpress;
  
import java.util.List;

import netscape.javascript.JSObject;

import org.apache.http.HttpResponse; 
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tools.Calljsfrombg;
import blog.PostDb; 
import blog.postDBContent;

public class WP_multi_xmlpost_todb extends WP_xmlparse{  
	private void setVariable(postDBContent dbcontent,String key,String value){
		if(key.equals("postid")) { dbcontent.postId = value;} else 			
		if(key.equals("title")) { dbcontent.title = value;} else 				
		if(key.equals("description")) { dbcontent.content = value;} else 					
		if(key.equals("mt_keywords")) { dbcontent.keyword = value;} else 						
		if(key.equals("categories")) { dbcontent.category = value;} else 							
		if(key.equals("date_created_gmt")) { dbcontent.date = value;} else 								
		if(key.equals("post_status")) { dbcontent.post_status = value;} else 									
		if(key.equals("permaLink")) { dbcontent.permalink = value;} 
	}
	
	public void postTodb(PostDb dbpost, HttpResponse xmlresponse, JSObject callback){ 
		try{
		Node workingnode = nodefromHttpResonse(xmlresponse);
		
		FindBynext finder = new FindBynext(workingnode);
		String parentNodename = "data";
		finder.startFind(parentNodename);		
		workingnode = finder.getNode(); 

		List<Node> getChild = finder.getChild();   
		for(Node mnode : getChild){ 
			List<Node> nlist = nodeListElement(((Element)mnode).getElementsByTagName("member")); 

			postDBContent dbcontent = new postDBContent();  
			
			for(Node tiapmember : nlist){ 
				String stringname = null;
				String stringvalue = null;
				
				List<Node> namenodes = oneLevelFindbyName(tiapmember,"name");
				List<Node> valuenodes = oneLevelFindbyName(tiapmember,"value");
				for(Node name : namenodes){
					stringname = ((Element) name).getTextContent();  
				}
				
				for(Node value : valuenodes){
					List<Node> childValue = nodeListElement(value.getChildNodes());
					for(Node tiapchildnode : childValue){
						
						String jenisvalue = tiapchildnode.getNodeName();
						if(!jenisvalue.equals("array")){

							stringvalue = ((Element) tiapchildnode).getTextContent(); 
							
						} else {									
							stringvalue = "";
							FindBynext arrFinder = new FindBynext(tiapchildnode);
							arrFinder.startFind("data");
							Node dataArr = arrFinder.getNode();
							List<Node> valuearraynode = oneLevelFindbyName(dataArr, "value");
							for(Node tiaparrvalue : valuearraynode){
								String sparator = (stringvalue.length() > 0)? ",":"";
								stringvalue += sparator +((Element) tiaparrvalue).getTextContent();	
							}
							
						}
					}
				}						
				
				if(stringname !=null && stringvalue !=null){
					
					setVariable(dbcontent,stringname,stringvalue); 
				}  
			}  
			dbpost.SaveNewPost(dbcontent);  
			Calljsfrombg.call(callback,"report", "save to databse post: " + dbcontent.title); 
		}
		
		}catch(Exception e){e.printStackTrace();}
		
		
		
	}
}