package com.store;

import com.crawler.beans.NewsItem;
import com.event.EventInfo;
import com.event.RelatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by ACT-NJ on 2017/7/23.
 */
public class DataAssembler {
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");//'z
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
    根据es 返回的map结果，恢复成NewsItem
     */
    public NewsItem bindNews(Map<String, Object> map) {
        NewsItem news = new NewsItem();
        if (map.containsKey("id")) {
            news.setId(map.get("id").toString());
        }
        if (map.containsKey("time")) {
            String time = map.get("time").toString();
//            try {
//                Date date = dateFormatter.parse(map.get("time").toString());
//                time = sdf.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            news.setStringTime(time);
        }
        if (map.containsKey("title")) {
            news.setTitle(map.get("title").toString());
        }
        if (map.containsKey("content")) {
            news.setContent(map.get("content").toString());
        }
        if (map.containsKey("url")) {
            news.setUrl(map.get("url").toString());
        }
        if (map.containsKey("source")) {
            try {
                news.setSource(map.get("source").toString());
            } catch (Exception e) {
                news.setSource("");
                e.printStackTrace();
            }
        }
        if (map.containsKey("type")) {
            news.setType(map.get("type").toString());
        }
        if (map.containsKey("keywords")) {
            news.setKeyWords(map.get("keywords").toString());
        }
        return news;
    }

    public RelatedEvent bindRelatedEvent(String id, Map<String, Object> map){
        RelatedEvent event = new RelatedEvent(id);
        String corewords = map.get("corewords").toString();
        event.setCorewords(corewords);
        Date date = null;
        if (map.containsKey("time")) {
            try {
                date = dateFormatter.parse(map.get("time").toString());
                event.setTime(date);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        if (map.containsKey("corewords")) {
            String core = map.get("corewords").toString();
            String coreword = core==null?"":core;
            event.setCorewords(coreword);
        }
        if (map.containsKey("detailwords")) {
            String detail = map.get("detailwords").toString();
            String detailword = detail==null?"":detail;
            event.setDetailwords(detailword);
        }
        if (map.containsKey("weiboids")) {
            String[] weibo = map.get("weiboids").toString().split(",");
            event.setWeiboID(weibo);
        }
        if(map.containsKey("e_type")){
            event.setType(Integer.valueOf(map.get("e_type").toString()));
        }
        if (map.containsKey("desc")) {
            event.setDesc(map.get("desc").toString());
        }
        if (map.containsKey("location")) {
            event.setLoca(map.get("location").toString());
        }
        if (map.containsKey("participant")) {
            event.setPart(map.get("participant").toString());
        }
        return event;
    }

    public EventInfo bindEvent(String id, Map<String, Object> map){
        EventInfo event = new EventInfo(id);
        String corewords = map.get("corewords").toString();
        if (map.containsKey("time")) {
            try {
                Date date = dateFormatter.parse(map.get("time").toString());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        return event;
    }
}
