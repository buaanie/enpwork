package com.crawler.processor;

import com.crawler.beans.NewsItem;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

import static com.crawler.utils.StirngUtil.filtJournal;

/**
 * Created by ACT-NJ on 2017/7/13.
 */
public class TencentInfoProcessor implements SubPageProcessor{

    @Override
    public MatchOther processPage(Page page) {
        String url = page.getUrl().toString();
        String type  = page.getRequest().getExtra("column").toString();
        String time  = page.getRequest().getExtra("time").toString();
        String title  = page.getRequest().getExtra("title").toString();
        String description = title;
        String keywords = page.getHtml().xpath("//head/meta[@name='keywords']/@content").toString().split(",",2)[1];
        String source = page.getHtml().xpath("//*[@id='Main-Article-QQ']//div[@class='qq_article']//span[@class='a_source']/allText()").toString().trim();
        String id = "TCT"+url.replaceAll("[^\\d]","");
        List<Selectable> contents = page.getHtml().xpath("//*[@id='Cnt-Main-Article-QQ']/p[@class='text']").nodes();
        StringBuffer sb = new StringBuffer();
        Boolean fp = true;
        for (Selectable content : contents) {
            if(content.xpath("/p/@align").toString().equals("") && !content.xpath("/p/text()").toString().equals(""))
                if(fp) {
                    sb.append(filtJournal(content.xpath("/p/text()").toString()));
                    description = sb.toString();
                    fp = false;
                }else{
                    sb.append(content.xpath("/p/text()").toString());
                }
        }
        String content = sb.toString();
        NewsItem news = new NewsItem(id,url,title,content,time,source,type,description,keywords);
        System.out.println(content);
        String cmt_id = page.getHtml().xpath("//*[@id='Main-Article-QQ']/div/div[1]/div[2]/script[2]").regex("cmt_id = (\\d+);").toString();
        news.setComment("tct"+cmt_id);        //电脑 http://coral.qq.com/cmt_id  手机http://xw.qq.com/c/coral/cmt_id
        page.putField("news",news);
        return MatchOther.NO;
    }

    @Override
    public boolean match(Request request) {
        return request.getUrl().matches("http://news\\.qq\\.com/a/\\S+");
    }
}
