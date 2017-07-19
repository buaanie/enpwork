package com.crawler.processor;

import com.crawler.beans.NewsItem;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.RequestMatcher;
import us.codecraft.webmagic.handler.SubPageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.crawler.utils.StirngUtil.lastSplitSlice;

/**
 * Created by ACT-NJ on 2017/7/19.
 */
public class SinaInfo implements SubPageProcessor {
    private final DateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm", Locale.CHINA);
    @Override
    public MatchOther processPage(Page page) {
        String url = page.getUrl().toString();
        String type = page.getRequest().getExtra("type").toString();
        String title = page.getRequest().getExtra("title").toString();
        Long _time = Long.valueOf(page.getRequest().getExtra("time").toString());
        String time = sdf.format(new Date(_time));
        String keywords = page.getHtml().xpath("//head/meta[@name='keywords']/@content").toString();
        String source = page.getHtml().xpath("//head/meta[@name='mediaid']/@content").toString();
        String id = "SIN-i"+page.getHtml().xpath("//head/meta[@name='publishid']/@content").toString();
        List<Selectable> contents = page.getHtml().xpath("//*[@id='artibody']/p").nodes();
        StringBuffer sb = new StringBuffer();
        for (Selectable content : contents) {
            if(content.xpath("/p/@class").toString().equals("")) {
                sb.append(content.xpath("/p/text()").toString());
            }
        }
        String content = sb.toString();
        if(content.matches("\\s+"))
            return MatchOther.NO;
        NewsItem news = new NewsItem(id,url,title,content,time,source,type,title,keywords);
        //电脑 http://comment5.news.sina.com.cn/comment/skin/default.html?channel= gj &newsid=comos-id
        //手机 http://cmnt.sina.cn/index?product=comos&index=cmt_id&tj_ch=news
        news.setComment("sin-"+id);
        page.putField(ItemType.NewsItem,news);
        return MatchOther.NO;
    }

    @Override
    public boolean match(Request request) {
        return request.getUrl().matches("http://(mil\\.)?news\\.sina\\.com\\.cn/\\S+");
    }
}
