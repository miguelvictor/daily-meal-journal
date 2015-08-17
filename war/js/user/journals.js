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
 * [08/14/2015] 0.01 – John Alton Decena – Initial codes.
 */


/*
 * Script for displaying user journal meal -- John Alton Decena
 * **/
$(document).ready(function () {
	var container = $('#journals');
	$.ajax({
    	type:'GET',
    	url: '/journals',
    	success: function(data){
    		console.log(data);
    		$.each(data,function(i,journal){
    			var total=0;
    			var obj = journal.journals;
    			var header = '<div class="list-header">';
    			header += '<span class="primary-text">' + journal.dateCreated + '</span>';
	            $.each(obj, function(j, data) {	
	            	total+=parseInt(data.calories);
        		});	
	            header += '<span class="secondary-text">' + total + ' calorie(s)</span>';     
	            header += '</div>';
	            container.append(header); 

    			$.each(obj, function(j, data) {

    				var item = '<a class="list-item" href="/user/edit?id='+data.mealJournalId+'">';//dr'+journal.dateCreated+'
                    item += '<img src="../svg/hard-boiled-eggs.jpg" class="list-item-avatar">';
                    item += '<div class="list-item-text">';
                    item += '<h3>' + data.name + '</h3>';
                    item += '<p>Calories: ' + data.calories + '</p>';
                    item += '</div></a>';
                    container.append(item);
        		});		
    		});
    	}
    });	
	 
});