var imgContextMenu = function(editorElem){
	this.editorElem = editorElem; 
	this.imgTarget;
	this.element = $('<div style="width:200px;padding: 10px;" class="dropdown-menu">'+ 
						'<div class="input-group input-group-sm">'+
						  '<span class="input-group-addon">Width :</span>'+
						  '<input   type="text" class="widthinput form-control" placeholder="Width">'+
						'</div>'+						
						'<div style="margin-top:3px;" class="input-group input-group-sm">'+
						  '<span class="input-group-addon">Height :</span>'+
						  '<input type="text" class="heightinput form-control" placeholder="Height">'+
						'</div>'+
						'<br/>'+ 
						'<div style="text-align: center; border-top: 1px solid #ccc;">'+ 
							'<button class="cancelBut" style="margin:5px;">Cancel</button>'+
							'<button  class="okBut" style="margin:5px;">OK</button>'+ 
						'</div>'+ 
						'</div>'); 
	this.widhtInput = this.element.find('.widthinput');
	this.heightInput = this.element.find('.heightinput');
	
	
	var myvar = this;
	this.element.find('.okBut').click(function(){
	
		if(Number(myvar.heightInput.val()) > 0){
			myvar.imgTarget.css({'height':Number(myvar.heightInput.val())});
		} 
		
		if(Number(myvar.widhtInput.val()) > 0){			
			myvar.imgTarget.css({'width':Number(myvar.widhtInput.val())});
		}
		myvar.contextMenu.view.hide();
	});
	
	this.element.find('.cancelBut').click(function(){
		myvar.contextMenu.view.hide();
	});
	
	this.contextMenu = new Context_menu(this.element);
};

imgContextMenu.prototype.clickSet = function(){
	var imgElem = this.editorElem.find('img');
	imgElem.unbind('contextmenu');
	var myvar = this;
	imgElem.bind('contextmenu',function(e){
		myvar.imgTarget = $(this);
		myvar.widhtInput.attr('placeholder',myvar.imgTarget.width());
		myvar.heightInput.attr('placeholder',myvar.imgTarget.height());
		
		myvar.contextMenu.show(e);
	});
}
 
