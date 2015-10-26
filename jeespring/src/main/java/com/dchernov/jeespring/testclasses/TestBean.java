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
@MySpringBean
public class TestBean {

    private static final Logger log = Logger.getLogger(TestBean.class);

    @MySpringConstruct
    public void init() {
        log.trace("Initialized");
    }

    @MySpringDestruct
    public void destroy() {
        log.trace("Destroyed");
    }

    @MySpringInitValue(intValue = 5)
    public int myInt;

    @MySpringInitValue(stringValue = "Yes!")
    public void mySet(String v) {
        myStr = v;
    }
    public String myStr;
}
