var index_blog_gui_class = function(){
	this.accList = [];
	
	this.workframe = $('.workFrame');
	this.accElem = this.workframe.find('.accList');
	this.postElem = this.workframe.find('.postlist');
	
	this.prepareButton();
	this.appearAccList();
};

index_blog_gui_class.prototype.prepareButton = function(){
	var mclass = this;
	$('.addblog_but').click(function(){
		java.addBlog({'run': function(){
			mclass.appearAccList();			
		}},null);
	});
	
}

index_blog_gui_class.prototype.appearAccList = function(){  
	var acctemplate = this.getTemplate('acclist_template.html');   
	this.accElem.html('');
	var accListJson = java.accList(); 
		accListJson = (accListJson !=null && accListJson.length > 0)? JSON.parse(accListJson): null;
	
	var myvar = this;  	
	var setAppearAccClick = function(accIndex, element){
		return function(){
			myvar.accButtonClick(accIndex,element);
		}
		
	};	 
		if(accListJson !=null){
			for(var aI=0; aI < accListJson.length; aI++){
				var blogDir = accListJson[aI]; 
				var accConfig = java.readBlogConfig(blogDir); 
					accConfig = JSON.parse(accConfig);
					
					this.accList[aI] = {};
					this.accList[aI].blogDir = blogDir;
					this.accList[aI].accConfig = accConfig;


					if(accConfig !=null && typeof accConfig.title !='undefined'){ 
						var cmsInfo = (accConfig.cms =="Wordpress")? " - (WP)": " - (Blogger)";
						var tiapAccElem = $(acctemplate); 			 						
							tiapAccElem.find('.accname').html(accConfig.title + cmsInfo);
							tiapAccElem.find('.accname').click(setAppearAccClick(aI,tiapAccElem)); 
							this.accElem.append(tiapAccElem);
							this.appearBlogList(aI,tiapAccElem);
					}  
				
			}	
			

		}
};


/*MENU APPER SYSTEM START*/
index_blog_gui_class.prototype.showMenu = function(menuElem){
	var contextMenu = new Context_menu(menuElem);	
	var clickWindow = function(e){		 
		if(contextMenu.view.is(':visible')){
			contextMenu.view.remove(); 
			$(window).unbind('click',clickWindow);
		} else {
			contextMenu.show(e);
		}
	};
	
	$(window).bind('click',clickWindow); 
	return true;
}

index_blog_gui_class.prototype.accButtonClick = function(accid,accElem){
	
	var myclass = this;
	var menutemplate = this.getTemplate("acclitst_menu.html");
	var menuElem = $(menutemplate);
		menuElem.find('.syncronize_acc').click(function(){
			myclass.openSyncBlog(accid,accElem);
		}); 
		
		menuElem.find('.editconfig').click(function(){
			var blogdir = myclass.accList[accid].blogDir;
			java.addBlog({'run': function(){
				myclass.appearAccList();			
			}},blogdir);
		});
	return this.showMenu(menuElem);
};

index_blog_gui_class.prototype.postMenuClick = function(acclistid,bloglistid,dbPostId,permalink){
	var myclass = this;
	var menutemplate = $(this.getTemplate("post_menu.html"));
		menutemplate.find('.openeditor').click(function(){ 			
			myclass.openEditor(acclistid, bloglistid, dbPostId);
		});
		
		menutemplate.find('.deletepost').click(function(){ 			
			myclass.deletePost(acclistid, bloglistid, dbPostId);
		}); 
		
		menutemplate.find('.openbrowser').click(function(){ 			
			java.openOnline(permalink);
		}); 
	return this.showMenu(menutemplate);
};

/*MENU APPEAR SYSTEM END*/

index_blog_gui_class.prototype.appearBlogList = function(accid,accElem){
	var blogDir = this.accList[accid].blogDir;  
	
	var myclass = this;
	
	var appearPostClickSet = function(blogListId,type){
		return function(){
			myclass.appearPost(accid, blogListId,0,type);
		}
	};
	
	if(typeof blogDir !='undefined'){		
		var blogAccInfo = java.readBlogAccInfo(blogDir);
			blogAccInfo = JSON.parse(blogAccInfo);
			

			this.accList[accid].blogList = blogAccInfo;

			
			var blogListElem = accElem.find('.listblog');
				blogListElem.html('');
				
			var blogListTemplate = this.getTemplate("list_blog.html"); 
			var listBlogHtml = $('<ul></ul>'); 
			
			if(blogAccInfo !=null){
				for(var bi=0; bi <blogAccInfo.length; bi++ ){ 
					var curBlogInfo = blogAccInfo[bi];
					var tiapBlogElem = $(blogListTemplate); 
						tiapBlogElem.find('.blog_title').html(curBlogInfo.blogName);
						tiapBlogElem.find('.post_but').click(appearPostClickSet(bi,'all'));
						tiapBlogElem.find('.draft_but').click(appearPostClickSet(bi,'draft'));
					
					listBlogHtml.append(tiapBlogElem);
				}
				
				blogListElem.append(listBlogHtml);
			}  
	}
		
};

