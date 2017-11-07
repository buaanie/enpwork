package com.crawler.sites;

import com.crawler.processor.SinaInfo;
import com.crawler.processor.SinaList;
import com.crawler.utils.ItemPipeLine;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.handler.CompositePageProcessor;

import static com.crawler.utils.StirngUtil.UA1;

/**
 * Created by ACT-NJ on 2017/7/19.
 * http://news.sina.com.cn/society/ 单独分类页面查看api：http://api.roll.news.sina.com.cn/zt_list?channel=news&cat_1=shxw&show_all=1&show_num=20&tag=1&format=json&page=1
 * http://roll.news.sina.com.cn/s/channel.php?ch=01#col=90,91,92,93&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=60&asc=&page=1 聚合滚动页面
 * http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=90,91,92,93&offset_page=0&offset_num=0&num=60&page=1&last_time=1500255424
 */
public class SinaNews {
    public static String url = "http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=90,91,92,93&num=100&page=%d&last_time=%s";
    private static Site site = Site.me().setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000).setUserAgent(UA1);
    public static void main(String[] args) {
        new SinaNews().start();
    }
    public void start(){
        CompositePageProcessor sinaProcessor = new CompositePageProcessor(site);
        sinaProcessor.setSubPageProcessors(new SinaInfo(),new SinaList());
        String now = String.valueOf(System.currentTimeMillis()/1000 - 13*60*60);
        String start = String.format(url,1,now);
        Request r = new Request(start).setCharset("GBK").putExtra("page",1).putExtra("time",now);
        Spider netease = Spider.create(sinaProcessor).addRequest(r).addPipeline(new ItemPipeLine(ItemType.NewsItem)).thread(4);
        netease.start();
    }
}
