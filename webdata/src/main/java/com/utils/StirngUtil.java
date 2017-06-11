package com.utils;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class StirngUtil {
    private static final String labelRegex = "<[^>]+>";
    private static final String noteRegex = "\\[\\d+\\]";
    private static final String specialRegex = "\\&[a-zA-Z]{1,10};";

    /**
     * 从HTML文本中取得干净的输出
     * @param rawText
     * @return 去掉各类标签之后的文本
     */
    public static String tidyHTMLText(String rawText){
        String res = rawText.replaceAll(labelRegex,"").replaceAll(noteRegex,"").replaceAll(specialRegex,"").replace(" ","");
        return res;
    }
    /**
     * 取得String切分之后的最后一段
     * @param rawText
     * @param regex
     * @return 切分后的最后一段文本
     */
    public static String lastSplitSlice(String rawText,String regex){
        int length = rawText.split(regex).length;
        return rawText.split(regex)[length-1];
    }
}
