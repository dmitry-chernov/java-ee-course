/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.importer;

import com.dchernov.book.dao.BookDAO;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Dc
 */
@MessageDriven(name = "BookImporter", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/book/importQueue")
})
public class BookImporterBean implements MessageListener {

    private static final Logger LOG = Logger.getLogger(BookImporterBean.class.getName());

    @EJB
    private BookDAO bookDAO;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Object object = objectMessage.getObject();
            object = bookDAO.persist(object);
            LOG.log(Level.INFO, "MSG>>>>>>>>{0}", object);
        } catch (JMSException ex) {
            Logger.getLogger(BookImporterBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
