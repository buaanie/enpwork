package com.store;

import java.util.ArrayList;
import java.util.List;

import com.crawler.beans.NewsItem;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class CrawlerNewsIndex {
	private TransportClient client;
	private String index = "tjnews";
	private List<NewsItem> newsList = new ArrayList<NewsItem>();
    //添加一个日志器
    private Logger logger;
    public CrawlerNewsIndex() {
    	client = new ESClient().getESClient();
    	logger =  Logger.getLogger(this.getClass());
    	registerShutdownHook();
	}
	private void registerShutdownHook() {
		// TODO Auto-generated method stub
		Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
            	close();
            }
        } );
	}
	public void close(){
		try{
			insertIndexBulk(newsList);
			newsList.clear();
		}catch(Exception e){
			e.printStackTrace();
		}
		SearchResponse response = client.prepareSearch(index).setSize(0).execute().actionGet();
		logger.info(index+" -----><----------  "+ response.getHits().getTotalHits());
//		System.out.println(response.getHits().getTotalHits());
//		if(client!=null || client.connectedNodes().size()>0)
//			client.close();
	}

	public synchronized void insertAllIndex(NewsItem anews) throws Exception{
		insertES(anews);
	}
	private synchronized void insertES(NewsItem news){
		if(newsList.size() <= 50){
			newsList.add(news);
		}
		else{
			try{
				insertIndexBulk(newsList);
				newsList.clear();
				newsList.add(news);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	private void insertIndexBulk(List<NewsItem> newsList2) throws Exception {
		BulkRequestBuilder bqb = client.prepareBulk();	
		for (NewsItem news : newsList2) {
			GetResponse test = client.prepareGet(index, "msg", news.getId()).execute().actionGet();
			if(!test.isExists()){
				XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
						.startObject()
							.field("title", news.getTitle())
							.field("url", news.getURL())
							.field("time", news.getDateTime())
						.endObject();
				IndexRequestBuilder iqbn_test = client.prepareIndex(index, "msg",
						news.getId()).setSource(contentBuilder);
				bqb.add(iqbn_test);
			}
//			else{
//				XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
//						.startObject()
//							.field("url", news.getURL())
//						.endObject();
//				UpdateRequestBuilder iqb_up = client.prepareUpdate(index, "msg",
//						news.getId()).setDoc(contentBuilder);
//				bqb.add(iqb_up);
//				System.out.println(news.getURL());
//			}
		}
		if(bqb.numberOfActions()!=0){
			try {
				BulkResponse response = bqb.execute().actionGet();
//				System.out.println(Thread.currentThread().getName() + " insert tjnews into es");
				logger.info("--------------  insert tjnews into es  "+bqb.numberOfActions());
				if(response.hasFailures()){
					logger.error("es news error");
				}
			} catch (Exception e) {
				logger.error("error");
			}
		}
	}

}
