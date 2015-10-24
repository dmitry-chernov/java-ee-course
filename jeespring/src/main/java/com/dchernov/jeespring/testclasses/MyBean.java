/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.jeespring.testclasses;

/**
 *
 * @author Dc
 */
//@Init(default=5, name="my-integer")
public class MyBean {

//    @Init(default=5, name="my-integer")
    public int myInt;
    String myStr;

  //  @Init(default=5, name="my-integer")
    void mySet(String v) {
        myStr = v;
    }
}
