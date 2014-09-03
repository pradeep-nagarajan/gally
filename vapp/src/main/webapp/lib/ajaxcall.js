function getJson(url, mname) {
	$.when(
	        $.ajax({
	            url: url,
	            dataType : 'jsonp',
	    		jsonp : 'callback',
	            success: function(data) {
	            	var code=mname+"DataPush(data)";
	            	eval(code);
	            },
	    		// If there was no resonse from the server
	    		error : function(jqXHR, textStatus, errorThrown) {
	    			console.log("Something really bad happened " + textStatus);
	    		}
	        })
	).then( function(){
		
    });
}
