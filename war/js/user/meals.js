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
 * [08/16/2015] 0.01 �  AJAX implementation to populate meals at meals.html.
 */

/*
 * Display meals
 * Populate meals from datastore on page.
 */
$(document).ready(function () {
    var container = $('#meals');
    
    $.ajax({
        type:'GET',
        url: '/meals',
        dataType: 'json',
        success: function(data){
            $.each(data,function(i,meal){   
                var header = '<a class="list-item" href="/user/add?id='+meal.mealId+'">';
                header += '<input type="hidden" id ="hide" value="true">';
                header += '<img src="../svg/hard-boiled-eggs.jpg" class="list-item-avatar">';
                header += '<div class="list-item-text">';
                header += '<h3>'+meal.name+'</h3>';
                header += ' <p>ASaas aksajks lSkajsaljslka</p>';
                header +='</div>';
                header +=' </a>';
                container.append(header);                   
            });
        }
    }); 
});