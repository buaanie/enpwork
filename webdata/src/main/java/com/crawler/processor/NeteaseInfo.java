package com.crawler.processor;

import com.crawler.beans.NewsItem;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

import static com.crawler.utils.StirngUtil.*;

/**
 * Created by ACT-NJ on 2017/7/17.
 */
public class NeteaseInfo implements SubPageProcessor{
    @Override
    public MatchOther processPage(Page page) {
//        存在翻页？
//        if(page.getHtml().xpath("//div[@class='ep-content']//div[@class='ep-pages']/a[1]/text()").toString()!=null){
//            String[] urlSplit = page.getUrl().toString().split("\\.");
//            urlSplit[urlSplit.length-2] = urlSplit[urlSplit.length-2]+"_all";
//            String wholePageUrl ="";
//            for(int i=0;i<urlSplit.length-1;i++){
//                wholePageUrl = wholePageUrl + urlSplit[i] +".";
//            }
//            wholePageUrl = wholePageUrl + urlSplit[urlSplit.length-1];
//            page.addTargetRequest(wholePageUrl);
//            page.setSkip(true);
//            return  MatchOther.NO;
//        }
        String id = page.getRequest().getExtra("id").toString();
        String title  = page.getRequest().getExtra("title").toString();
        String description = title;
        List<Selectable> contents = page.getHtml().xpath("//*[@id='endText']/p").nodes();
        StringBuffer sb = new StringBuffer();
        for (Selectable content : contents) {
            if(content.xpath("/p/@class").toString().equals("")) {
                sb.append(content.xpath("/p/text()").toString());
            }
        }
        String content = sb.toString();
        if(content.matches("\\s+"))
            return MatchOther.NO;
        String url = page.getUrl().toString();
        String source  = page.getRequest().getExtra("source").toString();
        String time  = page.getRequest().getExtra("time").toString();
        String type =  page.getRequest().getExtra("type").toString();
        String keywords = page.getHtml().xpath("//head/meta[@name='keywords']/@content").toString().split(",",2)[1];
        NewsItem news = new NewsItem("NTS-"+id,url,title,content,time,source,type,description,keywords);
        String cmt_id = page.getHtml().xpath("//*[@id='Main-Article-QQ']/div/div[1]/div[2]/script[2]").regex("cmt_id = (\\d+);").toString();
        news.setComment("nts-"+cmt_id);        //电脑 http://coral.qq.com/cmt_id  手机http://xw.qq.com/c/coral/cmt_id
        page.putField(ItemType.NewsItem,news);
        return MatchOther.NO;
    }

    @Override
    public boolean match(Request request) {
        return request.getUrl().matches("http://news\\.163\\.com/\\S+");
    }
}
