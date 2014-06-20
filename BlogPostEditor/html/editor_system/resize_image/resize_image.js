var resizeClass = function(elemToResize,callbackIn){  
	this.toResize = $('.resize_fake_content');
	this.toResize.css({'width' : $(elemToResize).width(),'height' : $(elemToResize).height()})
	this.callback = callbackIn; 
	this.elemToResize = elemToResize;
	
	this.prepare();
	this.prepareClick();
	this.clicktoRemoveSet();
	

};

resizeClass.prototype.prepare = function(){
	this.mainFrame = $('.resizframe');		
	this.contentFrame = this.mainFrame.find('.resizeContent'); 
	var targetPos = $(this.elemToResize).position();
	
	this.mainFrame.css({'top' : targetPos.top,'left' : targetPos.left,'display' : 'inline-block'});
	//$('body').append(this.mainFrame);
}

resizeClass.prototype.prepareClick = function(){
	var tl = this.mainFrame.find('.rh_tl');
	var tc = this.mainFrame.find('.rh_tc');
	var tr = this.mainFrame.find('.rh_tr'); 
	var cr = this.mainFrame.find('.rh_cr'); 
	var br = this.mainFrame.find('.rh_br');
	var bc = this.mainFrame.find('.rh_bc');
	var bl = this.mainFrame.find('.rh_bl');
	var cl = this.mainFrame.find('.rh_cl'); 
	
	this.handlerSet(tl,1);
	this.handlerSet(tc,2);
	this.handlerSet(tr,3);
	this.handlerSet(cr,4);
	this.handlerSet(br,5);
	this.handlerSet(bc,6);
	this.handlerSet(bl,7);
	this.handlerSet(cl,8); 
	 
};
resizeClass.prototype.handlerSet = function(handlerElem,mode){

		handlerElem.unbind('mousedown');
		var myvar = this;
		
		handlerElem.bind('mousedown',function(c){ 
			console.log('testmouse down');
			var workSpaceResize = $('<div></div>');
			workSpaceResize.css({'width' :myvar.toResize.width(),'height' :  myvar.toResize.height()});
			myvar.contentFrame.append(workSpaceResize);
			
			if(mode==1 || mode==2){			
				workSpaceResize.addClass('workspace_resize_br');				
				
			}  else if(mode==3 || mode==4){			
				workSpaceResize.addClass('workspace_resize_bl');				
			}  else if(mode==5 || mode==6){			
				workSpaceResize.addClass('workspace_resize_tl');				
			}   else if(mode==7 || mode==8){			
				workSpaceResize.addClass('workspace_resize_tr');				
			} 
			 	
			var brPos = myvar.mainFrame.position();
			var oldW = brPos.left ;
			var oldH = brPos.top ; 
			var oldCursorWidth = myvar.contentFrame.width() ;
			var oldCursorHeight = myvar.contentFrame.height();
			var rasio = oldCursorHeight/oldCursorWidth;
			
			var maskResize = $('<div style="background-color:transparent; position: fixed; top:0px;left:0px;height:100%;width:100%"></div>');
			maskResize.css({'cursor' : handlerElem.css('cursor')});
			$('body').append(maskResize);


		$(maskResize).bind('mousemove',function(e){ 
			
			var nW;
			var nH;
		 	if(mode ==1){
				nW = oldCursorWidth - (e.pageX - oldW);
				nH = rasio * nW;				
			 } else if(mode == 2){
			 	nW =  oldCursorWidth;
			 	nH =  oldCursorHeight - (e.pageY - oldH);
			 } else if( mode==3 ){ 
				nW =  e.pageX - oldW;
				nH =  rasio * nW;		
			 } else if( mode==4 ){ 	
				nW =  e.pageX - oldW; 
				nH = oldCursorHeight;
			 } else if( mode==5 ){ 	
				nW =  e.pageX - oldW; 
				nH = 	rasio * nW;
			 } else if( mode==6 ){ 	
				nW =  oldCursorWidth; 
				nH =  e.pageY - oldH;
			 } else if( mode==7 ){ 	
				nH =  e.pageY - oldH;
				nW =  nH / rasio; 
			 } else if( mode==8 ){ 	
				nW =  oldCursorWidth - (e.pageX - oldW);;
				nH = oldCursorHeight;
			 }
			
			//console.log(nW);
			
			workSpaceResize.css({'width' : nW,'height' : nH}); 
	 
		});
		
		$(maskResize).bind('mouseup',function(){
			maskResize.remove(); 
			myvar.toResize.css({'width':workSpaceResize.width(),'height' : workSpaceResize.height()}); 
			
			workSpaceResize.remove(); 
			console.log('rilis');
		});
		return false;
	}); 
}



resizeClass.prototype.rilisClick = function(){
	var myvar = this;
	$(this.elemToResize).css({'width' : myvar.mainFrame.width(),'height' : myvar.mainFrame.height() });
	 this.mainFrame.hide();
};

resizeClass.prototype.clicktoRemoveSet = function(){
	var myvar = this;
	var clickSet = function(){
		myvar.rilisClick();
		$(window).unbind('click',clickSet);
		if(typeof myvar.callback =='function'){
			myvar.callback();
		}
	};
	$(window).click(clickSet);
};