package com.store;

import java.util.ArrayList;
import java.util.List;

import com.crawler.beans.NewsItem;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class CrawlerNewsIndex {
	private TransportClient client;
	private ESClient esClient;
	private String index = "tjnews";
	private List<NewsItem> newsList = new ArrayList<NewsItem>();
    //添加一个日志器
    private Logger logger;
    public CrawlerNewsIndex() {
    	esClient = new ESClient();
    	client = esClient.getESClient();
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
	public synchronized void insertAllIndex(NewsItem anews) throws Exception{
		insertES(anews);
	}
	private synchronized void insertES(NewsItem news){
		if(newsList.size() <= 50){
			newsList.add(news);
		}
		else{
//			logger.info("news start to bulk");
			try{
				insertIndexBulk(newsList);
				newsList.clear();
				newsList.add(news);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void close(){
		try{
			insertIndexBulk(newsList);
			System.out.println("===================");
			newsList.clear();
		}catch(Exception e){
			e.printStackTrace();
		}
		SearchResponse response = client.prepareSearch(index).setSize(0).execute().actionGet();
		logger.info(index+" $$$$$$$$$$$$$  "+ response.getHits().getTotalHits());
//		System.out.println(response.getHits().getTotalHits());
//		if(client!=null || client.connectedNodes().size()>0)
//			client.close();
	}

	public static void main(String[] args){
//		SearchResponse response = client.prepareSearch(index).setSize(0).execute().actionGet();
//		System.out.println(response.getHits().getTotalHits());
		CrawlerNewsIndex c = new CrawlerNewsIndex();
		GetResponse test = c.client.prepareGet("crawler_all", "msg", "4089924896513098").execute().actionGet();
		System.out.println(test.getSource().get("releasedate"));
//		List<String> idStrings = c.findString("tjrb");
//		System.out.println(idStrings.size());
//		for(String s:idStrings)
//			System.out.println(s);
//		List<String> idStrings = Arrays.asList("bftjyw031677525","bftjyw031677491","bftjyw031677423","bftjyw031677513","bftjyw031677386","bftjyw031677598","bftjyw031677410","bftjyw031677496","bftjyw031677412","bftjyw031677490","bftjyw031677609","bftjyw031677521","bftjyw031677564","bftjyw031677494","bftjyw031679106","bftjyw031677501","bftjyw031677492","bftjyw031677563","bftjyw031677407","bftjyw031677409");
//		deleteID(idStrings);
	}
	private void deleteID(List<String> input){
		BulkRequestBuilder bqb = client.prepareBulk();
		for(String id :input){
			System.out.print(id+",");
			GetResponse test = client.prepareGet(index, "msg", id).execute().actionGet();
			if(test.isExists()){
				DeleteRequestBuilder iqbn_test = client.prepareDelete(index, "msg",id);
				bqb.add(iqbn_test);
			}
		}
		if(bqb.numberOfActions()!=0){
			BulkResponse response = bqb.execute().actionGet();
			logger.info("************** bulksize "+response.contextSize());
			System.out.println(response.hasFailures());
		}
		else{
			System.out.println("=======");
		}
	}
	private synchronized void insertIndexBulk(List<NewsItem> newsList2) throws Exception {
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
				System.out.println(Thread.currentThread().getName() + " insert tjnews into es");
				logger.info("--------------  insert tjnews into es  "+bqb.numberOfActions());
				if(response.hasFailures()){
					logger.error("es news error");
				}
			} catch (Exception e) {
				logger.error("error");
			}
		}
	}
	private List<String> findString(String filter){
		List<String> result = new ArrayList<>();
		int MaxSize = 4000;
//		QueryBuilder url = QueryBuilders.termQuery("url", filter);
//		QueryBuilder url = QueryBuilders.existsQuery("url");
		QueryBuilder time = QueryBuilders.rangeQuery("time").from("2017-04-06");
		QueryBuilder allFilter= QueryBuilders.boolQuery().must(time);//.mustNot(url);
		SearchRequestBuilder request = client.prepareSearch(index).setTypes("msg")
                .setSearchType(SearchType.DEFAULT).setScroll(new TimeValue(30000))
                .setQuery(allFilter).setSize(MaxSize);
//                .setPostFilter(url).setSize(MaxSize);
		SearchResponse scrollResp = request.execute().actionGet();
        System.out.println("total event hits:" + scrollResp.getHits().getTotalHits());
        while (true) {
            for (SearchHit hit : scrollResp.getHits()) {
            	if(hit.getSource().get("url")==null || hit.getSource().get("url").toString().matches("\\s*")){
                    String id = hit.getId();
                    result.add(id);
            	}
            }
            //通过scrollid来实现深度翻页
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
        return  result;
	}
}
