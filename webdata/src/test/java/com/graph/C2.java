package com.graph;

/**
 * Created by ACT-NJ on 2017/7/25.
 */
public class C2 {
    private static C2 c = new C2();
    private C1 c1;
    private C2(){
        init();
    }
    public static String getC2(String s){
        return c.c1.res(s);
    }
    private void init() {
        c1= new C1();
        System.out.println("?????");
    }
}
