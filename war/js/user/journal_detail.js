$(document).ready(function () {
	var container = $('#journal');
	$.ajax({
    	type:'GET',
    	url: '/meals?id='+getURLParameter('id'),
    	dataType: 'json',
    	success: function(data){
    			var header =  '<div class="input-group">';
    			header += '<label>Meal Name</label>';
    			header += '<input type="text" name="meal_name" id="mealName" value="'+data.name+'" disabled>';
    			header += '</div>';
    			header += '<div class="input-group">';
    			header += '<label>Calories per default quantity / unit</label>';
    			header += '<input type="text" id="mealCalories" value="'+data.calories+' caloriers per 1 serve" name="meal_name" disabled>';
    			header += '</div>';
    			header += '<div class="input-group">';
    			header += '<label>Default Quantity</label>';
    			header += '<input type="text" name="meal_name" id="mealQuantity" value="'+data.defaultQuantity+'">';
    			header += '</div>';
	            container.append(header);   
    	}
    });	
});
$(document).on('click','#check',function() {
	/*journal = {
			data: JSON.stringify({
				mealId: getURLParameter('id'),
				mealJournalId: getURLParameterDateCreate("tobechanged")
				calories: $('#mealCalories').val(),
				quantity: $('#mealQuantity').val(),

			})
	}*/
	/*$.ajax({
		type:'PUT',
		url: '/meals',
		data:meal,
		dataType:'json',
		success: function(data,status,jqXHR) {
			if(data.errorList.length==0) {
				alert('Success!');
			} else {
				var msg = "";
				for (var i = 0; i < data.errorList.length; i++)
					msg += data.errorList[i] + "\n";
				alert(msg);
			}
		},
		error: function(jqXHR,status,error) {
		}
	});*/
});

function getURLParameter(sParam)
{
	var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
	//var sPageURL = window.location.search.replace("id=","");
	//alert(sPageURL);
//	return sPageURL;
}

function getURLParameterDateCreate(sParam)
{
	/*var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }*/
	var sPageURL = window.location.search.replace("id=","");
	return sPageURL;
}