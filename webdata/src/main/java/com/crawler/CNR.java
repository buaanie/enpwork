package com.crawler;

import com.model.NewsSubject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

import static com.utils.StirngUtil.*;

/**
 * Created by ACT-NJ on 2017/6/12.
 */
public class CNR implements PageProcessor{
    public static  final String LIST_REGEX = "http://kuaixun\\.cnr\\.cn/index\\.html(\\?n=\\d+)?"; //新闻列表页面
    public static  final String NEWS_REGEX = "http://www.cnr.cn/.*\\.shtml"; //新闻列表页面
    private Site site = Site.me().setDomain("http://kuaixun.cnr.cn/").setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .setUserAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");

    @Override
    public void process(Page page) {
        if(page.getUrl().regex(NEWS_REGEX).match()){
            String news_url = page.getUrl().toString();
            String news_id = lastSplitSlice(news_url,"/").split("\\.")[0];
            String news_title = page.getHtml().xpath("//div[@class='wrapper']//div[@class='article']/div[@class='subject']/h2/text()").toString();
            String news_time = page.getHtml().xpath("//div[@class='wrapper']//div[@class='article']//div[@class='source']/span[1]/text()").regex(TIME_REGEX).toString();
            String news_source = page.getHtml().xpath("//head/[@name='source']/@content").toString();
            String news_type = page.getHtml().xpath("//head/meta[@name='WT.cg_n']/@content").toString();
            String news_content = page.getHtml().xpath("//div[@class='wrapper']//div[@class='article']//div[@class='TRS_Editor']/div/allText()").toString();
            String news_descp = page.getHtml().xpath("//head/meta[@name='description']/@content").toString();
            String news_keywords = page.getHtml().xpath("//head/meta[@name='keywords']/@content").toString();
            NewsSubject news = new NewsSubject(news_id,news_url,news_title,news_content,news_time,news_source,news_type,news_descp);
            news.setKeyWords(news_keywords);
        }else if(page.getUrl().regex(LIST_REGEX).match()){
            List<String> urls = page.getHtml().xpath("//div[@class='left']/trs_documents/ul").links().all();
            page.addTargetRequests(urls);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
    
}
