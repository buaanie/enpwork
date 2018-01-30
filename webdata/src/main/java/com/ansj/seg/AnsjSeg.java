package com.ansj.seg;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.List;

/**
 * Created by niej on 2016/4/4.
 */
public class AnsjSeg {
    public String ansjSeg(String sSrc, int bPOSTagged) {
        if (bPOSTagged == 0) {
            String result = "";
            List<Term> parse = ToAnalysis.parse(sSrc).getTerms();
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
        } else if (bPOSTagged == 1) {
            List<Term> parse = NlpAnalysis.parse(sSrc).getTerms();
            return parse.toString().replace("[", "").replace("]", "");
        }
        return "";
    }
}
