/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.jeespring;

import com.dchernov.jeespring.myspring.MySpring;
import com.dchernov.jeespring.myspring.MySpringContext;
import com.dchernov.jeespring.myspring.annotations.MySpringInitValue;
import com.dchernov.jeespring.testclasses.TestBean;
import com.dchernov.jeespring.testclasses.TestSingleton;
import java.lang.reflect.AccessibleObject;
import org.apache.log4j.Logger;

/**
 *
 * @author Dc
 */
public class Main {
    
    private static final Logger log = Logger.getLogger(Main.class);
    
    public static void main(String[] args) {
        try {
            log.info("Hello world!");
            
            MySpring theCustomSpring = new MySpring() {
                
                @Override
                protected Object getMemberInitValue(AccessibleObject m, Object bean, MySpringInitValue annotation) {
                    TestBean.TestCustom a = m.getAnnotation(TestBean.TestCustom.class);
                    if (a != null) {
                        log.info(a.message());
                    }
                    return super.getMemberInitValue(m, bean, annotation);                    
                }
                
            };
            MySpring.setContext(theCustomSpring);
MySpring.setContext(theCustomSpring);
            MySpringContext context = MySpring.getContext();
            TestBean b1 = context.getBean(TestBean.class);
            TestBean b2 = context.getBean(TestBean.class);
            log.info("Object 1: " + b1.myStr + " -- " + b1.myInt);
            log.info("Object 2: " + b2.myStr + " -- " + b2.myInt);
            b1.mySet("String changed");
            log.info("Object 1 changed: " + b1.myStr + " -- " + b1.myInt + " --- " + b1.hashCode());
            log.info("Object 2 unchanged: " + b2.myStr + " -- " + b2.myInt+ " --- " + b2.hashCode());
            
            TestSingleton s1 = context.getBean(TestSingleton.class);
            TestSingleton s2 = context.getBean(TestSingleton.class);
            log.info("Singleton 1: " + s1.myStr + " -- " + s1.myInt + " --- " + s1.hashCode());
            log.info("Singleton 2: " + s2.myStr + " -- " + s2.myInt+ " --- " + s2.hashCode());
            s2.mySet("String changed");
            log.info("Singleton 1 changed: " + s1.myStr + " -- " + s2.myInt);
            log.info("Singleton 2 changed: " + s1.myStr + " -- " + s2.myInt);
            
            context.destroyBean(b2);
            log.info("=== Destroy All ===");
            context.destroyConext();
        } catch (Exception ex) {
            log.error("ERROR", ex);
        }
    }
    
}
