var drop_down = function(){  
	
	$('.with-dropdown').each(function(){
		var myvar = this;
		$(this).removeClass('with-dropdown');
		var click = function(){ 
			var dropdown = $(myvar).find('.dropdown-menu');
			$('.dropdown-menu').css({'display':'none'});
			dropdown.css({'display':'block'});
			
			var hideButton = function(){
				$(window).unbind('click',hideButton);
				dropdown.css({'display':'none'});		
			}; 
			$(window).bind('click',hideButton); 
			return false;
		};	
		 
		$(this).find('button').click(click);
	}); 
};

 

$(document).ready(function(){
	drop_down(); 
});
