/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.dao;

import com.dchernov.orm.MergeObjectToExistingRecordByItsUniqueKey;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Dc
 */
@Stateless
@LocalBean
public class BookDAO {

    @EJB
    private MergeObjectToExistingRecordByItsUniqueKey mergeObject;

    private static final Logger LOG = Logger.getLogger(BookDAO.class.getName());

    @PersistenceContext(unitName = "BOOK-ejbPU")
    private EntityManager em;

    public <T> T persist(T object) {
        object = em.merge(object);
        em.flush();
        return object;
    }

    public <T> T merge(T object) {
        mergeObject.setObjectId(object, em);
        return persist(object);
    }
}