index_blog_gui_class.prototype.downloadClickButton = function(button,acclistid,bloglistid){ 
	var maxPostDown = $(button).attr('data-jumlah');
	var blogInfo = this.accList[acclistid].blogList[bloglistid]; 
	var blogDir = this.accList[acclistid].blogDir; 
	var blogConfig 	= this.accList[acclistid].accConfig; 
	var myvar = this;
	if(typeof blogInfo !='undefined' && typeof blogConfig !='undefined' ){ 	 
		
		/*
		 * 
		
				String blogdir,
				int jsonfromserverid,
				final String max,
				final JSObject callback
		 * */
		loadFunction(function(){
		 
			java.getRecentPost( 
					blogDir,
					bloglistid, 
					""+maxPostDown,
					
					{
						finish : function(){
							$('#loading').hide();
							myvar.appearPost(acclistid, bloglistid,0,'all');
						},
						
						report : function(text){
							$('#loading').append(text + '<br/>');
						}
					}
					
					
			);
		});
	}
};

index_blog_gui_class.prototype.appearPost = function(accListId,blogListId,startPos,type){ 
	this.postElem.html(this.getTemplate("post_list_container.html")); 
	var myvar = this; 

	
	this.postElem.find('.newpost_but').click(function(){
		myvar.openEditor(accListId, blogListId, "");
	});
	
	var postChildTemplate = this.getTemplate("post_list_child.html");
	
	var blogDir = this.accList[accListId].blogDir;
	var currentBlog = this.accList[accListId].blogList[blogListId]; 
	
	var postMenuClickSet = function(dbPostId,permalink){
		return function(){
			myvar.postMenuClick(accListId, blogListId, dbPostId,permalink);
		}
	};

	
	var countPost = "";
	if(typeof currentBlog !='undefined'){
		this.postElem.find('.activeblogtitle').html(currentBlog.blogName);
		
		this.postElem.find('.download_but').click(function(){			
			myvar.downloadClickButton(this, accListId, blogListId);
		}); 
		
		var dbfile = blogDir + "/" + currentBlog.url.replace(/[^a-zA-Z0-9]/g,'') + ".sqlite";
		 
		var postList = java.getPostList(dbfile,startPos,type);  
		countPost = java.getCountPost(dbfile,type);
		
		for(var pi=0; pi < postList.size();pi++){
			var currentPost = postList.get(pi);   
				var dbId 		=  ''+currentPost.dbid;	
				var postId 		=  ''+currentPost.postId;	
				var title 		=  ''+currentPost.title;	
				var date 		=  ''+currentPost.date;	
				var post_status =  ''+currentPost.post_status;	
				var permalink	=  ''+currentPost.permalink;	
				
				var childPostElem = $(postChildTemplate);
					childPostElem.find('.posttitle').html(title);
					childPostElem.find('.dbpostid').html(dbId);
					childPostElem.find('.date').html(date);
					childPostElem.find('.post_status').html(post_status);
					
					childPostElem.click(postMenuClickSet(dbId,permalink));
					
				this.postElem.find('.postlist_content').append(childPostElem); 
		} 
		drop_down();  
		countPost = Number(countPost); 
		countPost = Math.ceil(countPost / 20);
		currentPage = Math.ceil((Number(startPos) + 20) / 20); 
		
		pagenavi(currentPage,countPost,function(number){
			myvar.appearPost(accListId, blogListId, ((number - 1) * 20) - 1,type);
		});
	}
	
};

index_blog_gui_class.prototype.openSyncBlog = function(accid,accElem){ 
	var myclass = this; 
	var blogdir = this.accList[accid].blogDir;  
		 /*String blogdir,
				int jsonfromserverid,
				final String max,
				final JSObject callback*/
		 loadFunction(function(){
			 
			 java.syncronizeBlog(	
					 	blogdir, 
		 				{ 
		 					finish : function(){
		 						$('#loading').hide();
		 						myclass.appearBlogList(accid,accElem);
		 					},
		 					report : function(text){
		 						$('#loading').append(text + '<br/>');
		 					}
		 				}
		 				
		 		);  
		 }); 
};

index_blog_gui_class.prototype.openEditor = function(acclistid,bloglistid,Dbpostid){ 
	var blogDir =  this.accList[acclistid].blogDir; 
	
/*
 * 				String blogdir,
				int jsonfromserverpos,
				String dbpostIdIn,
				JSObject callbackIn*/
	
	var myvar = this;
	java.openEditor( 
			blogDir,
			bloglistid,
			Dbpostid,			
			{
				finish: function(){
					myvar.appearPost(acclistid, bloglistid, 0,"all");
				}
			} 
		);
	 
};

index_blog_gui_class.prototype.deletePost = function(acclistid,bloglistid,Dbpostid){
	var blogDir =  this.accList[acclistid].blogDir;
	var myvar = this;
	java.openDelete_page( 
			blogDir,
			bloglistid,
			Dbpostid,			
			{
				finish: function(){
					myvar.appearPost(acclistid, bloglistid, 0,"all");
				}
			} 
		);
};


index_blog_gui_class.prototype.getTemplate = function(template){
	var filepath = "blog_gui/index_gui/html_template/" + template;
	return $.ajax({
		type: "GET",
        url: filepath,
        async: false
    }).responseText;
};


