/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito™ - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */
package team.dailymealjournal.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.repackaged.org.json.JSONArray;
import org.slim3.repackaged.org.json.JSONException;
import org.slim3.repackaged.org.json.JSONObject;

import team.dailymealjournal.dto.MealJournalDto;
import team.dailymealjournal.model.Journal;
import team.dailymealjournal.model.Meal;
import team.dailymealjournal.model.MealJournal;
import team.dailymealjournal.meta.MealJournalMeta;
import team.dailymealjournal.service.JournalService;
import team.dailymealjournal.service.MealJournalService;
import team.dailymealjournal.service.MealService;
import team.dailymealjournal.util.Utils;
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
 * [09/09/2015] 0.07 - Miguel Victor Remulta - Massive refactor
 */
public class JournalsController extends Controller {
    
    public static final String JOURNAL_NOT_FOUND = "MealJournal not found";
    public static final String JOURNAL_ID_WRONG_TYPE = "MealJournal ID must be a whole number";
    public static final String REQUEST_NOT_JSON = "Request body must be JSON";
    public static final String JOURNAL_ID_MISSING = "MealJournal ID is missing";
    
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
        String requestMethod = request.getMethod();
        
        if ("post".equalsIgnoreCase(requestMethod)) {
            performPost();
        } else if ("put".equalsIgnoreCase(requestMethod)) {
            performPut();
        } else if ("delete".equalsIgnoreCase(requestMethod)) {
            performDelete();
        } else {
            performGet();
        }
        
        return null;
    }
    
    /**
     * Method to perform if request is GET.
     * @return String - resulting JSON string.
     */
    private void performGet() throws Exception {
        String id = requestScope("id");
        
        if (null != id) { // get a single journal
            try {
                long mealJournalId = Long.parseLong(id);
                MealJournal mealJournal = mealJournalService.getMealJournal(mealJournalId);
                
                if (null != mealJournal) {
                    JSONObject mealJournalJson = new JSONObject(MealJournalMeta.get().modelToJson(mealJournal));
                    populateJournalJsonWithMeals(mealJournalJson);
                    Utils.writeJsonResponse(response, mealJournalJson);
                } else { // journal not found
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, JOURNAL_NOT_FOUND);
                }
            } catch (NumberFormatException e) { // wrong data type of id, bad request
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, JOURNAL_ID_WRONG_TYPE);
            }
        } else { // get all journals
            List<Journal> journalsList = journalService.getJournalList();
            JSONArray journalsArray = journalsToJson(journalsList);
            Utils.writeJsonResponse(response, journalsArray);
        }
    }
    
    /**
     * Method to perform if request is POST.
     * @return String - resulting JSON string.
     */
    private void performPost() throws IOException {
        try {
            JSONObject postData = new JSONObject(request.getReader().readLine());
            JSONValidators validator = new JSONValidators(postData);
            
            validator.add("mealId", validator.required(), validator.longType());
            validator.add("quantity", validator.required(), validator.integerType());
            
            if (validator.validate()) {
                dto.setMealId(postData.getLong("mealId"));
                dto.setQuantity(postData.getInt("quantity"));
                dto = mealJournalService.addMealJournal(dto);
            } else {
                List<String> errorList = new ArrayList<String>();
                validator.addErrorsTo(errorList);
                Utils.writeErrors(response, errorList, "Input Validation Error");
            }
        } catch (JSONException e) { // we can't understand the request's request body, bad request
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, REQUEST_NOT_JSON);
        }
    }
    
    /**
     * Method to perform if request is PUT.
     * @return String - resulting JSON string.
     */
    private void performPut() throws IOException {
        try {
            JSONObject postData = new JSONObject(request.getReader().readLine());
            JSONValidators validator = new JSONValidators(postData);
            
            validator.add("mealId", validator.required(), validator.longType());
            validator.add("mealJournalId", validator.required(), validator.longType());
            validator.add("quantity", validator.required(), validator.integerType());
            
            if (validator.validate()) {
                dto.setMealId(postData.getLong("mealId"));
                dto.setQuantity(postData.getInt("quantity"));
                dto.setMealJournalId(postData.getLong("mealJournalId"));
                dto = mealJournalService.editMealJournal(dto);
            } else {
                List<String> errorList = new ArrayList<String>();
                validator.addErrorsTo(errorList);
                Utils.writeErrors(response, errorList, "Input Validation Error");
            }
        } catch (JSONException e) { // we can't understand the request's request body, bad request
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, REQUEST_NOT_JSON);
        }
    }
    
    /**
     * Method to perform if request is DELETE.
     * @return String - resulting JSON string.
     */
    private void performDelete() throws IOException {
        String id = requestScope("id");
        
        if (null != id) {
            try {
                long mealJournalId = Long.parseLong(id);
                MealJournal mealJournal = mealJournalService.getMealJournal(mealJournalId);
                
                if (null != mealJournal) {
                    dto.setMealId(mealJournalId);
                    dto = mealJournalService.deleteMealJournal(dto);
                } else { // journal not found
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, JOURNAL_NOT_FOUND);
                }
            } catch (NumberFormatException e) { // wrong data type, bad request
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, JOURNAL_ID_WRONG_TYPE);
            }
        } else { // no id, bad request, we can't delete nothing XD
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, JOURNAL_ID_MISSING);
        }
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
