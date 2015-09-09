/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito™ - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */
package team.dailymealjournal.dao;

import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.FilterCriterion;

import team.dailymealjournal.meta.MealJournalMeta;
import team.dailymealjournal.model.Journal;
import team.dailymealjournal.model.MealJournal;
import team.dailymealjournal.service.JournalService;
import team.dailymealjournal.utils.Utils;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

/**
* Dao used to access the datastore for mealJournal transactions.s
* @author Kim Agustin
* @version 0.04
* Version History
* [07/28/2015] 0.01 – Kim Agustin – Initial codes.
* [08/30/2015] 0.02 – Kim Agustin – Updated addMealJournal algorithm.
* [08/30/2015] 0.03 – Kim Agustin – Changed deleteMealJournal into cascading.
* [09/08/2015] 0.04 – Kim Agustin – Moved key initializations outside transaction.
* [09/09/2015] 0.03 - Miguel Victor Remulta - Added method to determine the number of today's mealjournals, Refactored codes, Modifed Documentation
*/
public class MealJournalDao {

    private static MealJournalDao SOLE_INSTANCE = new MealJournalDao();
    
    private MealJournalDao () {}
    
    /**
     * Persists a mealJournal to the Datastore. Creates a Journal if the
     * mealJournal passed is the first entry of the day which is determined
     * if the passed journal is null.
     * 
     * @param journal the parent journal
     * @param mealJournal the mealJouranl to be added
     * @return boolean - true if the transaction was successful, false otherwise
     */
    public boolean add(Journal journal, MealJournal mealJournal) {
        boolean result = true;
        
        if (null == journal) { // if first entry for the day, create new journal
            Key parentKey = Datastore.allocateId(Journal.class);
            journal = new Journal();
            journal.setKey(parentKey);
            journal.setJournalId(parentKey.getId());
            journal.setDateCreated(Utils.getCurrentDate());
        }
        
        // Manually allocate key
        Key key = Datastore.allocateId(journal.getKey(), MealJournal.class);
        mealJournal.setKey(key);
        mealJournal.setMealJournalId(key.getId());
        mealJournal.getJournalRef().setModel(journal);
        
        try {
            Transaction tx = Datastore.beginTransaction();
            Datastore.put(journal, mealJournal);
            tx.commit();
        } catch (DatastoreFailureException e) {
            result = false;
        }
        
        return result;
    }

    /**
     * Returns all MealJournals
     * @return the list of all MealJournals
     */
    public List<MealJournal> getAll() {
        MealJournalMeta meta = MealJournalMeta.get();
        return Datastore.query(meta).asList();
    }
    
    /**
     * Returns the MealJournal with the given id
     * @param mealJournalId the id of the MealJournal
     * @return the MealJournal
     */
    public MealJournal get(long mealJournalId) {
        MealJournalMeta meta = MealJournalMeta.get();
        FilterCriterion filter = meta.mealJournalId.equal(mealJournalId);
        return Datastore.query(meta).filter(filter).asSingle();
    }

    /**
     * Updates a MealJournal
     * @param mealJournal the MealJournal to be updated
     * @return true if the transaction was successful, false otherwise
     */
    public boolean update(MealJournal mealJournal) {
        boolean result = true;
        MealJournalMeta meta = MealJournalMeta.get();
        FilterCriterion filter = meta.mealJournalId.equal(mealJournal.getMealJournalId());

        try {
            MealJournal persistedMealJournal = Datastore.query(meta).filter(filter).asSingle();
            
            if (persistedMealJournal != null) {
                persistedMealJournal.setMealId(mealJournal.getMealId());
                persistedMealJournal.setQuantity(mealJournal.getQuantity());
                
                Transaction tx = Datastore.beginTransaction();
                Datastore.put(persistedMealJournal);
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
     * Deletes a MealJournal
     * @param mealJournal the MealJournal to be deleted
     * @return true if the transaction was successful, false otherwise
     */
    public boolean delete(MealJournal mealJournal) {
        boolean result = true;
        MealJournalMeta meta = MealJournalMeta.get();
        FilterCriterion filter = meta.mealJournalId.equal(mealJournal.getMealJournalId());

        try {
            mealJournal = Datastore.query(meta).filter(filter).asSingle();
                        
            if (mealJournal != null) {
                boolean deleteAll = true;
                Journal journal = new JournalService().getJournal(mealJournal.getKey().getParent().getId());
                if (journal.getMealJournalListRef().getModelList().size() > 1) {
                    deleteAll = false;
                }
                
                Transaction tx = Datastore.beginTransaction();
                if (deleteAll) {
                    Datastore.deleteAll(mealJournal.getKey().getParent());
                } else {
                    Datastore.delete(mealJournal.getKey());
                }
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
     * Returns the number of MealJournal that was created today
     * @param todaysJournal the Journal that was created today
     * @return the number of MealJournal that was created today
     */
    public int getTodaysJournalCount (Journal todaysJournal) {
        if (null != todaysJournal) {
            MealJournalMeta meta = MealJournalMeta.get();
            FilterCriterion filter = meta.journalRef.equal(todaysJournal.getKey());
            return Datastore.query(meta).filter(filter).count(); 
        }
        
        return 0;
    }
    
    /**
     * Returns the sole singleton instance
     * @return the sole singleton instance
     */
    public static MealJournalDao getInstance () {
        return SOLE_INSTANCE;
    }
}
