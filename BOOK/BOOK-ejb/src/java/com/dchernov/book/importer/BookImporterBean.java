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
        try {
            Object object = ((ObjectMessage) message).getObject();
            if (message.getJMSRedelivered()) {
                int redeliverCount = message.getIntProperty("JMSXDeliveryCount");
                if (redeliverCount == 2) {
                    object = bookDAO.merge(object);
                    LOG.log(Level.INFO, "Already exists, update: {0}", object);
                } else {
                    LOG.log(Level.INFO, "Failed: {0}", object);
                }
            } else {
                object = bookDAO.persist(object);
                LOG.log(Level.INFO, "New item, insert: {0}", object);
            }
        } catch (JMSException ex) {
            Logger.getLogger(BookImporterBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
