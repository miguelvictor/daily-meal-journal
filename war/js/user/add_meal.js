	/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito� - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */

/**
 * Scripts of meals data from datastore.
 * @author John Decena
 * @version 0.01
 * Version History
 * [08/16/2015] 0.01 �  AJAX implementation to create/add journal meal given a meal journal id.
 */

/*
 * Display meals
 * Populate meals from datastore on page.
 */
$(document).ready(function () {
	var container = $('#addJournalMeal');
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
    			header += '<input type = "hidden" id ="mealId" value="'+data.mealId+'">';
    			header += '</div>';
	            container.append(header); 
    	}
    });	
});

/*
 * Script for deleting a meal (given a mealJournalId) -- John Alton Decena
 **/
$(document).on('click','#check',function() {
	meal = {
			data: JSON.stringify({
				//mealJournalId: getURLParameter('id'),
				mealId: $('#mealId').val(),
				quantity: $('#mealQuantity').val(),
			})
	}
	$.ajax({
		type:'POST',
		url: '/journals?id='+getURLParameter('id'),
		data:meal,
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