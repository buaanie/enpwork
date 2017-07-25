package com.graph;

/**
 * Created by ACT-NJ on 2017/7/25.
 */
public class C2 {
    private static C2 c = new C2();
    private int b=0;
    private C1 a;
    private C2(){
        a = new C1();
        b++;
        System.out.println(a.a);
        System.out.println(b+"  ---");
    }
    public static C2 getC(){
        return c;
    }
}
