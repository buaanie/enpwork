package com.crawler.sites;

import com.crawler.processor.NeteaseInfo;
import com.crawler.processor.NeteaseList;
import com.crawler.utils.ItemPipeLine;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.handler.CompositePageProcessor;

import java.util.ArrayList;
import java.util.List;

import static com.crawler.utils.StirngUtil.UA1;

/**
 * Created by ACT-NJ on 2017/7/16.
 * http://news.163.com/latest/ 解析pc版列表api，无军事数据，e.g. :http://news.163.com/special/0001220O/news_json.js
 * http://3g.163.com/touch/news/subchannel/all?dataversion=A&version=v_standard 解析手机版网易新闻列表页api，但是无法统一有区分的返回数据，军事频道在单独页面
 * 电脑版军事频道 http://temp.163.com/special/00804KVA/cm_war_(02|03).js
 * 手机版军事频道 http://3g.163.com/touch/reconstruct/article/list/BAI67OGGwangning/0-10.html
 */
public class NeteaseNews{
    private static String[] list = {"BCR1UC1Qwangning-社会","BD29LPUBwangning-国内","BD29MJTVwangning-国际","BAI67OGGwangning-军事"};
    private static String url = "http://3g.163.com/touch/reconstruct/article/list/%s/%d-40.html";
    private static Site site = Site.me().setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .setUserAgent(UA1);
    public static void main(String[] args) {
        CompositePageProcessor ntesProcessor = new CompositePageProcessor(site);
        ntesProcessor.setSubPageProcessors(new NeteaseInfo(),new NeteaseList());
        Spider netease = Spider.create(ntesProcessor).addRequest(getStartUrls()).addPipeline(new ItemPipeLine(ItemType.NewsItem));
        netease.run();
    }
    public static Request[] getStartUrls(){
        List<Request> res = new ArrayList<>();
        for (String s : list) {
            for(int i=0;i<40;i=i+40){
                Request request = new Request(String.format(url,s.split("-")[0],i));
                request.putExtra("type",s.split("-")[1]);
                request.putExtra("identity",s.split("-")[0]);
                res.add(request);
            }
        }
        return res.toArray(new Request[0]);
    }
}
