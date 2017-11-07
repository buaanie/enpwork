package com.crawler.sites;

import com.crawler.processor.TencentInfo;
import com.crawler.processor.TencentList;
import com.crawler.utils.ItemPipeLine;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.handler.CompositePageProcessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.crawler.utils.StirngUtil.*;


/**
 * Created by ACT-NJ on 2017/7/10.
 * http://news.qq.com/articleList/rolls/ 从腾讯新闻滚动页面解析列表页面api，注意带上host和referer
 * http://news.qq.com/a/20170717/013341.htm 腾讯新闻正文页面示例
 */
public class TencentNews {
    private static final String tct_news = "http://roll.news.qq.com/interface/cpcroll.php?site=news&mode=1&cata=&date=%s&page=%s&_=%s";
    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Site site = Site.me().setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .setUserAgent(UA2);

    public static void main(String[] args) {
        new TencentNews().start();
    }
    public void start(){
        CompositePageProcessor tenProcessor = new CompositePageProcessor(site);
        tenProcessor.setSubPageProcessors(new TencentInfo(),new TencentList());

        String ms = String.valueOf(System.currentTimeMillis());
        String date = sdf.format(new Date());
        Request r1 = new Request(String.format(tct_news,date,"1",ms))
                .addHeader("Host","roll.news.qq.com").addHeader("Referer","http://news.qq.com/articleList/rolls/");
        Request r2 = new Request(String.format(tct_news,date,"2",ms))
                .addHeader("Host","roll.news.qq.com").addHeader("Referer","http://news.qq.com/articleList/rolls/");
        Spider tencent = Spider.create(tenProcessor).addRequest(r1,r2).addPipeline(new ItemPipeLine(ItemType.NewsItem)).thread(4);
        tencent.start();
    }
}
