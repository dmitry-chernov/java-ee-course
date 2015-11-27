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

    class Impl {

        boolean xmldone = false;
    };
    Impl x = new Impl();

    default boolean getAlreadyProcessed() {
        return x.xmldone;
    }

    default void setAlreadyProcessed(boolean v) {
        x.xmldone = v;
    }
}
