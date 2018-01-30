package com.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.crawler.beans.NewsCmt;
import com.crawler.beans.NewsItem;
import com.event.EventInfo;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerIndex {
	private static CrawlerIndex crawlerIndex = new CrawlerIndex();
	private static final String INDEX_NEWS = "nnews";
	private static final String INDEX_CMTS = "ncmts";
	private static final String INDEX_EVENTS = "nevents";
	private TransportClient client;
	private List<NewsItem> newsList = new ArrayList<>();
    //添加一个日志器
    private Logger logger;
    private CrawlerIndex() {
    	client = ESClient13.getInstance();
    	logger = LoggerFactory.getLogger(this.getClass());
    	registerShutdownHook();
	}
	public static CrawlerIndex getIndex(){
    	return crawlerIndex;
	}
	private void registerShutdownHook() {
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
			logger.error(e.getMessage());
		}
//		SearchResponse response = client.prepareSearch(index).setSize(0).execute().actionGet();
//		logger.info(index+" get response: "+ response.getHits().getTotalHits());
		if(client!=null || client.connectedNodes().isEmpty()) {
			client.close();
		}
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
                logger.error(e.getMessage());
            }
        }
	}
	private void indexBulk(List<NewsItem> newsLists) throws IOException {
		BulkRequestBuilder bqb = client.prepareBulk();
		for (NewsItem news : newsLists) {
			GetResponse test = client.prepareGet(INDEX_NEWS, "msg", news.getId()).execute().actionGet();
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
				IndexRequestBuilder iqbn = client.prepareIndex(INDEX_NEWS, "msg", news.getId()).setSource(contentBuilder);
				bqb.add(iqbn);
            }
		}
        if(bqb.numberOfActions()!=0){
			try {
				long  start = System.currentTimeMillis();
                BulkResponse response = bqb.execute().actionGet();
				logger.info("index news into es, hits:{}, time:{}",bqb.numberOfActions(),System.currentTimeMillis()-start);
				if(response.hasFailures()){
					logger.error("index news error: "+response.buildFailureMessage());
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	public synchronized void indexCmts(List<NewsCmt> cmtList){
        BulkRequestBuilder bqb = client.prepareBulk();
        try {
            for (NewsCmt cmt : cmtList) {
			    GetResponse test = client.prepareGet(INDEX_CMTS, "msg", cmt.getID()).execute().actionGet();
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
                    IndexRequestBuilder iqbn = client.prepareIndex(INDEX_CMTS, "msg", cmt.getID()).setSource(contentBuilder);
                    bqb.add(iqbn);
                }
                else{
                    XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                            .startObject()
                                .field("upnum", cmt.getUpNum())
                            .endObject();
                    UpdateRequestBuilder iqb = client.prepareUpdate(INDEX_CMTS, "msg",
                            cmt.getID()).setDoc(contentBuilder);
                    bqb.add(iqb);
                }
		    }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
		if(bqb.numberOfActions()!=0){
			try {
				BulkResponse response = bqb.execute().actionGet();
				logger.info(Thread.currentThread().getName()+" >> index comments into es  "+bqb.numberOfActions());
				if(response.hasFailures()){
					logger.error("index news error:"+response.buildFailureMessage());
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
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
                        .field("articleid", event.getArticleIds())
                        .field("show", event.getShow())
                        .field("summary", event.getSummary())
                        .endObject();
                IndexRequestBuilder iqbn = client.prepareIndex(INDEX_EVENTS, "msg", event.getEventId()).setSource(contentBuilder);
                bqb.add(iqbn);
			}
            if(bqb.numberOfActions()!=0){
                BulkResponse response = bqb.execute().actionGet();
//                Iterator<BulkItemResponse> it = response.iterator();
//                while(it.hasNext())
//                    System.out.println(it.next().getId());
                logger.info(Thread.currentThread().getName()+" >> index events into es  "+bqb.numberOfActions());
                if(response.hasFailures()){
                    logger.error("index events error: "+response.buildFailureMessage());
                }
            }
		}catch (IOException e){
			logger.error(e.getMessage());
		}
	}
}
