package blog.wordpress;

import java.util.HashMap;
import java.util.List;
 


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import blog.wordpress.WP_xmlparse.FindBynext;

public class Wp_xmlparse_imgupload{
	
	public static HashMap<String, String> parse(String xml){
		HashMap<String, String> result = new HashMap<String, String>();
		WP_xmlparse xmlparse = new WP_xmlparse();
			try{
				Node node = xmlparse.nodefromString(xml); 
				FindBynext finder = xmlparse.new FindBynext(node);
				finder.startFind("param");
				node = finder.getNode();
				
				NodeList member = ((Element) node).getElementsByTagName("member");
				for(int i=0; i< member.getLength();i++){
					Node curmember = member.item(i);
					List<Node> childMember = xmlparse.findChildElement(curmember);
					
					String name = null;
					String value = null;
					
					for(Node tiapChild : childMember){
						String childname = tiapChild.getNodeName();
						
						if(childname.equals("name")){
							name = tiapChild.getTextContent();
						}  else if(childname.equals("value")){
							value = tiapChild.getTextContent();
						}
						
					}
					
					if(name !=null && value !=null){
						result.put(name, value);
					}
				}
				
			}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
}