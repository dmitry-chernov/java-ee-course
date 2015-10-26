/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.jeespring.testclasses;

import com.dchernov.jeespring.myspring.annotations.MySpringBean;
import com.dchernov.jeespring.myspring.annotations.MySpringConstruct;
import com.dchernov.jeespring.myspring.annotations.MySpringDestruct;
import com.dchernov.jeespring.myspring.annotations.MySpringInitValue;
import org.apache.log4j.Logger;

/**
 *
 * @author Dc
 */
@MySpringBean(isSingleton = true)
public class TestSingleton {

    private static final Logger log = Logger.getLogger(TestSingleton.class);

    @MySpringConstruct
    public void init() {
        log.trace("Singleton Initialized -- " + this.hashCode());
    }

    @MySpringDestruct
    public void destroy() {
        log.trace("Singleton Destroyed -- " + this.hashCode());
    }

    @MySpringInitValue(intValue = 55)
    public int myInt;

    @MySpringInitValue(stringValue = "Singleton Yes!")
    public void mySet(String v) {
        myStr = v;
    }
    public String myStr;
}
