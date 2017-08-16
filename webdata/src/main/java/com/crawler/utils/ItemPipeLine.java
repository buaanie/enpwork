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
import java.util.List;

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
        if(type==ItemType.NewsCmt)
            crawlerNewsHBase = CrawlerHBase.getHBase(true);
        else
            crawlerNewsHBase = CrawlerHBase.getHBase(false);
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
        switch (pageType){
            case ItemType.NewsItem:
                if(resultItems.get(ItemType.NewsItem)!=null)
                {
                    NewsItem news = resultItems.get(ItemType.NewsItem);
//                    NewsItem news = (NewsItem) resultItems.get(ItemType.NewsItem);
//                    Thread index = new Thread(){
//                        public void run(){
                            crawlerNewsIndex.indexNews(news);
//                        }
//                    };
//                    Thread store = new Thread(){
//                        public void run(){
                            crawlerNewsHBase.storeNews(news);
//                        }
//                    };
//                    index.start();
//                    store.start();
                    System.out.println(news.toString());
                };break;
            case ItemType.NewsCmt:
                if(resultItems.get(ItemType.NewsCmt)!=null)
                {
                    List<NewsCmt> comments = resultItems.get(ItemType.NewsCmt);
                    List<CmtUser> users = resultItems.get(ItemType.CmtUser);
                    crawlerNewsHBase.storeBulkCmt(comments,users);
                };break;
            case ItemType.WikiItem:
                if(resultItems.get(ItemType.WikiItem)!=null && resultItems.get(ItemType.WikiItem) instanceof WikiItem)
                {
                    WikiItem wiki = (WikiItem) resultItems.get(ItemType.WikiItem);
                    System.out.println(wiki.toString());
                };break;
            default:
                System.out.println("sorry, it failed");break;
        }
    }

}
