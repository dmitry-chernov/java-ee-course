/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.jaxb;

import java.util.logging.Logger;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Dc
 */
public class PreventCycleXmlJavaTypeAdapter<T extends PreventCycleXml> extends
        XmlAdapter<T, T> {

    @Override
    public T unmarshal(T v) throws Exception {
        return v;
    }

    @Override
    public T marshal(T v) throws Exception {
        if (v != null && !((PreventCycleXml) v).getAlreadyProcessed()) {
            ((PreventCycleXml) v).setAlreadyProcessed(true);
        } else {
            v = null;
        }
        return v;
    }
}
