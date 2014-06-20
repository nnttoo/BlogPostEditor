var editor_blog_gui = function(callback){  
	this.getData();
	this.prepareButton();
	this.publishButton();  
	callback(); 
};

editor_blog_gui.prototype.htmlEntityRev = function(text){
	var dipar = $('<div/>'); 
	dipar.html(text);  
	return dipar.text();
};
editor_blog_gui.prototype.getData = function(){ 
	 
	this.currentPost =  java.getCurrentPost(); 
	this.editorElem = $('.editor');
	this.titleElem = $('input[name=title]');
	this.permalinkElem = $('input[name=permalink]');
	this.dateElem = $('input[name=datepost]'); 
	this.keywordElem = $('input[name=keyword]'); 
	this.categoryElem = $('input[name=category]'); 
	this.postIdElem = $('input[name=postid]');
	   
		this.titleElem.val(this.htmlEntityRev(""+this.currentPost.title));
		this.editorElem.html(""+this.currentPost.content);
		this.permalinkElem.val(""+this.currentPost.permalink);
		this.keywordElem.val(this.htmlEntityRev(""+this.currentPost.keyword));
		this.categoryElem.val(this.htmlEntityRev(""+this.currentPost.category));
		
		var timetoval = (this.currentPost.date =="")? this.getIsoDate():this.currentPost.date;
		this.dateElem.val(timetoval);
		this.postIdElem.val(""+this.currentPost.postId);  
		
	this.categoryClick();
	this.imageUploaderOption();
};

editor_blog_gui.prototype.getIsoDate = function(){
	function ISODateString(d){
		 function pad(n){return n<10 ? '0'+n : n}
		 return d.getUTCFullYear()+''
		      + pad(d.getUTCMonth()+1)+''
		      + pad(d.getUTCDate())+'T'
		      + pad(d.getUTCHours())+':'
		      + pad(d.getUTCMinutes())+':'
		      + pad(d.getUTCSeconds())+''
		 }

		var d = new Date();
	return ISODateString(d);
};

editor_blog_gui.prototype.imageUploaderOption = function(){
	var imageuploader_option = $('#imageuploader_option');
	var imageuploader = $('#imageuploader');
	var getListAccunt = java.listAccount();
	var clickSet = function(blogdir,fromserverIndex,title){
		return function(){
			imageuploader.html(title);
			imageuploader.attr('data-blogdir',blogdir);
			imageuploader.attr('data-fromserverindex',fromserverIndex);
		}
	}
	
	var defaultChild = $('<li><a class="image_upload_select" href="#">Default</a></li>');
	defaultChild.click(clickSet("default","default","Default"));
	imageuploader_option.append(defaultChild);
	
	if(getListAccunt !=null && getListAccunt.length > 2){
		var jsonacclist = JSON.parse(getListAccunt);
 
		if(jsonacclist !=null){
			for(var i=0;i<jsonacclist.length;i++){
				var blogdir = jsonacclist[i]; 
				var cmsInfo = " - (Blogger)";
				var fromserverInfoString = java.readFile(blogdir + "/fromserverinfo.json");
				var userConfigString = java.readFile(blogdir + "/config.json");
				if(userConfigString !=null){
					var userConfig = JSON.parse(userConfigString);
					if(userConfig.cms =="Wordpress"){
						cmsInfo = " - (WP)"
					}
					
				}				
				
				if(fromserverInfoString !=null){
					var fromserverinfo = JSON.parse(fromserverInfoString);
					if(fromserverinfo !=null){
						for(var fi=0;fi<fromserverinfo.length;fi++){
							var tiapFromserver = fromserverinfo[fi];
							var blogname = tiapFromserver.blogName;
							
							var child = $('<li><a href="#">'+blogname + cmsInfo+'</a></li>');
							child.click(clickSet(blogdir, fi, blogname));
							imageuploader_option.append(child);
						}						
					}					
				}				 				
			}
			
		}		
	} 
	 
};

editor_blog_gui.prototype.callSaveSystem = function(type){
	var myvar = this;
		new publishPost(
				myvar.titleElem,
				myvar.editorElem, 
				myvar.keywordElem,
				myvar.categoryElem,
				myvar.dateElem,
				myvar.postIdElem,
				myvar.permalinkElem,
				type
		); 
	if(type =='save'){
		Toast('Halaman di simpan');
	}
};

editor_blog_gui.prototype.publishButton = function(){
	var publishButton = $('#publishbut');
	var saveBut = $('#savebut');
	var myvar = this;

	publishButton.click(function(){ 
		myvar.callSaveSystem("publish");
	});
	saveBut.click(function(){
		 myvar.callSaveSystem("save");
	});
	
};

editor_blog_gui.prototype.prepareButton = function(){
	var myvar = this;
	$(window).keypress(function(event) {
		if(event.which == 19){
			myvar.callSaveSystem('save');
		}
	});
};

editor_blog_gui.prototype.categoryClick = function(){
	var categoryString = java.getCategory();
	var categoryHtml = ''; 
	if(categoryString !=null){
		var catJson = JSON.parse(categoryString);
		for(var ci=0;ci<catJson.length;ci++){
			categoryHtml +=  '<li><a class="catchild" href="#">'+catJson[ci].categoryName+'</a></li> ';
		}		
	}
	var myvar = this;
	this.catOptElem = $("<ul class='catoption dropdown-menu'>" +
								categoryHtml +
								"</ul>"); 
	if(this.catOptElem.parent().length < 1){
		$('.post_option').append(myvar.catOptElem);			
		this.catOptElem.css({top: myvar.categoryElem.position().top + 70});
		
		this.catOptElem.find('.catchild').click(function(){
			var oldcatString = myvar.categoryElem.val();
			oldcatString += (oldcatString.length > 0)? ',':'';
			oldcatString += $(this).text();			
			myvar.categoryElem.val(oldcatString);
			
		});
	}
	
	this.categoryElem.click(function(){ 
		var clickremove = function(){
			myvar.catOptElem.hide();
			$(window).unbind('click',clickremove);
		};
		var clickMenu = function(e){
			myvar.catOptElem.show();
			$(window).unbind('click',clickMenu);
			$(window).bind('click',clickremove);
		};
		$(window).click(clickMenu);
		
	});
};  