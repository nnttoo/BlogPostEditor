package blog.wordpress; 
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
 





import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList; 
import org.xml.sax.InputSource;
 

public class WP_xmlparse{ 
	
	public Node nodefromString(String xml){
		Node result = null;
		try{
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		result = dBuilder.parse(is); 
		}catch (Exception e){e.printStackTrace();}
		return result;
	}
	
	public Node nodefromHttpResonse(HttpResponse response){
		Node result = null;
		try{
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			result = dBuilder.parse(is);			
		}catch(Exception e){e.printStackTrace();}
		
		return result;
		
	}
	
	public List<Node> nodelistToElemNodeList(NodeList nodelist){
		List<Node> result = new ArrayList<Node>();
		for(int i=0; i<nodelist.getLength();i++){
			Node selectNode = nodelist.item(i);
			if(selectNode.getNodeType() == Node.ELEMENT_NODE){
				result.add(selectNode);
			}
		}
		
		return result;
	}
	
	public List<Node> findElemByNodename(Node node,String nodename){
		
		List<Node> result = new ArrayList<Node>();
		
		try{
			NodeList nodelit = ((Element) node).getElementsByTagName(nodename);
			result = nodeListElement(nodelit);
			
		}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
	
	public class FindBynext{
		Node nodetofind;
		List<Node> listchild;
		public FindBynext(Node nodein){
			nodetofind = nodein;
			listchild = new ArrayList<Node>();
		} 
		
		public void findBynext(String nodefind){   
			
			listchild = findChildElement(nodetofind);
			if(listchild.size() > 0){
				Node checkNode = listchild.get(0);
				String nodename = checkNode.getNodeName();
				if(checkNode.hasChildNodes()){ 
						nodetofind = checkNode;
						if(!nodename.equals(nodefind)){
						findBynext(nodefind); 
						} else { 
							listchild = findChildElement(nodetofind);
						}
				}  
			} 
			}
	
		public void startFind(String nodename){
			findBynext(nodename);
		}
		public Node getNode(){ 
			return nodetofind;
		} 
		public List<Node> getChild(){
			return listchild;
		}

	} 
	
	public List<Node> findChildElement(Node node){
		List<Node> result = new ArrayList<Node>();
		NodeList nl = node.getChildNodes();
		for(int f=0;f<nl.getLength();f++){
			Node tempnode = nl.item(f);
			if(tempnode.getNodeType() == Node.ELEMENT_NODE){
				result.add(tempnode);					
			}
		} 
		return result;
	}
	
	public List<Node> oneLevelFindbyName(Node node,String nametofind){
		List<Node> result = new ArrayList<Node>();
		List<Node> child = nodeListElement(node.getChildNodes());
		for(Node tiapChild : child){
			String nodename = tiapChild.getNodeName();
			if(nodename.equals(nametofind)){
				result.add(tiapChild);				
			}			
		}
		
		
		
		return result;
	}

	public List<Node> nodeListElement(NodeList nodelist){
		List<Node> result = new ArrayList<Node>();
		for(int i=0;i<nodelist.getLength();i++){
			Node tempNode = nodelist.item(i);
			if(tempNode.getNodeType() == Node.ELEMENT_NODE){
				
				result.add(tempNode);
				
			}				
		}
		return result;
	} 
	
}