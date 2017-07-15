package com.crawler.sites;

import com.crawler.utils.ItemPipeLine;
import com.crawler.beans.WikiItem;
import com.crawler.utils.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import static com.crawler.utils.StirngUtil.*;


/**
 * Created by ACT-NJ on 2017/6/8.
 * 主要用于搜索爬取，python实现更快
 */
public class Baike implements PageProcessor{
    // search word --> https://wapbaike.baidu.com/search/word?word=北京
    // item word --> http://wapbaike.baidu.com/item/北京
    // view id --> http://baike.baidu.com/view/445.htm
    // id: data-newlemmaid
    public static final String Baike_URL  = "https://wapbaike.baidu.com/search/word?word=%s";
    private final String URL_Regex = "</?a[^>]*>";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static void main(String[] args) {
        Baike bk = new Baike();
        String[] words = new String[]{"朱自清"};
        bk.getWordCrawler(words);
    }

    /**
     * 输入待查关键字（如实体名），进行爬取
     * @param queryWords
     */
    public void getWordCrawler(String... queryWords){
        List<Request> urls = new ArrayList<>();
        for (String queryWord : queryWords) {
            Request r  = new Request(String.format(Baike_URL,URLEncoder.encode(queryWord))).setPriority(7);//putExtra("_level",0)
            urls.add(r);
        }
        Spider baike = Spider.create((new Baike())).startRequest(urls).setScheduler(new PriorityScheduler()).addPipeline(new ItemPipeLine(ItemType.WikiItem)).thread(3);
        baike.run();
    }

    @Override
    public void process(Page page) {
        //没有找到相关词条
        if(page.getHtml().xpath("//ul[@id='searchList']").nodes().size()>0){
//            logger.error(" ***--------------------------------> "+ URLDecoder.decode(page.getUrl().toString().split("=")[1]));
            return;
        }
        String lemmaid = page.getHtml().xpath("//div[@id='J-lemma']//div[@id='J-vars']/@data-newlemmaid").toString();
        String title = page.getHtml().xpath("//div[@class='BK-body-wrapper']//*[@class='lemma-title']/tidyText()").toString().replace("\n","");
        String summary = page.getHtml().xpath("//div[@class='BK-body-wrapper']//div[@class='summary-content']/allText()").get();
        int infoSize = page.getHtml().xpath("//*[@id='J-basicInfo']/ul//li").all().size();
        List<String> nextItems = page.getHtml().xpath("//*[@id='J-basicInfo']/ul").links().all();
        Map map = new HashMap<String, String>();
        for(int i =infoSize;i>0;i--){
            String infoTilte = page.getHtml().xpath("//*[@id='J-basicInfo']/ul/li["+i+"]/div[@class='info-title']/tidyText()").toString().trim();
            String infoContent = page.getHtml().xpath("//*[@id='J-basicInfo']/ul/li["+i+"]/div[@class='info-content']/html()").toString().replaceAll("\n","").replaceAll(URL_Regex,"#").trim();
            map.put(infoTilte, tidyHTMLText(infoContent));
        }
        if(nextItems.size()>0 && page.getRequest().getPriority()>4){
            page.addTargetRequests(nextItems,page.getRequest().getPriority()-1);
        }
        WikiItem item = new WikiItem(title,lemmaid,tidyHTMLText(summary),map);
        if(page.getUrl().toString().contains("=") && !title.equals(URLDecoder.decode(page.getUrl().toString().split("=")[1]))) {
            item.setSynonym(URLDecoder.decode(page.getUrl().toString().split("=")[1]));
        }
        List<String> relatedIDs = page.getHtml().xpath("//*[@id='J-basicInfo']/ul//a/@data-lemmaid").all();
        item.setRelative(idList2Set(relatedIDs));
        int n = 0;
        if(( n= page.getHtml().xpath("//*[@id='J-polysemant-content']/ul/li").all().size())>0) {
            List<String> list = new ArrayList<>();
            list.add(page.getHtml().xpath("//*[@id='J-polysemant-content']/ul/li[0]/a/text()").toString()+'-'+lemmaid);
            for(int i =1;i<n;i++) {
                String temp = page.getHtml().xpath("//*[@id='J-polysemant-content']/ul/li["+i+"]/a/text()").toString()+'-'+
                        lastSplitSlice(page.getHtml().xpath("//*[@id='J-polysemant-content']/ul/li["+i+"]/a/@href").toString(),"/");
                list.add(temp);
            }
            item.setPolysemant(list);
        }
        page.putField(ItemType.WikiItem,item);

    }

    @Override
    public Site getSite() {
        return site;
    }
    private Site site = Site.me().setDomain("https://wapbaike.baidu.com/").setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .setUserAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
}
