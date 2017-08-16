package com.crawler.processor;

import com.crawler.beans.NewsItem;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

import static com.crawler.utils.StirngUtil.filtJournal;

/**
 * Created by ACT-NJ on 2017/7/13.
 */
public class TencentInfo implements SubPageProcessor{

    @Override
    public MatchOther processPage(Page page) {
        String url = page.getUrl().toString();
        String type  = page.getRequest().getExtra("column").toString();
        String time  = page.getRequest().getExtra("time").toString();
        String title  = page.getRequest().getExtra("title").toString();
        String keywords = page.getHtml().xpath("//head/meta[@name='keywords']/@content").toString();
        if(keywords==null || keywords.equals("")){
            page.setSkip(true);
            return MatchOther.NO;
        }
        keywords = keywords.split(",",2)[1];
        String source = page.getHtml().xpath("//*[@id='Main-Article-QQ']//div[@class='qq_article']//span[@class='a_source']/allText()").toString();
        if(source==null || source.matches("\\s+"))
            source = page.getHtml().xpath("//*[@id='C-Main-Article-QQ']//div[@class='tit-bar']//span[@bosszone='jgname']/text()").toString();;
        if(source==null)
            source = "腾讯新闻";
        String id = url.replaceAll("[^\\d]","");
        List<Selectable> contents = page.getHtml().xpath("//*[@id='Cnt-Main-Article-QQ']/p[@class='text']").nodes();
        StringBuffer sb = new StringBuffer();
        for (Selectable content : contents) {
            if(content.xpath("/p/@align").toString().equals("") && !content.xpath("/p/text()").toString().equals(""))
                sb.append(content.xpath("/p/text()").toString());
        }
        String content = sb.toString();
        if(content.matches("\\s+")) {
            page.setSkip(true);
            return MatchOther.NO;
        }
        NewsItem news = new NewsItem("tct-"+id,url,title,content,time,source.trim(),type,title,keywords);
        String cmt_id = page.getHtml().xpath("//*[@id='Main-Article-QQ']//div[@class='qq_articleFt']/script[2]").regex("cmt_id = (\\d+);").toString();
        if(cmt_id==null || cmt_id.equals(""))
            cmt_id = page.getHtml().xpath("//*[@id='Main-Article-QQ']//div[@class='qq_articleFt']/script[1]").regex("cmt_id = (\\d+);").toString();
        // 电脑 http://coral.qq.com/cmt_id
        // 手机http://xw.qq.com/c/coral/cmt_id
        news.setCmtID("tct"+cmt_id);
        page.putField(ItemType.NewsItem,news);
        return MatchOther.NO;
    }

    @Override
    public boolean match(Request request) {
        return request.getUrl().matches("http://news\\.qq\\.com/a/\\S+");
    }
}
