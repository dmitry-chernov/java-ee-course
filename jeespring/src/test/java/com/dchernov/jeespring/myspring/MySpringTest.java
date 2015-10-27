/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.jeespring.myspring;

import com.dchernov.jeespring.myspring.annotations.MySpringBean;
import com.dchernov.jeespring.myspring.annotations.MySpringDestruct;
import com.dchernov.jeespring.myspring.annotations.MySpringInitValue;
import com.dchernov.jeespring.testclasses.TestBean;
import com.dchernov.jeespring.testclasses.TestSingleton;
import java.lang.reflect.AccessibleObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Dc
 */
public class MySpringTest {

    public MySpringTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getContext method, of class MySpring.
     */
    @Test
    public void testGetContext() {
        System.out.println("getContext");
        MySpringContext result = MySpring.getContext();
        assertNotNull(result);
    }

    /**
     * Test of setContext method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetContext() throws Exception {
        System.out.println("setContext");
        MySpring.getContext().destroyConext();
        MySpringContext c = new MySpring();
        MySpring.setContext(c);
        assertSame(c, MySpring.getContext());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test of setContext method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetContextTwice() throws Exception {
        System.out.println("setContext");
        MySpring.getContext();
        MySpringContext c = new MySpring();
        thrown.expect(Exception.class);
        thrown.expectMessage("Context is already set");
        MySpring.setContext(c);
    }

    /**
     * Test of getBean method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBean() throws Exception {
        System.out.println("getBean");
        MySpringContext context = MySpring.getContext();
        Object expResult = context.getBean(TestBeanDestruct.class);
        Object result = context.getBean(TestBeanDestruct.class);
        assertNotSame(expResult, result);
    }

    /**
     * Test of getBean method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetSingleton() throws Exception {
        System.out.println("getBean");
        MySpringContext context = MySpring.getContext();
        Object expResult = context.getBean(TestSingleton.class);
        Object result = context.getBean(TestSingleton.class);
        assertSame(expResult, result);
    }

    @MySpringBean
    public static class TestBeanDestruct {

        public boolean destructed = false;

        @MySpringDestruct
        public void destruct() {
            destructed = true;
        }
    };

    @MySpringBean(isSingleton = true)
    public static class TestBeanDestructSingleton extends TestBeanDestruct {

        @MySpringDestruct
        @Override
        public void destruct() {
            super.destruct();
        }
    };

    /**
     * Test of destroyBean method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDestroyBean() throws Exception {
        System.out.println("destroyBean");
        TestBeanDestruct bean = MySpring.getContext().getBean(TestBeanDestruct.class);
        MySpring.getContext().destroyBean(bean);
        assertTrue(bean.destructed);
    }

    /**
     * Test of destroyBean method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDestroyBeanSingleton() throws Exception {
        System.out.println("destroyBean");
        TestBeanDestructSingleton bean = MySpring.getContext().getBean(TestBeanDestructSingleton.class);
        MySpring.getContext().destroyBean(bean);
        assertFalse(bean.destructed);
    }

    /**
     * Test of destroyConext method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDestroyConext() throws Exception {
        System.out.println("destroyConext");
        TestBeanDestruct[] beans = fillDestroyConext();
        MySpring.getContext().destroyConext();
        for (TestBeanDestruct b : beans) {
            assertTrue(b.destructed);
        }
    }

    private TestBeanDestruct[] fillDestroyConext() throws Exception {
        MySpringContext c = MySpring.getContext();
        return new TestBeanDestruct[]{
            c.getBean(TestBeanDestruct.class),
            c.getBean(TestBeanDestruct.class),
            c.getBean(TestBeanDestructSingleton.class),
            c.getBean(TestBeanDestructSingleton.class)
        };
    }

    /**
     * Test of finalize method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("FinalizeCalledExplicitly")
    public void testFinalize() throws Exception, Throwable {
        System.out.println("finalize");
        TestBeanDestruct[] beans = fillDestroyConext();
        MySpring instance = (MySpring) MySpring.getContext();
        instance.finalize();
        for (TestBeanDestruct b : beans) {
            assertTrue(b.destructed);
        }

    }

    /**
     * Test of getMemberType method, of class MySpring.
     *
     * @throws java.lang.NoSuchMethodException
     */
    @Test
    public void testGetMemberType_Method_Object() throws NoSuchMethodException {
        System.out.println("getMemberType");
        MySpring instance = (MySpring) MySpring.getContext();
        Object[] t = new Object[]{
            new Object() {
                public void set(String v) {
                }
            },
            new Object() {
                public void set() {
                }
            },
            new Object() {
                public void set(String v1, String v2) {
                }
            }
        };
        Object[] result = new Object[]{
            instance.getMemberType(t[0].getClass().getMethod("set", String.class), t[0]),
            instance.getMemberType(t[1].getClass().getMethod("set"), t[1]),
            instance.getMemberType(t[2].getClass().getMethod("set", String.class, String.class), t[2])
        };
        assertArrayEquals(new Object[]{String.class, null, null}, result);
    }

    /**
     * Test of getMemberType method, of class MySpring.
     *
     * @throws java.lang.NoSuchFieldException
     */
    @Test
    public void testGetMemberType_Field_Object() throws NoSuchFieldException {
        Object a = new Object() {
            public int a;
        };
        System.out.println("getMemberType");
        MySpring instance = (MySpring) MySpring.getContext();
        Class<?> result = instance.getMemberType(a.getClass().getField("a"), a);
        assertEquals(Integer.TYPE, result);
    }

    /**
     * Test of getMemberInitValue method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetMemberInitValue() throws Exception {
        System.out.println("getMemberInitValue");
        MySpring c = (MySpring) MySpring.getContext();
        TestBean b = MySpring.getContext().getBean(TestBean.class);
        AccessibleObject[] m = new AccessibleObject[]{
            b.getClass().getMethod("mySet", String.class),
            b.getClass().getField("myInt")
        };
        MySpringInitValue[] annotation = new MySpringInitValue[]{
            m[0].getAnnotation(MySpringInitValue.class),
            m[1].getAnnotation(MySpringInitValue.class)
        };
        Object[] expResult = new Object[]{"Yes!", 5};
        Object[] result = new Object[]{
            c.getMemberInitValue(m[0], b, annotation[0]),
            c.getMemberInitValue(m[1], b, annotation[1])
        };
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of runAnnotatedMember method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRunAnnotatedMember() throws Exception {

        MySpring c = (MySpring) MySpring.getContext();

        System.out.println("runAnnotatedMember");
        TestBeanDestruct bean = new TestBeanDestruct();
        c.runAnnotatedMember(bean, bean.getClass(), MySpringDestruct.class);
        assertTrue(bean.destructed);
    }

    /**
     * Test of initBeanMembers method, of class MySpring.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitBeanMembers() throws Exception {
        System.out.println("initBeanMembers");
        MySpring instance = (MySpring) MySpring.getContext();
        TestBean b = new TestBean();
        instance.initBeanMembers(b, TestBean.class, TestBean.class.getMethods());
        instance.initBeanMembers(b, TestBean.class, TestBean.class.getFields());
        assertArrayEquals(new Object[]{5, "Yes!"}, new Object[]{b.myInt, b.myStr});
    }

}
