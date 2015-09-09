/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito™ - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */
package team.dailymealjournal.dao;

import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.FilterCriterion;

import team.dailymealjournal.meta.MealMeta;
import team.dailymealjournal.model.Meal;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

/**
* Dao used to access the datastore for meal transactions.s
* @author Kim Agustin
* @version 0.02
* Version History
* [07/27/2015] 0.01 – Kim Agustin – Initial codes.
* [09/08/2015] 0.02 – Kim Agustin – Moved key initializations outside transaction.
* [09/09/2015] 0.03 - Miguel Victor Remulta - Refactor
*/
public class MealDao {

    /**
     * The sole instance
     */
    private static MealDao SOLE_INSTANCE = new MealDao();
    
    private MealDao () {}
    
    /**
     * Persists a meal to the Datastore
     * @param meal the meal to be added
     * @return true if the transaction was successful, false otherwise
     */
    public boolean add(Meal meal) {
        boolean result = true;
        
        try {
            Key key = Datastore.allocateId(Meal.class);
            meal.setMealId(key.getId());
            
            Transaction tx = Datastore.beginTransaction();
            Datastore.put(meal);
            tx.commit();
        } catch (DatastoreFailureException e) {
            result = false;
        }
        
        return result;
    }

    /**
     * Returns all meals
     * @return the list of meals
     */
    public List<Meal> getAll() {
        MealMeta meta = MealMeta.get();
        return Datastore.query(meta).asList();
    }
    
    /**
     * Returns a meal with the given mealId
     * @param mealId the id of the meal
     * @return the meal
     */
    public Meal get(long mealId) {
        MealMeta meta = MealMeta.get();
        FilterCriterion filter = meta.mealId.equal(mealId);
        return Datastore.query(meta).filter(filter).asSingle();
    }

    /**
     * Updates a meal
     * @param meal the meal to update
     * @return true if the transaction was successful, false otherwise
     */
    public boolean update(Meal meal) {
        boolean result = true;
        MealMeta meta = MealMeta.get();
        FilterCriterion filter = meta.mealId.equal(meal.getMealId());

        try {
            Meal persistedMeal = Datastore.query(meta).filter(filter).asSingle();
            
            if (persistedMeal != null) {
                persistedMeal.setName(meal.getName());
                persistedMeal.setUnit(meal.getUnit());
                persistedMeal.setCalories(meal.getCalories());
                persistedMeal.setDefaultQuantity(meal.getDefaultQuantity());
                
                Transaction tx = Datastore.beginTransaction();
                Datastore.put(persistedMeal);
                tx.commit();
            } else {
                result = false;
            }
        } catch (DatastoreFailureException e) {
            result = false;
        }
        
        return result;
    }

    /**
     * Deletes a meal
     * @param meal the meal to be deleted
     * @return true if the transaction was successful, false otherwise
     */
    public boolean delete(Meal meal) {
        boolean result = true;
        MealMeta meta = MealMeta.get();
        FilterCriterion filter = meta.mealId.equal(meal.getMealId());

        try {
            Meal persistedMeal = Datastore.query(meta).filter(filter).asSingle();
            
            if (persistedMeal != null) {
                Transaction tx = Datastore.beginTransaction();
                Datastore.delete(persistedMeal.getKey());
                tx.commit();
            } else {
                result = false;
            }
        } catch (DatastoreFailureException e) {
            result = false;
        }
        
        return result;
    }
    
    /**
     * Returns the sole singleton instance
     * @return the sole singleton instance
     */
    public static MealDao getInstance () {
        return SOLE_INSTANCE;
    }
}
