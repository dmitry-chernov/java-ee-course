/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.soap;

import com.dchernov.book.dao.BookDAO;
import com.dchernov.book.entity.Publisher;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author Dc
 */
@WebService(serviceName = "book")
@Stateless()
public class BookService {
    @EJB
    private BookDAO bookDAO;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    @WebMethod(operationName = "importPublisher")
    public Publisher importPublisher(@WebParam(name = "publisher") Publisher item) {
        item = bookDAO.persist(item);
        return item;
    }
}
