package com.graph;

import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by stcas on 2018/1/23.
 */
public class StringTest {
    public static void main(String[] args) {
        String s = "责任编辑（董成瑞）";
        System.out.println(ToAnalysis.parse(s).toStringWithOutNature(" "));

//        String s1 = StringUtils.removePattern(s,"（[^）]*）|\\([^\\)]*\\)");
//        System.out.println(s1);
    }
}
