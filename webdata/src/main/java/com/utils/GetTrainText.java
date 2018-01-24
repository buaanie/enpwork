package com.utils;

import com.store.CrawlerHBase;
import com.store.CrawlerIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by stcas on 2018/1/9.
 */
public class GetTrainText {
    private CrawlerIndex crawlerNewsIndex;
    private GetTableRows getTableRows;
    private Logger logger;
    public GetTrainText(String type){
        if(type.equals("es")) {
            crawlerNewsIndex = CrawlerIndex.getIndex();
        }
        else if(type.equals("hbase")){
            getTableRows = new GetTableRows();
        }
        logger = LoggerFactory.getLogger(GetTrainText.class);
    }
    public void getHBaseRawNews(String startRow){
        getTableRows.getNewsFrom(startRow);
    }

    //注意采用秒而不是毫秒
    private void getDocsByTime(long start,long end){
        long temp = start + 259200;//3*24*3600
        while(temp < end){
        }
    }

    public static void main(String[] args) {
        String source = "hbase";
        GetTrainText getNews = new GetTrainText(source);
        getNews.getHBaseRawNews("2017-03");
    }
}
