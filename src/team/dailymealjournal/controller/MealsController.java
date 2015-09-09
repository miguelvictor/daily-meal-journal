/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito™ - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */
package team.dailymealjournal.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.repackaged.org.json.JSONArray;
import org.slim3.repackaged.org.json.JSONException;
import org.slim3.repackaged.org.json.JSONObject;

import team.dailymealjournal.dto.MealDto;
import team.dailymealjournal.meta.MealMeta;
import team.dailymealjournal.model.Meal;
import team.dailymealjournal.service.MealService;
import team.dailymealjournal.util.Utils;
import team.dailymealjournal.validator.JSONValidators;

/**
 * Service used to handle meal transactions.
 * @author Kim Agustin
 * @version 0.06
 * Version History
 * [07/27/2015] 0.01 – Kim Agustin – Initial codes.
 * [08/07/2015] 0.02 – Kim Agustin – Migration to slim3_1.0.16.
 * [08/07/2015] 0.03 – Kim Agustin – Merged CRUD controllers into one.
 * [08/31/2015] 0.04 – Kim Agustin – Added validation support.
 * [08/31/2015] 0.05 – Kim Agustin – Restructured controller flow.
 * [09/01/2015] 0.06 – Miguel Victor Remulta – Added documentation.
 * [09/09/2015] 0.07 - Miguel Victor Remulta - Updated code to handle json data, Massive refactor
 */
public class MealsController extends Controller {
    
    public static final String MEAL_NOT_FOUND = "Meal not found";
    public static final String MEAL_ID_WRONG_TYPE = "Meal ID must be a whole number";
    public static final String REQUEST_NOT_JSON = "Request body must be JSON";
    public static final String MEAL_ID_MISSING = "Meal ID is missing";
    
    /**
     * The MealService to use.
     * Holds the method for adding a meal.
     */
    private MealService service = new MealService();
    
    /**
     * The MealMeta to use.
     * Responsible for converting the models to JSON format.
     */
    private MealMeta meta = MealMeta.get();
    
    /**
     * The MealDto to use.
     * Wrapper for input data of model.
     * Also contains the errors list.
     */
    private MealDto dto = new MealDto();

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
    private void performGet() throws JSONException, IOException {
        String id = requestScope("id");
        
        if (null != id) { // get a single meal
            try {
                long mealId = Long.parseLong(id);
                Meal meal = service.getMeal(mealId);
                if (null != meal) {
                    JSONObject mealJson = new JSONObject(meta.modelToJson(meal));
                    Utils.writeJsonResponse(response, mealJson);
                } else { // meal not found
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) { // wrong data type, bad request
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, MEAL_NOT_FOUND);
            }
        } else { // get all meals
            List<Meal> mealsList = service.getMealList();
            JSONArray mealsArray = new JSONArray(meta.modelsToJson(mealsList));
            Utils.writeJsonResponse(response, mealsArray);
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
            
            validator.add("name", validator.required());
            validator.add("unit", validator.required());
            validator.add("calories", validator.required(), validator.doubleType());
            validator.add("defaultQuantity", validator.required(), validator.integerType());
            
            if (validator.validate()) {
                dto.setName(postData.getString("name"));
                dto.setDefaultQuantity(postData.getInt("defaultQuantity"));
                dto.setCalories(postData.getDouble("calories"));
                dto.setUnit(postData.getString("unit"));
                dto = this.service.addMeal(dto);
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
            JSONObject postData = new JSONObject(this.request.getReader().readLine());
            JSONValidators validator = new JSONValidators(postData);
            
            validator.add("name", validator.required());
            validator.add("unit", validator.required());
            validator.add("calories", validator.required(), validator.doubleType());
            validator.add("defaultQuantity", validator.required(), validator.integerType());
            validator.add("mealId", validator.required(), validator.longType());
            
            if (validator.validate()) {
                dto.setName(postData.getString("name"));
                dto.setDefaultQuantity(postData.getInt("defaultQuantity"));
                dto.setCalories(postData.getDouble("calories"));
                dto.setUnit(postData.getString("unit"));
                dto.setMealId(postData.getLong("mealId"));
                dto = this.service.editMeal(dto);
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
                long mealId = Long.parseLong(id);
                Meal meal = service.getMeal(mealId);
                if (null != meal) {
                    dto.setMealId(mealId);
                    dto = service.deleteMeal(dto);
                } else { // meal not found
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, MEAL_NOT_FOUND);
                }
            } catch (NumberFormatException e) { // wrong data type, bad request
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, MEAL_ID_WRONG_TYPE);
            }
        } else { // no id, bad request
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, MEAL_ID_MISSING);
        }
    }
    
}
