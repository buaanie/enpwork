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
    public void getRawNews(){
        GetTrainText getNews = new GetTrainText("hbase");
        String startRow = "0";
        getTableRows.getNewsFrom(startRow);
    }

    public static void main(String[] args) {

    }
}
