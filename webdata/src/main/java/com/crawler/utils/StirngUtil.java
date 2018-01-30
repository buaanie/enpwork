package com.crawler.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.nlpcn.commons.lang.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class StirngUtil {

//    public static final String UA1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
    public static final String UA1 = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    public static final String UA1_2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
    public static final String UA2 = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
    public static final String TIME_REGEX = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}";
    public static final String TIME_REGEX2 = "\\d{4}年\\d{2}月\\d{2}日\\d{2}\\:\\d{2}";
    public static final String WORD_REGEX = "\\w+：";
    private static final String labelRegex = "<[^>]+>";
    private static final String noteRegex = "\\[\\d+\\]";
    private static final String specialRegex = "\\&[a-zA-Z]{1,10};";

    /**
     * 从HTML文本中取得干净的输出
     * @param rawText
     * @return 去掉各类标签之后的文本
     */
    public static String tidyHTMLText(String rawText){
//        StringUtil.rmHtmlTag(rawText);
        String res = rawText.replaceAll(labelRegex,"").replaceAll(noteRegex,"").replaceAll(specialRegex," ").trim();
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

    /**
     * 去掉重复的id，返回set
     * @param list
     * @return 去重后的set
     */
    public static Set idList2Set(List<String> list){
        List<String> list2 = new ArrayList<>();
        list.forEach(key-> {if(!key.isEmpty()) list2.add(key);});
        return new HashSet(list2);
    }
    public static String getChineseTime(String time){
        String res = time.replaceAll("年|月","-").replace("日"," ");
        return res;
    }
    public static String filtChinese(String input){
        return input.replaceAll(WORD_REGEX,"");
    }
    public static String filtJournal(String p){
        String journalHead = "([\\s|\\S]*?[）|】]：?)";
        Pattern pattern = Pattern.compile(journalHead);
        Matcher matcher = pattern.matcher(p);
        if(matcher.find()) {
            p = p.replace(matcher.group(0), "");
        }
        else if(p.split(" ").length>1)
            p = p.split(" ",2)[1];
        return p;
    }
    public static String filtPhoto(String p){
        p = p.replaceAll("\n","");
        String spHead = "<div class=\"spinfo\"[^>].*>";
        String photoHead = "<div class=[\"photo|video\"]+>.*?</a|p>";
        p = p.replaceAll(spHead,"").replaceAll(photoHead,"").replaceAll(labelRegex,"");
        return p;
    }
    public static JSONObject parseJsonp(String jsonp, String mark){
        jsonp = jsonp.replaceAll("\n","");
        int startIndex = 0,endIndex = 0;
        switch (mark){
            case "(":
                startIndex = jsonp.indexOf("(");endIndex = jsonp.lastIndexOf(")");
                break;
            case "{":
                startIndex = jsonp.indexOf("{")-1;endIndex = jsonp.lastIndexOf("}")+1;
                break;
            default:break;
        }
        String json = jsonp.substring(startIndex+1, endIndex).replace("…","");
        return JSONObject.parseObject(json);
    }
}
