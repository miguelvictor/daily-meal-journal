/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito™ - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */
package team.dailymealjournal.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.repackaged.org.json.JSONArray;
import org.slim3.repackaged.org.json.JSONObject;

import team.dailymealjournal.dto.MealJournalDto;
import team.dailymealjournal.model.Journal;
import team.dailymealjournal.model.Meal;
import team.dailymealjournal.model.MealJournal;
import team.dailymealjournal.meta.MealJournalMeta;
import team.dailymealjournal.service.JournalService;
import team.dailymealjournal.service.MealJournalService;
import team.dailymealjournal.service.MealService;
import team.dailymealjournal.validator.JSONValidators;

/**
 * Service used to handle journal & meal journal transactions.
 * @author Kim Agustin
 * @version 0.06
 * Version History
 * [07/28/2015] 0.01 – Kim Agustin – Initial codes.
 * [08/07/2015] 0.02 – Kim Agustin – Refactored controller to handle meal journal transactions (GET)
 * [08/08/2015] 0.03 – Kim Agustin – Added POST, PUT, DELETE methods
 * [08/17/2015] 0.04 – Kim Agustin – Fixed GET by meal journal ID
 * [09/01/2015] 0.05 – Kim Agustin – Restructured controller flow.
 * [09/08/2015] 0.06 – Kim Agustin – Changed received request into JSON.
 */
public class JournalsController extends Controller {
    
    /**
     * The JournalService to use.
     * Holds the method for adding a journal.
     */
    private JournalService journalService = new JournalService();
    
    /**
     * The MealJournalService to use.
     * Holds the method for adding a journal.
     */
    private MealJournalService mealJournalService = new MealJournalService();
    
    /**
     * The MealJournalDto to use.
     * Wrapper for input data of model.
     * Also contains the errors list.
     */
    MealJournalDto dto = new MealJournalDto();

    @Override
    public Navigation run() throws Exception {
        String json;
        
        String requestMethod = request.getMethod();
        if ("post".equalsIgnoreCase(requestMethod)) {
            json = performPost();
        } else if ("put".equalsIgnoreCase(requestMethod)) {
            json = performPut();
        } else if ("delete".equalsIgnoreCase(requestMethod)) {
            json = performDelete();
        } else {
            json = performGet();
        }
        
        if (dto.getErrorList().size() > 0) {
            // if errors are found, replace whole JSON string to errorList
            json = new JSONObject().put("errorList", dto.getErrorList()).toString();
        }
        
        response.setContentType("application/json");
        response.getWriter().write(json);
        
        return null;
    }
    
    /**
     * Method to perform if request is GET.
     * @return String - resulting JSON string.
     */
    private String performGet() {
        String json = "";
        JSONValidators validators = new JSONValidators(this.request);
        
        try {
            String mealJournalId = requestScope("id");
            
            if(null == mealJournalId) {
                List<Journal> journalList = journalService.getJournalList();
                if (null != journalList) {
                    json = journalsToJson(journalList).toString();
                }
            } else {
                validators.add("id", validators.longType());
                if (validators.validate()) {
                    long id = Long.valueOf(mealJournalId);
                    MealJournal mealJournal = mealJournalService.getMealJournal(id);
                    if (null != mealJournal) {
                        JSONObject mealJournalJson = new JSONObject(MealJournalMeta.get().modelToJson(mealJournal));
                        populateJournalJsonWithMeals(mealJournalJson);
                        json = mealJournalJson.toString();
                    }
                } else {
                    validators.addErrorsTo(dto.getErrorList());
                }
            }
        } catch (Exception e) {
            dto.getErrorList().add("An unexpected error occured!");
        }
        
        return json;
    }
    
    /**
     * Method to perform if request is DELETE.
     * @return String - resulting JSON string.
     */
    private String performDelete() {
        String json = "";
        JSONValidators validators = new JSONValidators(this.request);
        
        try {
            validators.add("id", validators.required(), validators.longType());
            if (validators.validate()) {
                dto.setMealJournalId(this.asLong("id"));
                dto = mealJournalService.deleteMealJournal(dto);
            }
        } catch (Exception e) {
            dto.getErrorList().add("An unexpected error occured!");
        }
        
        validators.addErrorsTo(dto.getErrorList());
        return json;
    }
    
