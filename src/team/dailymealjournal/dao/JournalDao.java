/* ------------------------------------------------------------------------------
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Copyright (C) Miguelito™ - All Rights Reserved 2015
 * --------------------------------------------------------------------------- */
package team.dailymealjournal.dao;

import java.util.Date;
import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.FilterCriterion;

import team.dailymealjournal.meta.JournalMeta;
import team.dailymealjournal.model.Journal;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

/**
* Dao used to access the datastore for journal transactions.
* @author Kim Agustin
* @version 0.02
* Version History
* [07/27/2015] 0.01 – Kim Agustin – Initial codes.
* [09/08/2015] 0.02 – Kim Agustin – Moved key initializations outside transaction.
* [09/09/2015] 0.03 - Miguel Victor Remulta - Refactor
*/
public class JournalDao {

    /**
     * The sole instance
     */
    private static JournalDao SOLE_INSTANCE = new JournalDao();
    
    private JournalDao () {}
    
    /**
     * Persists a journal to the Datastore
     * @param journal - the journal to be saved
     * @return true, if journal is saved; otherwise, false.
     */
    public boolean add(Journal journal) {
        boolean result = true;
        
        try {
            Key key = Datastore.allocateId(Journal.class);
            journal.setJournalId(key.getId());
            
            Transaction tx = Datastore.beginTransaction();
            Datastore.put(journal);
            tx.commit();
        } catch (DatastoreFailureException e) {
            result = false;
        }
        
        return result;
    }

    /**
     * Returns a list of all journals
     * @return the list of journals.
     */
    public List<Journal> getAll() {
        JournalMeta meta = JournalMeta.get();
        return Datastore.query(meta).asList();
    }
    
    /**
     * Returns a journal with the given journalId
     * @param long journalId
     * @return the journal
     */
    public Journal get(long journalId) {
        JournalMeta meta = JournalMeta.get();
        FilterCriterion filter = meta.journalId.equal(journalId);
        return Datastore.query(meta).filter(filter).asSingle();
    }
    
    /**
     * Returns the journal with the given dateCreated
     * @param Date dateCreated
     * @return the journal
     */
    public Journal get(Date dateCreated) {
        JournalMeta meta = JournalMeta.get();
        FilterCriterion filter = meta.dateCreated.equal(dateCreated);
        return Datastore.query(meta).filter(filter).asSingle();
    }

    /**
     * Updates the journal
     * @param journal the journal to be updated
     * @return true, if journal is saved; otherwise, false.
     */
    public boolean update(Journal journal) {
        boolean result = true;
        JournalMeta meta = JournalMeta.get();
        FilterCriterion filter = meta.journalId.equal(journal.getJournalId());

        try {
            Journal persistedJournal = Datastore.query(meta).filter(filter).asSingle();
            
            if (persistedJournal != null) {
                persistedJournal.setDateCreated(journal.getDateCreated());
                Transaction tx = Datastore.beginTransaction();
                Datastore.put(persistedJournal);
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
     * Deletes a journal based from source
     * @param journal
     * @return true if the delete was successful, false otherwise
     */
    public boolean delete(Journal journal) {
        boolean result = true;
        JournalMeta meta = JournalMeta.get();
        FilterCriterion filter = meta.journalId.equal(journal.getJournalId());

        try {
            Journal persistedJournal = Datastore.query(meta).filter(filter).asSingle();
            
            if (persistedJournal != null) {
                Transaction tx = Datastore.beginTransaction();
                Datastore.delete(persistedJournal.getKey());
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
    public static JournalDao getInstance () {
        return SOLE_INSTANCE;
    }

}
