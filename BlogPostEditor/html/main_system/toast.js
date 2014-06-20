var Toast = function(msg){
	 var contextToast = new Context_menu($('<div class="dropdown-menu"></div>'));  
	 contextToast.view.append('<div style="font-size: 14px;padding: 0px 10px 0px 10px; background:#000;color:#fff;text-align:center">' + msg+'</div>');
	 var e = {
		pageX : ($(window).width() / 2 ) - (contextToast.view.width() / 2),
		pageY : 10
		};
	contextToast.show(e);
	setTimeout(function(){
		contextToast.view.remove();
	}, 3000);
}; 