var linkoption =  function(editorsys){
	editorsys.saveRangeSelected();
	var linkoption = $('<div style="background: #d6ebfc; width:400px;padding: 10px;" class="linkoption dropdown-menu">'+ 

			'<div style="margin-top:3px;" class="input-group input-group-sm">'+
				'<span  style="min-width: 100px;" class="input-group-addon">Anchor :</span>'+
				'<input type="text"   class="achorinput form-control" placeholder="Link Anchor">'+
			'</div>'+ 
			
			
			'<div style="margin-top:3px;" class="input-group input-group-sm">'+
				'<span style="min-width: 100px;" class="input-group-addon">URL :</span>'+
				'<input type="text" class="hrefinput form-control" placeholder="http://">'+
			'</div>'+ 
			
			'<div class="input-group input-group-sm">'+
				'<span  style="min-width: 100px;" class="input-group-addon">Rel :</span>'+
				'<input   type="text" class="relinput form-control" placeholder="follow, nofollow">'+
			'</div>'+						

				
			
			'<div class="input-group input-group-sm">'+
				'<span  style="min-width: 100px;" class="input-group-addon">Target :</span>'+
				'<input   type="text" class="targetinput form-control" placeholder="_blank, self">'+
			'</div>'+						

			
			'<br/>'+ 
			
			'<div style="text-align: center; border-top: 1px solid #ccc;">'+ 
				'<button class="cancelBut" style="margin:5px;">Cancel</button>'+
				'<button  class="okBut" style="margin:5px;">OK</button>'+ 
			'</div>'+ 
		'</div>'); 				
	var contextLink = new Context_menu(linkoption);
	var hidefunc = function(){ 
				contextLink.view.remove();
				$('window').unbind('click',hidefunc);
			};
	var showFunc = function(e){
			contextLink.show(e);  
			$(window).unbind('click',showFunc);
			$(window).bind('click',hidefunc); 
			contextLink.view.click(function(){ 
			return false;
	});
			
	contextLink.view.find('.cancelBut').click(hidefunc); 

	contextLink.view.find('.okBut').click(function(){
		var href = contextLink.view.find('.hrefinput').val();
		var targetval = contextLink.view.find('.targetinput').val();
		var relval = contextLink.view.find('.relinput').val();
		var text = contextLink.view.find('.achorinput').val();
		
		var relTag = (relval.length > 1)? 'rel="'+relval+'"':'';
		var targettag = (targetval.length > 1)? 'target="'+targetval+'"':'';
		
		if(href.length > 1){ 
			editorsys.focusRange();
			document.execCommand('insertHTML',false, '<a '+relTag+' '+targettag+' href="'+href+'">'+text+'</a>');
			hidefunc();
		}

		});
	};
	$(window).bind('click',showFunc); 
}