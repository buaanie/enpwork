package com.ansj.seg;

import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;

/**
 * Created by ACT-NJ on 2017/7/24.
 */
public class ansjSeg {
    // ansj 词性： http://blog.csdn.net/lb521200200/article/details/53504839
    public static String ansjSeg(String sSrc, int bPOSTagged) {
        if (bPOSTagged == 1) {
            Result parse = ToAnalysis.parse(sSrc);
            String parseString = parse.toString().replace("[", "").replace("]", "");
            return parseString.replaceAll("(?<=[/\\S+]),"," ");
        } else if (bPOSTagged == 0) {
            String result = "";
            Result parse = ToAnalysis.parse(sSrc);
            String[] cmtWords = parse.toString().replace("[", "").replace("]", "").split("\\s+");
            for (String wordPosStr : cmtWords) {
                String[] wordPosPair = wordPosStr.split("/");
                if (wordPosPair.length < 2) {
                    continue;
                } else if (wordPosPair[0].length() > 1) {
                    result += wordPosPair[0] + " ";
                }
            }
            return result;
        }
        return "";
    }

    public static void main(String[] args) {
        String s = "天津爆炸事件";
        System.out.println(ansjSeg(s,1));
    }
}
