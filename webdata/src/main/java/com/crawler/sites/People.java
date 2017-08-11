package com.crawler.sites;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crawler.beans.NewsItem;
import com.crawler.utils.ItemPipeLine;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.crawler.utils.StirngUtil.*;

/**
 * 人民网
 * Created by ACT-NJ on 2017/6/20.
 */
public class People implements PageProcessor{
    private static final String url = "http://news.people.com.cn/210801/211150/index.js?_=%s";
    private final String IGNORE_PAGE = "http://(health|media|game|tv|pic)\\.people\\.com\\.cn/n1/\\d{4}/\\d{4}/\\S+";
    private Site site = Site.me().setDomain("http://news.people.com.cn/").setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .setUserAgent(UA2).setUseGzip(true);

    public static void main(String[] args) {
        new People().run();
    }
    public void run(){
        Date today = new Date();
        String  startURL = String.format(url,today.getTime());
        Request r = new Request(startURL).setCharset("UTF-8");
        Spider people = Spider.create(new People()).addRequest(r).addPipeline(new ItemPipeLine(ItemType.NewsItem)).thread(3);//.setScheduler(new PriorityScheduler())
        people.start();
    }
    @Override
    public void process(Page page) {
        String _url = page.getUrl().toString();
        if(_url.contains("n1")){
            String news_id = page.getHtml().xpath("/html/head/meta[@name='contentid']/@content").toString();
            String news_title = page.getRequest().getExtra("title").toString();;
            String news_time = page.getRequest().getExtra("time").toString();
            String news_source = filtChinese(page.getHtml().xpath("/html/head/meta[@name='source']/@content").toString());
            String news_type = page.getHtml().xpath("/html/head/title/text()").toString().split("--")[1];
            List<Selectable> news_contents = page.getHtml().xpath("//*[@id='rwb_zw']/p").nodes();
            StringBuffer sb = new StringBuffer();
            Boolean fp = true;
            for (Selectable content : news_contents) {
                if(!content.xpath("/p/allText()").toString().equals(""))
                    if(fp) {
                        sb.append(filtJournal(content.xpath("/p/allText()").toString()));
                        fp = false;
                    }else{
                        sb.append(content.xpath("/p/allText()").toString());
                    }
            }
            String news_content = sb.toString();
            String news_descp = page.getHtml().xpath("/html/head/meta[@name='description']/@content").toString();
            String news_keywords = page.getHtml().xpath("/html/head/meta[@name='keywords']/@content").toString();
            NewsItem news = new NewsItem("pep-"+news_id,_url,tidyHTMLText(news_title),news_content.trim(),news_time,news_source,news_type,news_descp,news_keywords);
            page.putField(ItemType.NewsItem,news);
        }else if(_url.contains("index")){
            JSONArray res = JSONObject.parseObject(page.getRawText()).getJSONArray("items");
            for(int i =0;i<900;i++){
                String u = res.getJSONObject(i).getString("url");
                String t = res.getJSONObject(i).getString("title");
                String tm = res.getJSONObject(i).getString("date");
                if(!u.matches(IGNORE_PAGE))
                    page.addTargetRequest(new Request(u).putExtra("time",tm).putExtra("title",t));
            }
            page.setSkip(true);
        }else{
            page.setSkip(true);
            return;
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
