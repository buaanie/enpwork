package com.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class Trim {
    public static void main(String[] args) {
        String regex = "</?a([^>]*)>";
        String s = "<a href=\"/item/%E4%B8%AD%E5%9B%BD/1122445\" data-lemmaid=\"1122445\">中国</a>";
        System.out.println(s.replaceAll(regex,"#"));
//        String s = "wpnfsfbn \ndsgnbierg\rdfg\r\ns";
//        System.out.println(s.trim());
//        Map<String,String> m = new HashMap<>();
//        m.put("aaas","sfs");
//        m.forEach((k,v)->System.out.print(k + " ," + v));
    }
}
