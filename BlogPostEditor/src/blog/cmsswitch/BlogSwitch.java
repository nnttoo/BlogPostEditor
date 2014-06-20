package blog.cmsswitch;

import blog.AllConfig.UserConfig;
 
   
public class BlogSwitch{  
	public static BlogRule getBlogRule(UserConfig userconfig){ 
		if(userconfig.cms.equals("Wordpress")){
			return new Wordpress_rule(userconfig);
		} else {
			return new Blogger_rule(userconfig);
		}
	}
}
	
	 
	
	 