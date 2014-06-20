package blog.wordpress;
 
import java.util.List; 

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node; 
 

public class WP_xmlparsetojson extends WP_xmlparse{
	public String parsetoJson(String xmlResult){
		String result = null;
		try{ 
			Node workingnode = nodefromString(xmlResult); 
			
			FindBynext finder = new FindBynext(workingnode);
			finder.startFind("data");
			workingnode =  finder.getNode(); 
			List<Node> getChild = findChildElement(workingnode);
			JSONArray jsonArray = new JSONArray();
			for(Node mnode : getChild){
				List<Node> nlist = nodeListElement(((Element)mnode).getElementsByTagName("member"));
				JSONObject tiapData = new JSONObject();
				
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
						tiapData.put(stringname, stringvalue);						
					}  
				}
				jsonArray.put(tiapData);
			}
				
			result = jsonArray.toString();
			
		}catch(Exception e){}
		
		
		return result;
		
	} 
}