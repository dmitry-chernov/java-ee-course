/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.dao;

import com.dchernov.orm.MergeObjectToExistingRecordByItsUniqueKey;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author Dc
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class BookDAO {

    public static class DuplicateUniqueKeyException extends Exception {

        public DuplicateUniqueKeyException(Throwable cause) {
            super(cause);
        }

    }

    private static final Logger LOG = Logger.getLogger(BookDAO.class.getName());

    @PersistenceContext(unitName = "BOOK-ejbPU")
    private EntityManager em;
    @Resource
    UserTransaction tx;

    @EJB
    private MergeObjectToExistingRecordByItsUniqueKey mergeObject;

    public <T> T persistOrMerge(T object) {
        try {
            object = persist(object);
        } catch (DuplicateUniqueKeyException ex) {
            object = merge(object);
        }
        return object;
    }

    public <T> T persist(T object) throws DuplicateUniqueKeyException {
        try {
            boolean useOwnTransaction = tx.getStatus() == Status.STATUS_NO_TRANSACTION;
            if (useOwnTransaction) {
                tx.begin();
            }
            object = processObjectOrArray(object, (obj) -> {
                return em.merge(obj);
            });
            em.flush();
            if (useOwnTransaction) {
                tx.commit();
            }
            printLOG("New item(s), inserted", object);
        } catch (PersistenceException ex) {
            try {
                printLOG("Already exists, not inserted ", object);
                tx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(BookDAO.class.getName()).log(Level.SEVERE, null, ex1);
            }
            throw new DuplicateUniqueKeyException(ex);
        } catch (RollbackException | SystemException | NotSupportedException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            throw new PersistenceException(ex);
        }
        return object;
    }

    public <T> T merge(T object) {
        try {
            boolean useOwnTransaction = tx.getStatus() == Status.STATUS_NO_TRANSACTION;
            if (useOwnTransaction) {
                tx.begin();
            }
            final EntityManager emm = em;
            object = processObjectOrArray(object, (obj) -> {
                return em.merge(mergeObject.setObjectId(obj, emm));
            });
            em.flush();
            if (useOwnTransaction) {
                tx.commit();
            }
            printLOG("Existing item(s), updated", object);
        } catch (RollbackException | SystemException | NotSupportedException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            throw new PersistenceException(ex);
        }
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
