/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.dao;

import com.dchernov.book.dao.exception.DuplicateUniqueKeyException;
import com.dchernov.orm.MergeObjectToExistingRecordByItsUniqueKey;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

/**
 *
 * @author Dc
 */
@Stateless
@LocalBean
public class BookDAO {

    private static final Logger LOG = Logger.getLogger(BookDAO.class.getName());

    @PersistenceContext(unitName = "BOOK-ejbPU")
    private EntityManager em;

    @EJB
    private MergeObjectToExistingRecordByItsUniqueKey mergeObject;

    @EJB
    BookDAO recursiveBookDAO;

    /**
     * 1. Try to insert new (if ID == null) or update by ID in a separate
     * transaction 2. On "duplicate unique key" exception: update by the unique
     * key marked by "@UniqueConstraint(name =
     * "IDENTITY_KEY_<an unique suffix>"...." in the current transaction
     *
     * @param <T> Entity class
     * @param object Entity instance to be processed
     * @return The object merged into EntityManager context
     */
    public <T> T persistOrMerge(T object) {
        try {
            object = recursiveBookDAO.mergeById(object);
        } catch (DuplicateUniqueKeyException ex) {
            object = mergeByKey(object);
        }
        return object;
    }

    /**
     * The method inserts new item (if ID == null or doesn't exists in the DB)
     * or updates existing item by ID
     *
     * @param <T> Entity class
     * @param object Entity instance to be processed
     * @return The object merged into EntityManager context
     * @throws DuplicateUniqueKeyException on unique key violation
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> T mergeById(T object) throws DuplicateUniqueKeyException {
        try {
            object = processObjectOrArray(object, (obj) -> {
                return em.merge(obj);
            });
            em.flush();
            printLOG("Item(s) are inserted or updated by ID", object);
        } catch (PersistenceException ex) {
            printLOG("Unique key violation, not inserted/updated", object);
            throw new DuplicateUniqueKeyException(ex);
        }
        return object;
    }

    /**
     * The method merges and updates item by the unique key marked by
     * "@UniqueConstraint(name = "IDENTITY_KEY"...." or inserts new item if the
     * unique key doesn't exist in DB
     *
     * @param <T> Entity class
     * @param object Entity instance to be processed
     * @return The object merged into EntityManager context
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> T mergeByKey(T object) {
        mergeObject.setObjectId(object, em);
        object = processObjectOrArray(object, (obj) -> {
            return em.merge(obj);
        });
        em.flush();
        printLOG("Item(s) are updated by key or inserted", object);
        return object;
    }

    public void printLOG(String msg, Object object) {
        if (object instanceof Object[]) {
            int i = 0;
            for (Object o : (Object[]) object) {
                LOG.log(Level.INFO, "{2} {1}: {0}", new Object[]{o, i++, msg});
            }
        } else {
            LOG.log(Level.INFO, "{1}: {0}", new Object[]{object, msg});
        }
    }

    ////////////////////////////////// private /////////////////////////////////////
    private <T> T processObjectOrArray(T objOrArr, Function<T, T> action) {
        if (objOrArr instanceof Object[]) {
            T[] arr = (T[]) objOrArr;
            for (int i = 0; i < arr.length; i++) {
                arr[i] = action.apply(arr[i]);
            }
        } else {
            objOrArr = action.apply(objOrArr);
        }
        return objOrArr;
    }
}
