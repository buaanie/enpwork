package com.crawler.utils;

import com.crawler.beans.CmtUser;
import com.crawler.beans.NewsCmt;
import com.crawler.beans.NewsItem;
import com.crawler.beans.WikiItem;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class ItemPipeLine implements Pipeline{
    private  String pageType = null;
    public ItemPipeLine(String type){
        this.pageType = type;
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
        switch (pageType){
            case ItemType.NewsItem:
                System.out.println("888888");
                if(resultItems.get(ItemType.NewsItem)!=null)
                {
                    NewsItem news = (NewsItem) resultItems.get(ItemType.NewsItem);
                    System.out.println(news.toStringJson());
                };break;
            case ItemType.WikiItem:
                if(resultItems.get(ItemType.WikiItem)!=null && resultItems.get(ItemType.WikiItem) instanceof WikiItem)
                {
                    WikiItem wiki = (WikiItem) resultItems.get(ItemType.WikiItem);
                    System.out.println(wiki.toString());
                };break;
            case ItemType.NewsCmt:
                if(resultItems.get(ItemType.NewsCmt)!=null)
                {
                    ArrayList comments = (ArrayList<NewsCmt>) resultItems.get(ItemType.NewsCmt);
                    ArrayList users = (ArrayList<CmtUser>) resultItems.get(ItemType.CmtUser);
                    System.out.println(comments.size()+"    "+ users.size());
                };break;
            default:
                System.out.println("sorry");;break;
        }
    }

}