    /**
     * Method to perform if request is POST.
     * @return String - resulting JSON string.
     */
    private String performPost() {
        String json = "";
        JSONObject journalJson = null;
        JSONValidators validators;// = new JSONValidators(this.request);
        
        try {
            //validators.add("data", validators.required("Request must be done with post data."));
            //if (validators.validate()) {
                journalJson = new JSONObject(this.request.getReader().readLine());
                
                validators = new JSONValidators(journalJson);
                validators.add("mealId", validators.required());
                validators.add("quantity", validators.required());

                if (validators.validate()) {
                    dto.setMealId(journalJson.getLong("mealId"));
                    dto.setQuantity(journalJson.getInt("quantity"));
                    dto = mealJournalService.addMealJournal(dto);
                }
            //}
                validators.addErrorsTo(dto.getErrorList());
        } catch (Exception e) {
            dto.getErrorList().add("An unexpected error occured!");
        }
        
        return json;
    }
    
    /**
     * Method to perform if request is PUT.
     * @return String - resulting JSON string.
     */
    private String performPut() {
        String json = "";
        JSONObject journalJson;
        JSONValidators validators;
        
        try {
            //validators.add("data", validators.required("Request must be done with post data."));
            //if (validators.validate()) {
                journalJson = new JSONObject(this.request.getReader().readLine());
                
                validators = new JSONValidators(journalJson);
                validators.add("mealId", validators.required());
                validators.add("quantity", validators.required());
                validators.add("mealJournalId", validators.required());

                if (validators.validate()) {
                    dto.setMealId(journalJson.getLong("mealId"));
                    dto.setQuantity(journalJson.getInt("quantity"));
                    dto.setMealJournalId(journalJson.getLong("mealJournalId"));
                    dto = mealJournalService.editMealJournal(dto);
                }
            //}
                validators.addErrorsTo(dto.getErrorList());
        } catch (Exception e) {
            dto.getErrorList().add("An unexpected error occured!");
        }
        
        return json;
    }
    
    // JSON parser for GET, to be refactored
    private static JSONArray journalsToJson(List<Journal> journalList) throws Exception {
        Map<String, Object> m = new HashMap<String, Object>();
        JSONArray jsonList = new JSONArray();
        Calendar calendar = Calendar.getInstance();
        for(Journal journal : journalList) {
            calendar.setTime(journal.getDateCreated());
            m.put("dateCreated", calendar.getTimeInMillis());
            m.put("journals", new JSONArray(MealJournalMeta.get().modelsToJson(journal.getMealJournalListRef().getModelList())));
            jsonList.put(m);
        }
        populateJournalsJson(jsonList);
        return jsonList;
    }
    
    private static boolean populateJournalsJson(JSONArray jsonList) throws Exception {
        boolean succesful = false;
        for (int i = 0, count = jsonList.length(); i < count; i++) {
            double totalCalories = 0;
            JSONArray journals = jsonList.getJSONObject(i).getJSONArray("journals");
            for (int j = 0, jCount = journals.length(); j < jCount; j++) {
                JSONObject obj = journals.getJSONObject(j);
                populateJournalJsonWithMeals(obj);
                totalCalories += (obj.getDouble("calories") * obj.getInt("quantity"));
            }
            jsonList.getJSONObject(i).put("totalCalories", totalCalories);
        }
        return succesful;
    }
    
    private static boolean populateJournalJsonWithMeals(JSONObject journal) throws Exception {
        boolean succesful = false;
        MealService mealService = new MealService();
        Meal meal = mealService.getMeal(journal.getLong("mealId"));
        journal.put("name", meal.getName());
        journal.put("unit", meal.getUnit());
        journal.put("calories", meal.getCalories());
        succesful = true;
        return succesful;
    }
}
