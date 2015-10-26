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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.apache.log4j.Logger;

/**
 *
 * @author Dc
 */
@MySpringBean
public class TestBean {

    @Retention(value = RetentionPolicy.RUNTIME)
    public static @interface TestCustom {

        String message();
    }

    private static final Logger log = Logger.getLogger(TestBean.class);

    @MySpringConstruct
    public void init() {
        log.trace("Initialized -- " + this.hashCode());
    }

    @MySpringDestruct
    public void destroy() {
        log.trace("Destroyed -- " + this.hashCode());
    }

    @MySpringInitValue(intValue = 5)
    @TestCustom(message = "CUSTOM!")
    public int myInt;

    @MySpringInitValue(stringValue = "Yes!")
    public void mySet(String v) {
        myStr = v;
    }
    public String myStr;
}
