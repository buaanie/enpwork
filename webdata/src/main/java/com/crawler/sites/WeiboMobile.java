package com.crawler.sites;

import com.alibaba.fastjson.JSONObject;
import com.crawler.beans.Weibo;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.crawler.utils.StirngUtil.UA1;

/**
 * Created by ACT-NJ on 2017/7/25.
 */
public class WeiboMobile implements PageProcessor{
    private static final String urlRegx = "<[^>]+>";
    private static final String quotRegx = "\\&quot;";
    public static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String URL = "http://m.weibo.cn/page/json?containerid=100505%s_-_WEIBO_SECOND_PROFILE_WEIBO&page=%d";
    //	private static String cookie = GetCookie.SinaLogin();

    public static void main(String[] args) {
        List<String> startURL = new ArrayList<>();
        for(String s : args){
                String t = String.format(URL,s,1);
                startURL.add(t);
        }
        Spider wb = Spider.create(new WeiboMobile()).startUrls(startURL).thread(4);
        wb.run();
    }
    private Site site = Site.me().setDomain("http://m.weibo.cn/").setRetryTimes(3).setCycleRetryTimes(3000).setSleepTime(1000)
//			.addCookie(".weibo.cn", "SUHB", "0sqwzIO-6PssGH")
//			.addHeader("cookie", this.cookie)
//			.addCookie(".weibo.cn", "SSOLoginState", "1487298742")
//			.addCookie(".weibo.cn", "SUB", "_2A251ohDmDeRxGeRM61cR8CjEzT-IHXVW1gUurDV8PUNbmtANLXStkW-JxdgvgNnwm7MkL3nufAtRmVwofA..")
//			.addCookie(".weibo.cn","SUBP", "0033WrSXqPxfM725Ws9jqgMF55529P9D9W5Jn.yGQ4GdVPXA7aes0aQ75JpX5K2hUgL.FozEeh-7ehqRSoe2dJLoIEBLxK-LBKBLBK.LxK-LBKBLBKMLxKnL122LBo2LxKnL122LBo2t")
            .setUserAgent(UA1);

    @Override
    public Site getSite() {
        return site;
    }
    @Override
    public void process(Page page) {
        String pageText = page.getRawText();
        if(pageText.contains("通行证")||pageText.contains("拒绝")||pageText.contains("Forbidden")){
            System.out.println("need to login in");
            return;
        }
        List<String> weibo = new JsonPathSelector("$..card_group.mblog[*].mid").selectList(page.getRawText());
        for(int num = weibo.size()-1;num>=0;num--){
            String wbString = new JsonPathSelector("$..card_group.mblog["+num+"]").select(pageText);
            JSONObject mblog = JSONObject.parseObject(wbString);
            String mid = mblog.getOrDefault("id","-").toString();
            String timestamp = mblog.getOrDefault("created_timestamp","-").toString();
            String time = parseTime(timestamp);
            String uid = mblog.getJSONObject("user").getOrDefault("id","-").toString();
            String uname = mblog.getJSONObject("user").getOrDefault("screen_name","-").toString();
            String text = mblog.getOrDefault("text","-").toString().replaceAll(urlRegx, "").replaceAll(quotRegx, "");
            String source = mblog.getOrDefault("source","-").toString();
            String pmid = "-";
            String reposts = mblog.getOrDefault("reposts_count","-").toString();
            String comments = mblog.getOrDefault("comments_count","-").toString();
            String attitudes = mblog.getOrDefault("attitudes_count","-").toString();
            if(mblog.containsKey("retweeted_status")){
                JSONObject retweeted = mblog.getJSONObject("retweeted_status");
                pmid = retweeted.getOrDefault("id","-").toString();
                String ruid = retweeted.getJSONObject("user").getOrDefault("id","-").toString();
                String rtimestamp = retweeted.getOrDefault("created_timestamp","-").toString();
                String rtime = parseTime(rtimestamp);
                String runame = retweeted.getJSONObject("user").getOrDefault("screen_name","-").toString();
                String rtext = retweeted.getOrDefault("text","-").toString().replaceAll(urlRegx, "").replaceAll(quotRegx, "");
                String rsource = retweeted.getOrDefault("source","-").toString();
                String rreposts = retweeted.getOrDefault("reposts_count","-").toString();
                String rcomments = retweeted.getOrDefault("comments_count","-").toString();
                String rattitudes = retweeted.getOrDefault("attitudes_count","-").toString();
                Weibo rweibo = new Weibo(pmid,rtime,ruid,runame,rtext,rsource,"-",rreposts,rcomments,rattitudes);
            }
            Weibo oweibo = new Weibo(mid,time,uid,uname,text,source,pmid,reposts,comments,attitudes);

        }
    }
    private String parseTime(String created_timestamp) {
        long time = Long.parseLong(created_timestamp)*1000;
        Date dtime = new Date(time);
        String timeStr = sdf.format(dtime);
        return timeStr;
    }
}
