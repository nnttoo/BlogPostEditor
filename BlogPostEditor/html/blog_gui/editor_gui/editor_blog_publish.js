var publishPost = function(titleElemIn,editorElemIn, keywordElemIn ,categoryElemIn,dateElemIn, postidElemIn, permalinkElemIn,type){
	
	this.titleElem = titleElemIn;
	this.editorElem = editorElemIn;
	this.catElem = categoryElemIn;
	this.keywordElem = keywordElemIn;
	this.dateElem = dateElemIn;
	this.postidElem = postidElemIn;
	this.permalinkElem = permalinkElemIn;
	this.type = type;
	this.savaTodraft("save");
	if(type == 'publish'){
		this.uploadImage(); 
	} 
}; 
 
publishPost.prototype.uploadImage = function(){
	var listToupload = [];
	var imageready = 0;
	
	var imageElem = this.editorElem.find('img'); 
	var imguploader_blogdir = $('#imageuploader').attr('data-blogdir');
	var imageuploader_fi = $('#imageuploader').attr('data-fromserverindex');
	var myvar = this;

	var Time = function(){
		return new Date().getTime();
	}; 
	
	$.each(imageElem,function(key, val){
		var imgsrc = $(val).attr("src");
		var altimg = $(val).attr("alt");
		var match = imgsrc.match(/data:(.*?);base64,(.*)/); 
		if(match){
			var filetype = match[1];
			var fileExtentionArr = filetype.split('/');
			var	fileExtention = (typeof fileExtentionArr[1] !='undefined')? fileExtentionArr[1]:'';
			var filename = (typeof altimg !='undefined')?  altimg : Time() ;
				filename =  (filename.match(new RegExp('\.'+fileExtention+'$')))? filename + "." + fileExtention : filename;
			
			listToupload.push({ 
				filetype : match[1],
				baseimage : match[2],
				filename : filename,
				imageElem : $(val)
			});
		}
	});
	loadFunction(function(){
		var imageUploadLoop = function(position){
			if(listToupload.length > position){
				var uploadParamObj = listToupload[position];
				var callback = {
						finish : function(src){
									if(typeof src != 'undefined' && src !=null){
									uploadParamObj.imageElem.attr("src",src);
									}
									imageUploadLoop(position + 1);
						},
						report : function(text){
							$('#loading').append(text + "<br/>");
						}
				};
				  
				java.uploadImage(
						 imguploader_blogdir, 
						 Number(imageuploader_fi),
						 uploadParamObj,
						 callback
						);
				
			} else {
				myvar.savaTodraft("publish");
			}
		}
		imageUploadLoop(0);
	}); 
	  
};

publishPost.prototype.htmlEntity = function(text){
	var div = $('<div/>');
	div.text(text);
	return div.html();
};

publishPost.prototype.savaTodraft = function(typesave){
	var postTitle = this.htmlEntity(this.titleElem.val());
	var postBody = this.editorElem.html();
	var postKeyword = this.htmlEntity(this.keywordElem.val());
	var postCat = this.htmlEntity(this.catElem.val());
	var date = this.dateElem.val();  
	var myvar = this;
	 
	java.savetoDraft({
		title : postTitle,
		body : postBody,
		keyword : postKeyword,
		category : postCat,
		date : date  
		},
		{
			report : function(text){
				$("#loading").append(text + "<br/>");
			},
			finish : function(){ 
				if(typesave == 'publish'){
					java.publish({
						finish: function(){ 
							java.closeEditor();
						},
						report: function(text){
							$('#loading').append(text + "<br/>");
						}
					});
				}
			}
		});
}
