package com.crawler.sites;

import com.crawler.processor.TencentInfo;
import com.crawler.utils.ItemPipeLine;
import com.crawler.processor.TencentList;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.handler.RequestMatcher;
import us.codecraft.webmagic.handler.SubPageProcessor;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.crawler.utils.StirngUtil.*;


/**
 * Created by ACT-NJ on 2017/7/10.
 * http://news.qq.com/articleList/rolls/ 从腾讯新闻滚动页面解析列表页面api，注意带上host和referer
 * http://news.qq.com/a/20170717/013341.htm 腾讯新闻正文页面示例
 */
public class TencentNews implements PageProcessor{
    private static final String tct_news = "http://roll.news.qq.com/interface/cpcroll.php?site=news&mode=1&cata=&date=%s&page=%s&_=%s";
    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static List<SubPageProcessor> subPageProcessors = new ArrayList<SubPageProcessor>();
    private Site site = Site.me().setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .addHeader("Host","roll.news.qq.com").addHeader("Referer","http://news.qq.com/articleList/rolls/")
            .setUserAgent(UA1);

    public static void main(String[] args) {
        new TencentNews().run();
    }
    public void run(){
        String ms = String.valueOf(System.currentTimeMillis());
        String date = sdf.format(new Date());
        String url1 = String.format(tct_news,date,"1",ms);
        subPageProcessors.add(new TencentInfo());
        subPageProcessors.add(new TencentList());
        Spider tencent = Spider.create(new TencentNews()).addUrl(url1).thread(4).addPipeline(new ItemPipeLine(ItemType.NewsItem));
        tencent.start();
    }
    @Override
    public void process(Page page) {
        for (SubPageProcessor subPageProcessor : subPageProcessors) {
            if(subPageProcessor.match(page.getRequest())){
                RequestMatcher.MatchOther matchOther = subPageProcessor.processPage(page);
                if(matchOther==null|| matchOther == RequestMatcher.MatchOther.NO)
                    return;
            }
        }
    }
    @Override
    public Site getSite() {
        return site;
    }
}
