package blog.wordpress;

import java.util.List; 
import org.w3c.dom.Node; 

import blog.wordpress.WP_xmlparse.FindBynext;

public class WP_xmlparse_afterpost{
	public static String parse(String xml){
		String result = null;
		try{
			
			WP_xmlparse xmlparser = new WP_xmlparse();
			
			Node worknode = xmlparser.nodefromString(xml);
			FindBynext finder = xmlparser.new FindBynext(worknode);
			finder.startFind("param");			
			worknode = finder.getNode();
			List<Node> valElem = xmlparser.findElemByNodename(worknode, "value");
			for(Node mynode : valElem){
				result  = mynode.getTextContent();
			}
			
			result = result.replaceAll(" ", "");
			
		}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
}