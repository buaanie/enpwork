package com.graph;

/**
 * Created by ACT-NJ on 2017/7/25.
 */
public class C2 {
    private static C2 c = new C2();
    private C1 c1;
    public int a = 0;
    private C2(){
        init();
    }
    public static C2 getC2(){
        return c;
    }
    private void init() {
        a++;
        c1= new C1();
        System.out.println("?????");
    }
}
