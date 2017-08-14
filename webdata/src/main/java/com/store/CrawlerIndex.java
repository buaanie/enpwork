package com.store;

import java.util.ArrayList;
import java.util.List;

import com.crawler.beans.NewsItem;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class CrawlerIndex {
	private static CrawlerIndex crawlerIndex = new CrawlerIndex();
	private TransportClient client;
	private List<NewsItem> newsList = new ArrayList<NewsItem>();
    //添加一个日志器
    private Logger logger;
    private CrawlerIndex() {
    	client = ESClient.getInstance();
    	logger = Logger.getLogger(this.getClass());
    	registerShutdownHook();
	}
	public static CrawlerIndex getIndex(){
    	return crawlerIndex;
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
			indexBulk(newsList);
			newsList.clear();
		}catch(Exception e){
			e.printStackTrace();
		}
//		SearchResponse response = client.prepareSearch(index).setSize(0).execute().actionGet();
//		logger.info(index+" -----><----------  "+ response.getHits().getTotalHits());
//		System.out.println(response.getHits().getTotalHits());
		if(client!=null || client.connectedNodes().size()>0)
			client.close();
	}

	public synchronized void indexNews(NewsItem anews){
        if(newsList.size() <= 50){
            newsList.add(anews);
        }
        else{
            try{
                indexBulk(newsList);
                newsList.clear();
                newsList.add(anews);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
	}
	private void indexBulk(List<NewsItem> newsLists) throws Exception {
		String index = "nnewsindex";
		BulkRequestBuilder bqb = client.prepareBulk();
		for (NewsItem news : newsLists) {
			GetResponse test = client.prepareGet(index, "msg", news.getId()).execute().actionGet();
			if(!test.isExists()){
				XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
						.startObject()
							.field("time", news.getStringTime())
							.field("title", news.getTitle())
							.field("desp", news.getDesp())
							.field("content", news.getContent())
                            .field("newsid", news.getId())
                            .field("url", news.getURL())
							.field("newstype", news.getType())
							.field("source", news.getSource())
							.field("keywords", news.getKeywords())
							.field("cmtid", news.getCmtID())
						.endObject();
				IndexRequestBuilder iqbn = client.prepareIndex(index, "msg", news.getId()).setSource(contentBuilder);
				bqb.add(iqbn);
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
                System.out.println("start bulk");
                BulkResponse response = bqb.execute().actionGet();
				System.out.println(Thread.currentThread().getName() + " index news into es");
				logger.info(Thread.currentThread().getName()+" --------- index news into es  "+bqb.numberOfActions());
				if(response.hasFailures()){
					logger.error("index news error");
					System.out.println(response.buildFailureMessage());
				}
			} catch (Exception e) {
				logger.error("error");
			}
		}
	}

}
