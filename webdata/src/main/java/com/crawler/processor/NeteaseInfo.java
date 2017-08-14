package com.crawler.processor;

import com.crawler.beans.NewsItem;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

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
        List<Selectable> contents = page.getHtml().xpath("//*[@id='endText']/p").nodes();
        StringBuffer sb = new StringBuffer();
        for (Selectable content : contents) {
            if(content.xpath("/p/@class").toString().equals("")) {
                sb.append(content.xpath("/p/text()").toString());
            }
        }
        String content = sb.toString();
        if(content.matches("\\s+")) {
            page.setSkip(true);
            return MatchOther.NO;
        }
        String url = page.getUrl().toString();
        String source  = page.getRequest().getExtra("source").toString();
        String time  = page.getRequest().getExtra("time").toString();
        String type =  page.getRequest().getExtra("type").toString();
        String keywords = page.getHtml().xpath("//head/meta[@name='keywords']/@content").toString();
        if(keywords==null){
            page.setSkip(true);
            return MatchOther.NO;
        }
        NewsItem news = new NewsItem("nts-"+id,url,title,content,time,source,type,title,keywords);
        //电脑 http://comment.news.163.com/news2_bbs/cmt_id.html
        //手机 http://3g.163.com/touch/comment.html?docid=cmt_id
        news.setCmtID("nts-"+id);
        page.putField(ItemType.NewsItem,news);
        return MatchOther.NO;
    }

    @Override
    public boolean match(Request request) {
        return request.getUrl().matches("http://news\\.163\\.com/\\S+");
    }
}
