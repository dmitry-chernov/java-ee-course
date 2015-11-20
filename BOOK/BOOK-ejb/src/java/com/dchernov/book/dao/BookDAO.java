/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.dao;

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
    @PersistenceContext(unitName = "BOOK-ejbPU")
    private EntityManager em;

    public <T> T persist(T object) {
        object = em.merge(object);
        em.flush();
        return object;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
