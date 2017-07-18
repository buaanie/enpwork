package com.graph;

import org.apache.hadoop.hbase.util.Bytes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class Trim {
    public static void main(String[] args) {
        String s ="http://comment.news.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/%s/comments/newList?offset=50&limit=40&headLimit=3&tailLimit=1&ibc=newswap";
        Matcher m = Pattern.compile("offset=(\\d+)").matcher(s);
        if(m.find())
            System.out.println(m.group(1));
//        String s = "http://3g.163.com/news/article/CPIRSAQQ0001899N.html";
//        System.out.println(s.matches("http://3g\\.163\\.com/news/\\S+"));
//        String s = "https://coral.qq.com/article/%s/comment?commentid=%s&tag=&reqnum=20";
//        System.out.println(String.format(s,"1233","ww"));

//        String s = "【环球时报-环球网报道 记者 白云怡】:2017年日，一则“中国游客在泰国国际机场内吸食大麻，安保人员放任不管”的消息在泰国脸书上引发网友热烈讨论，发帖人Netchanok Mingkwan女士称，她在泰国曼谷素万那普国际机场D8候机区域的吸烟室，看到一个中国旅游团的大爷，把大麻放在一个经过改装，戳有气孔的塑料瓶子上点燃并吸食，搞得里面乌烟瘴气，她质疑中国游客如何将大麻带入泰国境内。";
////        Pattern p = Pattern.compile("[\\s|\\S]+）");
////        Matcher m = p.matcher(s);
////        while(m.find())
////            System.out.println(m.group(0));
//        System.out.println(s.replaceAll("[\\s|\\S]+[）|)|】]:?",""));
//        System.out.println(Bytes.toLong(Bytes.toBytes("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u000E")));
//        String s = "nihao/a/sd/2913/24.html";
//        System.out.println(s.replaceAll("[^\\d]",""));
//        String s = "nihao,wohao,dajiahao，都是真的好";
//        System.out.println(s.split(",",2)[1]);
//        Stri ng IGNORE_PAGE = "http://(meda|game|tv|pic)\\.people\\.com\\.cn/n1/\\d{4}/\\d{4}/\\S+";
//
//        String s = "http://media.people.com.cn/n1/2017/0621/c14677-29352617.html";
//        System.out.println(s.matches(IGNORE_PAGE));
//        String regex = "</?a([^>]*)>";
//        String s = "<a href=\"/item/%E4%B8%AD%E5%9B%BD/1122445\" data-lemmaid=\"1122445\">中国</a>";
//        System.out.println(s.replaceAll(regex,"#"));
//        String s = "wpnfsfbn \ndsgnbierg\rdfg\r\ns";
//        System.out.println(s.trim());
//        Map<String,String> m = new HashMap<>();
//        m.put("aaas","sfs");
//        m.forEach((k,v)->System.out.print(k + " ," + v));
    }
}
