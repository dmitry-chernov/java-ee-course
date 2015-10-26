/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.jeespring.myspring;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Dc
 */
public interface MySpringContext {

    public <T> T getBean(Class<T> beanClass) throws InstantiationException, IllegalAccessException, Exception;
    public <T> void destroyBean(Object bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
    public void destroyConext() throws InstantiationException, IllegalAccessException, Exception;
}
