/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.jeespring.myspring;

import com.dchernov.jeespring.myspring.annotations.MySpringBean;
import com.dchernov.jeespring.myspring.annotations.MySpringConstruct;
import com.dchernov.jeespring.myspring.annotations.MySpringDestruct;
import com.dchernov.jeespring.myspring.annotations.MySpringInitValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Dc
 */
public class MySpring implements MySpringContext {

    //////////////////// Interface ///////////////////////
    /**
     *
     * @return The context set by {@link #setContext(MySpringContext)
     * } or default context if not set
     */
    public static MySpringContext getContext() {
        return context == null ? new MySpring() : context;
    }

    /**
     *
     * @param c The custom context extends MySpring. Can be set once; can't be
     * changed;
     * @throws Exception
     */
    public static void setContext(MySpringContext c) throws Exception {
        if (context == null) {
            context = c;
        } else {
            throw new Exception("Context is already set and can't be changed");
        }
    }

    private static final Logger log = Logger.getLogger(MySpring.class);
    private static MySpringContext context;

    @Override
    public final <T> T getBean(Class<T> beanClass) throws InstantiationException, IllegalAccessException, Exception {

        T r = newBean(beanClass);
        initBeanMembers(r, beanClass, beanClass.getDeclaredFields());
        initBeanMembers(r, beanClass, beanClass.getDeclaredMethods());
        runAnnotatedMember(r, beanClass, MySpringConstruct.class);
        return r;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void destroyBean(Object bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (beansCache.contains(bean)) {
            runBeanDestructor(bean);
            beansCache.remove(bean);
        }
    }

    @Override
    public void destroyConext() {
        for (Object b : beansCache) {
            try {
                runBeanDestructor(b);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                log.warn("", ex);
            }
        }
        beansCache.clear();
        for (Object b : singletonCache.values()) {
            try {
                runBeanDestructor(b);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                log.warn("", ex);
            }
        }
        singletonCache.clear();
    }

    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        destroyConext();
        super.finalize();
    }

    ////////// Customization ////////
    protected Class<?> getMemberType(Method m, Object bean) {
        Class<?> type = null;
        if (((Method) m).getParameterCount() == 1) {
            type = ((Method) m).getParameterTypes()[0];
        } else {
            log.debug(bean.getClass().getName() + "." + ((Method) m).getName() + "() shall have 1 argument");
        }
        return type;

    }

    protected Class<?> getMemberType(Field m, Object bean) {
        return m.getType();

    }

    protected Object getMemberInitValue(AccessibleObject m, Object bean, MySpringInitValue annotation) {
        Class<?> type = m instanceof Method ? getMemberType((Method) m, bean) : getMemberType((Field) m, bean);
        Object r = null;
        if (type == java.lang.Integer.TYPE) {
            r = annotation.intValue();
        } else if (String.class.equals(type)) {
            r = annotation.stringValue();
        } else {
            log.warn("Annotation MySpringInitValue is not supported for type '" + type.getName() + "' or wrong number of args");
        }
        return r;
    }

    ////////// Internal ////////
    protected final void runAnnotatedMember(Object bean, Class<?> beanClass, Class<? extends Annotation> anno) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (Method m : beanClass.getDeclaredMethods()) {
            if (m.getAnnotation(anno) != null) {
                m.invoke(bean);
                break;
            }
        }
    }

    protected final <T, M extends AccessibleObject> void initBeanMembers(T bean, Class<T> beanClass, M[] members) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (M m : members) {
            MySpringInitValue annotation = m.getAnnotation(MySpringInitValue.class);
            if (annotation != null) {
                Object value = getMemberInitValue(m, bean, annotation);
                if (value != null) {
                    if (m instanceof Method) {
                        ((Method) m).invoke(bean, value);
                        if (log.isTraceEnabled()) {
                            log.trace("Call setter " + beanClass.getName() + "."
                                    + ((Method) m).getName()
                                    + "(" + value + ")");
                        }
                    } else {
                        ((Field) m).set(bean, value);
                        if (log.isTraceEnabled()) {
                            log.trace("Set field " + beanClass.getName() + "."
                                    + ((Field) m).getName()
                                    + " = " + value);
                        }
                    }
                }
            }
        }
    }

    private <T> T newBean(Class<T> beanClass) throws InstantiationException, IllegalAccessException, Exception {
        MySpringBean annotation = beanClass.getAnnotation(MySpringBean.class);
        if (annotation == null) {
            Exception ex = new Exception("This is not a Bean");
            log.error(beanClass.getName() + ex.getMessage(), ex);
            throw ex;
        }
        T r;
        if (annotation.isSingleton()) {
            r = (T) singletonCache.get(beanClass);
            if (r == null) {
                r = beanClass.newInstance();
                singletonCache.put(beanClass, r);
            }
        } else {
            r = beanClass.newInstance();
            beansCache.add(r);
        }
        return r;
    }

    @SuppressWarnings("empty-statement")
    private void runBeanDestructor(Object bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        runAnnotatedMember(bean, bean.getClass(), MySpringDestruct.class);
    }
    private Set<Object> beansCache = new HashSet<>();
    private Map<Class, Object> singletonCache = new HashMap<>();
}
