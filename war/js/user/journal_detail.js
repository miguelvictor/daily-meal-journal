/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito™ - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */

/**
 * Handles addition of meals to datastore.
 * @author John Alton Decena
 * @version 0.01
 * Version History
 * [08/16/2015] 0.01 – John Alton Decena – Initial codes.
 * [08/17/2015] 0.02 - John Alton Decena - AJAX implementation to to delete meal given a meal journal id.
 */


/*
 * Script for displaying meal detail -- John Alton Decena
 **/
$(document).ready(function () {
	var container = $('#journal');
	$.ajax({
    	type:'GET',
    	url: '/journals?id='+getURLParameter('id'),
    	dataType: 'json',
    	success: function(data){
    		console.log(data);
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

/*
 * Script for deleting a meal (given a mealJournalId) -- John Alton Decena
 **/
$(document).on('click','#delete',function() {
	$.ajax({
		type:'DELETE',
		url: '/journals?mealJournalId='+getURLParameter('id'),
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
	});
});


//helper function in filtering url string to get id
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

}

