/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito� - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */
package team.dailymealjournal.dao;

import java.util.Date;
import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.FilterCriterion;

import team.dailymealjournal.meta.JournalMeta;
import team.dailymealjournal.model.Journal;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

/**
* Dao used to access the datastore for journal transactions.
* @author Kim Agustin
* @version 0.02
* Version History
* [07/27/2015] 0.01 � Kim Agustin � Initial codes.
* [09/08/2015] 0.02 � Kim Agustin � Moved key initializations outside transaction.
*/
public class JournalDao {

    /**
     * Method used to save a journal.
     * @param JournalModel - Journal to be saved.
     * @return Boolean - true, if journal is saved; otherwise, false.
     */
    public boolean addJournal(Journal journalModel) {
        boolean result = true;
        try {
            // Manually allocate key
            Key key = Datastore.allocateId("Journal");
            journalModel.setJournalId(key.getId());
            
            Transaction tx = Datastore.beginTransaction();
            Datastore.put(journalModel);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * Method used to retrieve list of Journals.
     * @return List<Journal> - list of Journals.
     */
    public List<Journal> getAllJournals() {
        JournalMeta meta = new JournalMeta();
        return Datastore.query(meta).asList();
    }
    
    /**
     * Method used to retrieve a Journal using its ID.
     * @param long journalId
     * @return Journal.
     */
    public Journal getJournal(long journalId) {
        JournalMeta meta = new JournalMeta();
        FilterCriterion mainFilter = meta.journalId.equal(journalId);
        return Datastore.query(meta).filter(mainFilter).asSingle();
    }
    
    /**
     * Method used to retrieve a Journal using its ID.
     * @param Date dateCreated
     * @return Journal.
     */
    public Journal getJournal(Date dateCreated) {
        JournalMeta meta = new JournalMeta();
        FilterCriterion mainFilter = meta.dateCreated.equal(dateCreated);
        return Datastore.query(meta).filter(mainFilter).asSingle();
    }

    /**
     * Method used to edit a journal.
     * @param JournalModel - Journal to save.
     * @return Boolean - true, if journal is saved; otherwise, false.
     */
    public boolean editJournal(Journal journalModel) {
        boolean result = true;
        JournalMeta meta = new JournalMeta();
        FilterCriterion mainFilter = meta.journalId.equal(journalModel.getJournalId());

        try {
            Journal originalJournalModel = Datastore.query(meta).filter(mainFilter).asSingle();
            if (originalJournalModel != null) {
                originalJournalModel.setDateCreated(journalModel.getDateCreated());
                
                Transaction tx = Datastore.beginTransaction();
                Datastore.put(originalJournalModel);
                tx.commit();
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * Method used to delete a journal.
     * @param JournalModel - journal to delete.
     * @return Boolean - true, if journal is deleted; otherwise, false.
     */
    public boolean deleteJournal(Journal journalModel) {
        boolean result = true;
        JournalMeta meta = new JournalMeta();
        FilterCriterion mainFilter = meta.journalId.equal(journalModel.getJournalId());

        try {
            Journal originalJournalModel = Datastore.query(meta).filter(mainFilter).asSingle();
            if (originalJournalModel != null) {
                Transaction tx = Datastore.beginTransaction();
                Datastore.delete(originalJournalModel.getKey());
                tx.commit();
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
