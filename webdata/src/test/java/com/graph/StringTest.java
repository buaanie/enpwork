package com.graph;

import com.utils.Pair;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * Created by stcas on 2018/1/23.
 */
public class StringTest {
    public static void main(String[] args) {
//        String s = "责任编辑（董成瑞）";
//        System.out.println(ToAnalysis.parse(s).toStringWithOutNature(" "));
        StringTest s = new StringTest();
        HashMap<Integer,Integer> h = new HashMap<>();
        s.func(1,2,h);
        System.out.println(h.getOrDefault(1,0));
        System.out.println(h.getOrDefault(3,0));

//        String s1 = StringUtils.removePattern(s,"（[^）]*）|\\([^\\)]*\\)");
//        System.out.println(s1);
    }
    public void func(int a, int b,HashMap<Integer,Integer> res){
        res.put(a+b,b);
    }
}
