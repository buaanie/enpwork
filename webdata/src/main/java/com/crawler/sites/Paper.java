package com.crawler.sites;

import com.crawler.beans.NewsItem;
import com.crawler.utils.ItemPipeLine;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

import static com.crawler.utils.StirngUtil.UA1;
import static com.crawler.utils.StirngUtil.lastSplitSlice;

/**
 * Created by ACT-NJ on 2017/7/24.
 */
public class Paper implements PageProcessor{
    private static String list = "http://m.thepaper.cn/load_channel.jsp?nodeids=25428,25426,25481,25429,25462,25490,25488,25434&topCids=1739457,1741122,1741013&pageidx=%d&lastTime=%s";
    private static String url_head = "http://m.thepaper.cn/";
    private String time_regex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}";
    private Site site = Site.me().setDomain("http://m.thepaper.cn/").setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000).setDisableCookieManagement(true)
            .setUserAgent(UA1).setUseGzip(true);

    public static void main(String[] args) {
        new Paper().start();
    }
    public void start(){
        long time = System.currentTimeMillis();
        List<String> starts = new ArrayList<>();
        for(int i=2;i<10;i++){
            starts.add(String.format(list,i,time));
        }
        //建议page 1~8
        Spider paper = Spider.create(new Paper()).startUrls(starts).addPipeline(new ItemPipeLine(ItemType.NewsItem)).thread(3);
        paper.run();
    }
    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        if(url.contains("newsDetail")){
            String content = page.getHtml().xpath("//*[@id='v3cont_id']//div[@class='news_part']/allText()").toString();
            if(content==null || content.matches("\\s+")){
                page.setSkip(true);
                return;
            }
            String title = page.getRequest().getExtra("title").toString();
            String type = page.getRequest().getExtra("type").toString();
            String descp = page.getHtml().xpath("/html/head/meta[@name='Description']/text()").toString();
            if(descp==null ||descp.equals(""))
                descp=title;
            String keywords = page.getHtml().xpath("/html/head/meta[@name='Keywords']/text()").toString();
            String id = lastSplitSlice(url,"_");
            String source = page.getHtml().xpath("//*[@id='v3cont_id']/div[1]/p[1]/text()").toString();
            if(source==null)
                source = "澎湃新闻";
            else if(source.contains("/")){
                source = source.split("/")[1];
            }
            String time = page.getHtml().xpath("//*[@id='v3cont_id']/div[1]/p[2]/text()").regex(time_regex).toString();
            NewsItem news = new NewsItem("pap-"+id,url,title,content,time,source.trim(),type,descp,keywords);
            page.putField(ItemType.NewsItem,news);
        }else{
            List<String> urls = page.getHtml().xpath("//div[@class='txt_t']/div/p/a/@href").all();
            List<String> titles = page.getHtml().xpath("//div[@class='txt_t']/div/p/a/text()").all();
            List<String> sources = page.getHtml().xpath("//div[@class='txt_t']/p/a/text()").all();
            for(int i=0;i<urls.size();i++){
                Request req = new Request(url_head+urls.get(i))
                        .putExtra("title",titles.get(i)).putExtra("type",sources.get(i));
                page.addTargetRequest(req);
            }
            page.setSkip(true);
        }

    }

    @Override
    public Site getSite() {
        return site;
    }
}
