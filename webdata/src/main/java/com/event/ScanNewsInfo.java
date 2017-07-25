package com.event;

/**
 * Created by ACT-NJ on 2016/10/10.
 */
import java.util.*;

import com.crawler.beans.NewsItem;
import com.store.DataAssembler;
import com.store.ESClient;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class ScanNewsInfo {
    private  final String NewsIndex = "news_all";
    private TransportClient client;
    public ScanNewsInfo()
    {
        client = ESClient.getInstance();
    }
    public static void main(String[] args) {
        ScanNewsInfo getNewsInfo = new ScanNewsInfo();
        ScanWeiboEvent getEventInfo = new ScanWeiboEvent();
        Date to = new Date(new Date().getTime()-4*3600*1000);
        Date from = new Date(to.getTime()-3600*15*1000);
        List<RelatedEvent> events = getEventInfo.filterEventFromTo(from,to);
        //检测微博新闻交集
        List<Neo4jData> neo4j = getNewsInfo.getNewsFromES(events);
        for(Neo4jData n:neo4j){
        	System.out.println(n.toString());
        }
    }
    public List<Neo4jData> getNewsFromES(List<RelatedEvent> events){
        Map<String,String> judge = new HashMap<>();
        List<Neo4jData> neo4jRes = new ArrayList<>();
        int maxSize = 5;
        for(RelatedEvent event : events){
            Date eventTime = event.getTime();
            Date from = new Date(eventTime.getTime() - 12*24*3600*1000L);
            Date to = new Date(eventTime.getTime() + 3*3600*1000L);
            String desc = event.getDesc().replaceAll("[\\pP‘’“”~^]", "");
            if(desc.length()>20)
                desc= desc.substring(0,20);
            String participant = event.getPart();
            String location = event.getLoca();
            String words = desc+" "+location+" "+participant;
            SortBuilder timeSort = SortBuilders.fieldSort("time").order(SortOrder.DESC);
            SortBuilder score = SortBuilders.scoreSort();
            QueryBuilder date = QueryBuilders.rangeQuery("time").from(from).to(to);
            QueryBuilder text = QueryBuilders.queryStringQuery(words);
            BoolQueryBuilder querys= QueryBuilders.boolQuery().must(text).should(date);
            SearchRequestBuilder request = client.prepareSearch(NewsIndex).setTypes("msg").setQuery(querys).setMinScore(0.65f)
                    .addSort(score).addSort(timeSort).setSize(maxSize);
            SearchResponse response = request.execute().actionGet();
            long total = response.getHits().getTotalHits();
            if(total==0){
                continue;
            }
//            System.out.println(words+" "+ event.getTime().toLocaleString()+ event.getDesc()+"-----总共"+total+"结果");
            NewsItem newsSubject;
            Map<String, Object> map = response.getHits().getAt(0).getSource();
            newsSubject = new DataAssembler().bindNews(map);
            if(!judge.containsKey(newsSubject.getTitle())){
                judge.put(newsSubject.getTitle(),"-");
                Neo4jData neo4jData = new Neo4jData(event.getID(),event.getDesc().replaceAll("\\[.+\\]", ""),newsSubject.getTitle(),event.getTime(),event.getLoca(),event.getPart(),event.getType(),event.getCorewords());
//                System.out.println(neo4jData.toString());
                neo4jRes.add(neo4jData);
            }
        }
        System.out.println("total N-E hits:"+neo4jRes.size());
        return neo4jRes;
    }

}
