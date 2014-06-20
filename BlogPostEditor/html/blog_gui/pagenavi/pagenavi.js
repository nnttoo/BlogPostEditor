  var pagenavi = function(startIn,maxNumb,clickfunc){
	var parent = $('#pagenavi_parent'); 
	var pagenavi_min = $('#pagenavi_min');
	var pagenavi_plus = $('#pagenavi_plus');
	var jumlahButton = 0; 
	
	var appearNumber = function(start){ 
		parent.html('');
		 
		for(var i=start;i<=maxNumb;i++){
			var cssClass = (startIn == i)? 'pagenavi_yellow':'pagenavi_red';
			var numButton = $('<div class="pagenav_number '+cssClass+'">'+i+'</div>'); 
			parent.append(numButton);  
			
			var position = numButton.position();
			if((position.top + 2) > parent.height()){ 
				numButton.remove();
				jumlahButton = parent.find('.pagenav_number').length;
				break;
			}
			
		}; 
		
		parent.find('.pagenav_number').click(function(){
			var curNumber = $(this).html();
			if(typeof clickfunc =='function'){
				clickfunc(Number(curNumber));
			}
			
		});
		
	};	
	var t; 
	var repeatPlus = function (repeatTime) {
        clickNext();
        t = setTimeout(function(){
				repeatPlus(100);
			}, repeatTime);  
    };
	var clickNext = function(){
		var firstNumber = parent.find('.pagenav_number').first().html();
		var nextNumb = Number(firstNumber) + jumlahButton;
			nextNumb = (nextNumb > maxNumb)? maxNumb - 1:nextNumb;
		appearNumber(nextNumb); 
	};
	
	pagenavi_plus.bind('mousedown',function(){
				repeatPlus(1000);
			});
	pagenavi_plus.bind('mouseup mouseout',function(){
		clearTimeout(t);
	});
	
	
	var tm;
	var repeatMin = function (repeatTime) {
        clickMin();
        tm = setTimeout(function(){
				repeatMin(100);
			}, repeatTime);  
    };
	
	var clickMin = function(){
		var firstNumb = parent.find('.pagenav_number').first().html(); 			
		var minNumb = Number(firstNumb) - jumlahButton;
			minNumb = (minNumb < 1)? 1:minNumb; 
			appearNumber(minNumb); 
	};
	
	pagenavi_min.bind('mousedown',function(){		
		repeatMin(1000);
	});
	pagenavi_min.bind('mouseup mouseout',function(){		
		clearTimeout(tm);
	}); 
	var startApear =  startIn - 3;
		startApear = (startApear <= 1)? 1:startApear;
	appearNumber(startApear);
 }