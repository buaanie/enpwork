package com.event;

/**
 * Created by ACT-NJ on 2016/10/10.
 */
import java.util.*;

import com.crawler.beans.NewsItem;
import com.store.DataAssembler;
import com.store.ESClient13;
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
    private static final String NewsIndex = "news_info3";
    private TransportClient client;
    public ScanNewsInfo()
    {
        client = ESClient13.getInstance();
    }
    public static void main(String[] args) {
        ScanNewsInfo getNewsInfo = new ScanNewsInfo();
        ScanWeiboEvent getEventInfo = new ScanWeiboEvent();
        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        Calendar temp = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH,-15);
        temp.add(Calendar.DAY_OF_MONTH,-14);
        while(temp.before(end)){
            List<RelatedEvent> events = getEventInfo.filterEventFromTo(start.getTime(),temp.getTime());
            getNewsInfo.getNewsFromES(events);
            start.add(Calendar.DAY_OF_MONTH,1);
            temp.add(Calendar.DAY_OF_MONTH,1);
        }
        //检测微博新闻交集
//        List<Neo4jData> neo4j = getNewsInfo.getNewsFromES(events);
//        for(Neo4jData n:neo4j){
//        	System.out.println(n.toString());
//        }
    }
    public List<Neo4jData> getNewsFromES(List<RelatedEvent> events){
        Set<String> judge = new HashSet();
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
            Map<String, Object> map = response.getHits().getAt(0).getSource();
            NewsItem newsSubject = new DataAssembler().bindNews(map);
            if(!judge.contains(newsSubject.getTitle())){
                judge.add(newsSubject.getTitle());
                Neo4jData neo4jData = new Neo4jData(event.getID(),event.getDesc().replaceAll("\\[.+\\]", ""),newsSubject.getTitle(),event.getTime(),event.getLoca(),event.getPart(),event.getType(),event.getCorewords());
//                System.out.println(neo4jData.toString());
                neo4jRes.add(neo4jData);
            }
        }
        System.out.println("total N-E hits:"+neo4jRes.size());
        return neo4jRes;
    }

}
