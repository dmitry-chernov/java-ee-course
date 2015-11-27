/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.jaxb;

/**
 *
 * @author Dc
 */
public interface PreventCycleXml {

    boolean getXmlAlreadyProcessed();

    void setXmlAlreadyProcessed(boolean v);
}
