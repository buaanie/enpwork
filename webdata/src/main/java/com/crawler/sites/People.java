package com.crawler.sites;

import com.crawler.beans.NewsItem;
import com.crawler.utils.ItemPipeLine;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
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
    private final String IGNORE_PAGE = "http://(media|game|tv|pic)\\.people\\.com\\.cn/n1/\\d{4}/\\d{4}/\\S+";
    private Site site = Site.me().setDomain("http://news.people.com.cn/").setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .setUserAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").setCharset("GBK").setUseGzip(true);

    public static void main(String[] args) {
        Date today = new Date();
        String  startURL = String.format(url,today.getTime());
        Spider people = Spider.create(new People()).addUrl(startURL).setScheduler(new PriorityScheduler()).addPipeline(new ItemPipeLine(ItemType.NewsItem)).thread(3);
        people.run();
    }
    @Override
    public void process(Page page) {
        String _url = page.getUrl().toString();
        if(_url.contains("n1")){
            String news_id = "PEP"+page.getHtml().xpath("/html/head/meta[@name='contentid']/@content").toString();
            String news_title = page.getHtml().xpath("/html/head/title/text()").toString().split("--")[0];
            String news_time = getChineseTime(page.getHtml().xpath("/html/body//div[@class='box01']/div[@class='fl']/text()").regex(TIME_REGEX2).toString());
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
            NewsItem news = new NewsItem(news_id,_url,news_title.trim(),news_content.trim(),news_time,news_source,news_type,news_descp,news_keywords);
            page.putField(ItemType.NewsItem,news);
        }else if(_url.contains("index")){
            Json res = page.getJson();
            List<String> urls =res.jsonPath("$.items[*].url").all();
            List<String> pages = new ArrayList<>();
            urls.forEach(url -> {if(!url.matches(IGNORE_PAGE)) pages.add(url);});
            page.addTargetRequests(pages);
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
