var codeMirrorCall = { 
	'imageCache' 	 : [],
	'audioCache' 	 : [],
	'cacheHtml'  	 : '',
	'baseCache'  	 : function(html){
								var myvar = this; 
								var mhhtml = $('<div>'+html+'</div>');
									mhhtml.find('img').each(function(){
										var imgSRC = $(this).attr('src');
									
										if(/^data:.*/.exec(imgSRC) !=null){
											var imgIndex = myvar.imageCache.length;
											myvar.imageCache[imgIndex] = imgSRC;
											$(this).attr('src',"cache:"+imgIndex);
										}
									}); 
								return mhhtml.html();
							},
	'audioBaseCache' : function(html){ 
								var myvar = this;
								var mhtml = $('<div>'+html+'</div>');
								mhtml.find('a').each(function(){
									var href = $(this).attr('href');
									if(href !=null && /^data:.*/.exec(href)){
										var AudioIndex = myvar.audioCache.length;
										myvar.audioCache[AudioIndex] = href;
										$(this).attr('href','audiocache:'+AudioIndex);
									}
								});
								return mhtml.html();
	},
	'audioDelCache'	: function(html){
								var myvar = this;
								var mhtml = $('<div>'+html+'</div>');
								mhtml.find('a').each(function(){
									
									var href = $(this).attr('href');
									var match = /^audiocache:([0-9]*)/.exec(href);
									if(match !=null && match.length > 1){
										var auIndex = match[1];
										$(this).attr('href',myvar.audioCache[auIndex]);
									}
								
								});	
								myvar.audioCache = [];					
								return mhtml.html();
		
	},
	
	'delBaseCache'	: function(html){
								var myvar = this;
								var mhhtml = $('<div>'+html+'</div>');
									mhhtml.find('img').each(function(){
								var imgSRC = $(this).attr('src');
								var match = /^cache:([0-9]*)/.exec(imgSRC);
										if( match !=null && match.length > 1){ 
											var imgIndex = match[1]; 
											$(this).attr('src',myvar.imageCache[imgIndex]);
										}
									}); 
								myvar.imageCache = [];
								return mhhtml.html();
	},
	'call' : function(editorClass,button,callback){ 
				if(typeof this.close !='undefined'){
					this.close();					 
				}  else { 
					this.close = function(){ 					
						this.closeButton.remove();						
						var resultHtml = this.frame[0].contentWindow.myCodeMirror.getValue();
						if(typeof callback == 'function'){
						
							resultHtml = this.delBaseCache(resultHtml);
							resultHtml = this.audioDelCache(resultHtml);
							
							callback(resultHtml);
						}
						this.frame.remove();
						delete this.close;
					};
					 
					this.closeButton = $('<i> Close </i>');
					$(button).append(this.closeButton);
					
					this.cacheHtml = editorClass.editor.html();
					this.cacheHtml = this.baseCache(this.cacheHtml );
					this.cacheHtml = this.audioBaseCache(this.cacheHtml );
					
					
					this.frame = $('<iframe class="codemirorframe autoresizable"  src="editor_system/codemiror-basic/codemiror-basic.html"></iframe>');
					$('body').append(this.frame);
					var editorPosisiton = editorClass.editor.position();
					this.frame.css({'top' : editorPosisiton.top,'left' : editorPosisiton.left});
					editorClass.resize();
				} 
	}	
	
};