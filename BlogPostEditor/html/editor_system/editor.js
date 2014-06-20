var EditorSystem = function(){
	this.editor = $('.editor');
	this.panelEditor = $('.editor-control');
	this.resize();
	this.contextMenuImg = new imgContextMenu(this.editor);
	this.costumMetodhRegister();
	this.butClick();  
	this.firstInsertDiv(); 
	
	this.recallEditorClick();
	
	var myvar = this;
	window.onresize = function(){ 
		myvar.resize();
	}; 
  
};

EditorSystem.prototype.firstInsertDiv = function(){	
	this.editor.focus();
	this.simpleCommand('formatBlock <p>');	
	document.execCommand('defaultParagraphSeparator', false, 'p');
}


EditorSystem.prototype.getMaxPos = function(){
	var ruller = $('#ruller');
	if(ruller.size() < 1){
		ruller = $('<div id="ruller" style="position:fixed;height:1px;width:1px;bottom:10px;right:10px"></div>');
		$('body').append(ruller);
	}
	var position = ruller.position(); 
	return position;
};

EditorSystem.prototype.resize = function(){
	var position = this.getMaxPos();	
	var Eposition = this.editor.position();
	var leftPos = position.left - Eposition.left;
	var topPos = position.top - Eposition.top
	$('.autoresizable').css({'width': leftPos,'height':topPos});
};

EditorSystem.prototype.butClick = function(){
	var myvar = this;
	var pn = this.panelEditor;
	
	pn.find('[data-editorcommand]').click(function(){
		if($(this).attr('data-paste-range')){
			myvar.focusRange();
		}
		var dataControl = $(this).attr('data-editorcommand'); 
		if(typeof dataControl !='undefined'){ 
				myvar.simpleCommand(dataControl);
		}
	});
	
	pn.find('[data-costumcommand]').click(function(){
			var funcSelector = $(this).attr('data-costumcommand');
			try{
				myvar.costumMetod[funcSelector](this);
			}catch(e){
			console.log(e);
			}
		});
	
	pn.find('[data-copy-range=range]').click(function(){ 
			myvar.saveRangeSelected();
		}); 
};
EditorSystem.prototype.simpleCommand = function(command){
	var splitedDC = command.split(' ');
			
			if(splitedDC.length < 2){
				try{
				 document.execCommand(splitedDC[0]);
				 } catch(e){
					console.log(e);
				 }
			} else {			
				try{
				document.execCommand(splitedDC[0],false, splitedDC[1]); 
				} catch(e){
					console.log(e);
				}
				// java.log(splitedDC[0] + " - " + splitedDC[1]);
			}
};
EditorSystem.prototype.costumMetodhRegister = function(){
		var myvar = this;
		this.costumMetod = {			
			'viewhtml' : function(button){ 	 				
					codeMirrorCall.call(myvar,button,function(html){			 				
					myvar.editor.html(html);
					myvar.recallEditorClick();
					});
			},
			
			'insertImage' : function(){
				var imgFile = {};
				java.openImageFileDialog(imgFile); 
				
				if(typeof imgFile.baseimage !='undefined' && typeof imgFile.filename !='undefined'){
				document.execCommand('insertHTML',false, '<img alt="'+imgFile.filename+'" src="'+imgFile.baseimage+'" />');
				}
				
			  myvar.recallEditorClick();
			},
			'link' : function(){
				linkoption(myvar);
			}
		};
};
 

EditorSystem.prototype.saveRangeSelected = function(){
		this.range;
		var myvar = this; 

		try{
			var sel = window.getSelection();
			 myvar.range = sel.getRangeAt(0); 
			}catch(s){
			console.log(s);
			} 
	};
EditorSystem.prototype.focusRange = function(){
	var range = this.range;  
	if(typeof range !='undefined'){
	var sel = window.getSelection();
		sel.removeAllRanges();
		 sel.addRange(range);
		 }
};  

EditorSystem.prototype.recallEditorClick = function(){
	this.setResizeImage();
	this.contextMenuImg.clickSet();
};

EditorSystem.prototype.setResizeImage = function(){
	var myvar = this;
	var toResize = function(){
		new resizeClass(this,function(){
			myvar.recallEditorClick();
		}); 
		
		myvar.editor.find('img').unbind('click',toResize);
		return false;
	};
	
	var setClickImg = function(){	 
		myvar.editor.find('img').click(toResize);
	};
	
	setClickImg();
}




	