package com.crawler.utils;

import com.crawler.beans.CmtUser;
import com.crawler.beans.NewsCmt;
import com.crawler.beans.NewsItem;
import com.crawler.beans.WikiItem;
import com.store.CrawlerHBase;
import com.store.CrawlerIndex;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class ItemPipeLine implements Pipeline{
    private CrawlerIndex crawlerNewsIndex;
    private CrawlerHBase crawlerNewsHBase;
    private  String pageType = null;
    public ItemPipeLine(String type){
        this.pageType = type;
        crawlerNewsIndex = CrawlerIndex.getIndex();
//        if(type == ItemType.NewsItem)
//            crawlerNewsHBase = CrawlerHBase.getHBase(1);
//        else if(type == ItemType.NewsCmt)
//            crawlerNewsHBase = CrawlerHBase.getHBase(2);
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
        HashMap<String,String> s = new HashMap<>();
        s.put("","");
        switch (pageType){
            case ItemType.NewsItem:
                if(resultItems.get(ItemType.NewsItem)!=null)
                {
                    NewsItem news = (NewsItem) resultItems.get(ItemType.NewsItem);
                    System.out.println(news.toStringJson());
                };break;
            case ItemType.NewsCmt:
                if(resultItems.get(ItemType.NewsCmt)!=null)
                {
                    ArrayList comments = (ArrayList<NewsCmt>) resultItems.get(ItemType.NewsCmt);
                    ArrayList users = (ArrayList<CmtUser>) resultItems.get(ItemType.CmtUser);
                };break;
            case ItemType.WikiItem:
                if(resultItems.get(ItemType.WikiItem)!=null && resultItems.get(ItemType.WikiItem) instanceof WikiItem)
                {
                    WikiItem wiki = (WikiItem) resultItems.get(ItemType.WikiItem);
                    System.out.println(wiki.toString());
                };break;
            default:
                System.out.println("sorry");;break;
        }
    }

}
