var javaReady = function(callback){
	if(typeof callback =='function'){
		if(typeof java !='undefined'){
			callback();
		} else {
			var javaTimeout = 0;
			var readycall = setInterval(function(){
			javaTimeout++;  
				if(typeof java !='undefined' || javaTimeout > 100){ 
					clearInterval(readycall);
					if(typeof java !='undefined'){ 
							callback(); 
					}
				}
			},100);
		}
	}
};

var errorlistener = function(msg, url, line){ 
	javaReady(function(){
	java.log(msg +", url: "+url+ ", line:" + line); 
	}); 
};

window.onerror = errorlistener;
 

javaReady(function(){
	console.log = function(msg){
		java.log(""+msg);
	};
});


var loadFunction = function(call){
	var loadElem = $('#loading');
	if(loadElem.length < 1){
		loadElem = $('<div id="loading"></div>');
		$('body').append(loadElem);
	}
	loadElem.html("loading..");
	loadElem.show();
	setTimeout(function(){ 
		call();
	},200);
};