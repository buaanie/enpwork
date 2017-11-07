package com.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.crawler.beans.NewsCmt;
import com.crawler.beans.NewsItem;
import com.event.EventInfo;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class CrawlerIndex {
	private static CrawlerIndex crawlerIndex = new CrawlerIndex();
	private final String index_news = "nnews";
	private final String index_cmts = "ncmts";
	private final String index_events = "nevents";
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
		BulkRequestBuilder bqb = client.prepareBulk();
		for (NewsItem news : newsLists) {
			GetResponse test = client.prepareGet(index_news, "msg", news.getId()).execute().actionGet();
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
							.field("isHot", news.getHot())
						.endObject();
				IndexRequestBuilder iqbn = client.prepareIndex(index_news, "msg", news.getId()).setSource(contentBuilder);
				bqb.add(iqbn);
            }
		}
        if(bqb.numberOfActions()!=0){
			try {
                BulkResponse response = bqb.execute().actionGet();
				System.out.println(Thread.currentThread().getName() + " index news into es");
				logger.info(Thread.currentThread().getName()+" --------- index news into es  "+bqb.numberOfActions());
				if(response.hasFailures()){
					logger.error("index news error");
					System.out.println(response.buildFailureMessage());
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	public synchronized void indexCmts(List<NewsCmt> cmtList){
        BulkRequestBuilder bqb = client.prepareBulk();
        try {
            for (NewsCmt cmt : cmtList) {
			    GetResponse test = client.prepareGet(index_cmts, "msg", cmt.getID()).execute().actionGet();
			    if(!test.isExists()){
                    XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field("time", cmt.getStringTime())
                                .field("content", cmt.getContent())
                                .field("newsid", cmt.getTargetId())
                                .field("userid", cmt.getUid())
                                .field("upnum", cmt.getUpNum())
                                .field("cpid", cmt.getPid())
                                .field("crid", cmt.getRid())
                                .endObject();
                    IndexRequestBuilder iqbn = client.prepareIndex(index_cmts, "msg", cmt.getID()).setSource(contentBuilder);
                    bqb.add(iqbn);
                }
                else{
                    XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                            .startObject()
                                .field("upnum", cmt.getUpNum())
                            .endObject();
                    UpdateRequestBuilder iqb_up = client.prepareUpdate(index_cmts, "msg",
                            cmt.getID()).setDoc(contentBuilder);
                    bqb.add(iqb_up);
                }
		    }
        } catch (IOException e) {
            e.printStackTrace();
        }
		if(bqb.numberOfActions()!=0){
			try {
				BulkResponse response = bqb.execute().actionGet();
				System.out.println(Thread.currentThread().getName() + " index comments into es");
				logger.info(Thread.currentThread().getName()+" --------- index comments into es  "+bqb.numberOfActions());
				if(response.hasFailures()){
					logger.error("index news error");
					System.out.println(response.buildFailureMessage());
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	public void indexEvent1Step(List<EventInfo> events1st){
		BulkRequestBuilder bqb = client.prepareBulk();
		try{
			for (EventInfo event : events1st) {
                XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("eventid", event.getEventId())
                        .field("articleid", event.getArticleId())
                        .field("show", event.getShow())
                        .field("summary", event.getSummary())
                        .endObject();
                IndexRequestBuilder iqbn = client.prepareIndex(index_events, "msg", event.getEventId()).setSource(contentBuilder);
                bqb.add(iqbn);
			}
            if(bqb.numberOfActions()!=0){
                BulkResponse response = bqb.execute().actionGet();
                Iterator<BulkItemResponse> it = response.iterator();
                while(it.hasNext())
                    System.out.println(it.next().getId());
                System.out.println(Thread.currentThread().getName() + " index events into es ,total:"+ bqb.numberOfActions());
                logger.info(Thread.currentThread().getName()+" --------- index events into es  "+bqb.numberOfActions());
                if(response.hasFailures()){
                    logger.error("index events error");
                    System.out.println(response.buildFailureMessage());
                }
            }
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
