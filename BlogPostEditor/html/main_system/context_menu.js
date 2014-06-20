var Context_menu = function(jqObject){ 
	this.view = jqObject;
	$('body').append(this.view);	 
};  
Context_menu.prototype.show = function(e){	 
	var myvar = this; 
	var maxpos = this.getMax();
	var contextwidth = this.view.width();
	var contextheight = this.view.height(); 
	var xpos = (e.pageX + contextwidth) > maxpos.left? e.pageX - contextwidth - 10 :e.pageX; 
	var ypos = (e.pageY + contextheight) > maxpos.top? e.pageY - contextheight - 10: e.pageY;
	this.view.css({'display': 'block','left':xpos,'top':ypos});	 
};

Context_menu.prototype.hide = function(){
	this.view.hide();
};

Context_menu.prototype.getMax = function(){
	var pojokBawah = $('.contextruller');
	if(pojokBawah.size() < 1){
		pojokBawah = $('<div class="contextruller" style="position: fixed;bottom:0px;right:0px;height: 1px;widht:1px"></div>');
		$('body').append(pojokBawah);
		}
	var pos = pojokBawah.position(); 
	return pos;
}