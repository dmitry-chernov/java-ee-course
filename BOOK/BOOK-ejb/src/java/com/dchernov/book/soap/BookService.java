/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.soap;

import com.dchernov.book.entity.Publisher;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

/**
 *
 * @author Dc
 */
@WebService(serviceName = "book")
@Stateless()
public class BookService {

    @Resource(mappedName = "jms/book/importQueue")
    private Queue importQueue;

    @Inject
    @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
    private JMSContext context;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    @WebMethod(operationName = "importPublisher")
    public Publisher importPublisher(@WebParam(name = "publisher") Publisher item) {
        return importItem(item);
    }

    @WebMethod(operationName = "importListOfPublishers")
    public Publisher[] importListOfPublishers(@WebParam(name = "publisher") Publisher[] item) {
        return importItem(item);
    }
    
    private <T extends Serializable> T importItem(T item) {
        JMSProducer producer = context.createProducer();
        ObjectMessage message = context.createObjectMessage();
        try {
            message.setObject(item);
        } catch (JMSException ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(importQueue, message);
        return item;
    }
}
