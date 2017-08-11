package com.graph;

/**
 * Created by ACT-NJ on 2017/7/25.
 */
public class C3 {
    private C2 c;
    public C3(){
        c = C2.getC2();
    }
    public void print(){
        System.out.println(c.hashCode()+"---"+c.a);
    }

}
