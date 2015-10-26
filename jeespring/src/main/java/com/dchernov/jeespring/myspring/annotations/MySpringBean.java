/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.jeespring.myspring.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Dc
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MySpringBean {
    boolean isSingleton() default false;
}

