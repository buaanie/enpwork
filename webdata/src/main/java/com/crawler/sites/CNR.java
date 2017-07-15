package com.crawler.sites;

import com.crawler.utils.ItemPipeLine;
import com.crawler.beans.NewsItem;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

import static com.crawler.utils.StirngUtil.*;


/**
 * 央广网
 * Created by ACT-NJ on 2017/6/12.
 */
public class CNR implements PageProcessor{
    public static  final String LIST_REGEX = "http://kuaixun\\.cnr\\.cn/\\?n=\\d+"; //新闻列表页面 index\.html
    public static  final String NEWS_REGEX = "http://www\\.cnr\\.cn/.*\\.shtml"; //新闻列表页面
    private Site site = Site.me().setDomain("http://www.cnr.cn/").setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .setUserAgent(UA2).setUseGzip(true).setUseGzip(true);

    public static void main(String[] args) {
        List<String> start = new ArrayList<>();
//        start.add("http://www.cnr.cn/newscenter/native/city/20170713/t20170713_523848598.shtml");
//        for(int i =1;i<10;i++)
//            start.add("http://kuaixun.cnr.cn/?n="+i);
        Spider cnr = Spider.create(new CNR()).startUrls(start).setScheduler(new PriorityScheduler()).addPipeline(new ItemPipeLine(ItemType.NewsItem)).thread(3);
        cnr.run();
    }
    @Override
    public void process(Page page) {
        if(page.getUrl().regex(NEWS_REGEX).match()){
            String news_url = page.getUrl().toString();
            String news_id = "CNR"+lastSplitSlice(news_url,"/").split("\\.")[0];
            List<Selectable> news_contents = page.getHtml().xpath("//div[@class='wrapper']//div[@class='article']//div[@class='TRS_Editor']//p").nodes();
            StringBuffer sb = new StringBuffer();
            Boolean fp = true;
            for (Selectable content : news_contents) {
                if(content.xpath("/p/@align").toString().equals("") && !content.xpath("/p/allText()").toString().equals(""))
                    if(fp) {
                        sb.append(filtJournal(content.xpath("/p/allText()").toString()));
                        fp = false;
                    }else{
                        sb.append(content.xpath("/p/allText()").toString());
                    }
            }
            String news_content = sb.toString();
            String news_title = page.getHtml().xpath("//div[@class='wrapper']//div[@class='article']/div[@class='subject']/h2/text()").toString();
            String news_time = page.getHtml().xpath("//div[@class='wrapper']//div[@class='article']//div[@class='source']/span[1]/text()").regex(TIME_REGEX).toString();
            String news_source = page.getHtml().xpath("//head/[@name='source']/@content").toString();
            String news_type = page.getRequest().getExtra("type").toString();
            String news_descp = page.getHtml().xpath("//head/meta[@name='description']/@content").toString();
            String news_keywords = page.getHtml().xpath("//head/meta[@name='keywords']/@content").toString();
            NewsItem news = new NewsItem(news_id,news_url,news_title.trim(),news_content.trim(),news_time,news_source,news_type,news_descp,news_keywords);
            page.putField(ItemType.NewsItem,news);
        }else if(page.getUrl().regex(LIST_REGEX).match()){
            List<Selectable> nodes = page.getHtml().xpath("//div[@class='margin']//div[@class='left']/trs_documents/ul/li").nodes();
            for (Selectable node : nodes) {
                String type = node.xpath("/li/span[1]/text()").toString();
                if(!type.contains("旅游") && !type.contains("体育")){
                    Request r = new Request(node.xpath("/li/span[2]/a/@href").toString());
                    r.putExtra("type",node.xpath("/li/span[1]/text()").toString());
                    page.addTargetRequest(r);
                }

            }
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
    
}