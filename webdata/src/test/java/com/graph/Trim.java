package com.graph;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.util.Bytes;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.crawler.utils.StirngUtil.TIME_REGEX;
import static com.crawler.utils.StirngUtil.lastSplitSlice;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class Trim {
    public static void main(String[] args) {
        String template = "{" +
                "    index: {" +
                "        analysis: {" +
                "            analyzer: {" +
                "                \"word_analyzer\":{" +
                "                    type: \"custom\"," +
                "                    tokenizer: \"word_tokenizer\"" +
                "                }," +
                "                \"id_analyzer\": {" +
                "                    type: \"custom\"," +
                "                    tokenizer: \"id_tokenizer\"" +
                "                }" +
                "            }," +
                "            tokenizer: {" +
                "                \"word_tokenizer\": {" +
                "                    pattern: \"\\\\s|,|，\"," +
                "                    type: \"pattern\"" +
                "                }," +
                "                \"id_tokenizer\": {" +
                "                    pattern: \"-\"," +
                "                    type: \"pattern\"" +
                "                }" +
                "            }" +
                "        }" +
                "    }" +
                "}";
        System.out.println(JSONObject.parse(template));
//        int n =133242354;
//        System.out.println(n%8);
//        System.out.println(7&n);
//        String s = "2017-08-07 19:56:46";
//        DateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm", Locale.CHINA);
//        try {
//            System.out.println(sdf.parse(s));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String s =  "wb_verified=0&wb_screen_name=杨大洁子&wb_cmnt_type=comment_status&wb_user_id=5351052638&wb_description=&wb_parent=&wb_profile_img=http%3A%2F%2Ftvax3.sinaimg.cn%2Fcrop.0.0.664.664.50%2F005Q8teCly8fh0tpj3718j30ig0igq40.jpg&wb_time=2017-07-21 15:55:29&wb_comment_id=4131977760631219&area=上海";
//        String regex = "http\\S+\\.jpg";
//        Matcher m = Pattern.compile(regex).matcher(s);
//        if(m.find())
//            System.out.println(URLDecoder.decode(m.group(0)));
//        String s = "2017-07-21 10:13:57";
//        try {
//            System.out.println(new SimpleDateFormat ("yyyy-MM-dd HH:mm", Locale.CHINA).parse(s).getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String s ="http://news.sina.com.cn/s/wh/2017-07-19/doc-ifyihmmm7541366.shtml";
//        System.out.println(lastSplitSlice(s,"-").replace(".shtml",""));
//        String s ="http://comment.news.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/%s/comments/newList?offset=50&limit=40&headLimit=3&tailLimit=1&ibc=newswap";
//        Matcher m = Pattern.compile("offset=(\\d+)").matcher(s);
//        if(m.find())
//            System.out.println(m.group(1));
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
//        String s = "<a href='/item/%E4%B8%AD%E5%9B%BD/1122445' data-lemmaid='1122445'>中国</a>";
//        System.out.println(s.replaceAll(regex,"#"));
//        String s = "wpnfsfbn dsgnbierg\rdfg\rs";
//        System.out.println(s.trim());
//        Map<String,String> m = new HashMap<>();
//        m.put("aaas","sfs");
//        m.forEach((k,v)->System.out.print(k + " ," + v));
    }
}
