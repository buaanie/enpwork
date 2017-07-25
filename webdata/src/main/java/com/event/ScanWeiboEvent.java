package com.event;

/**
 * Created by ACT-NJ on 2016/10/10.
 */
import com.store.DataAssembler;
import com.store.ESClient;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.SearchHit;

import java.util.*;


public class ScanWeiboEvent {
    private final String EventIndex = "events_v3";
    private TransportClient client;
    public ScanWeiboEvent(){
        client = ESClient.getInstance();
    }
    public static void main(String[] args) {
        ScanWeiboEvent getEvent = new ScanWeiboEvent();
        Date to = new Date(new Date().getTime());
        Date from = new Date(to.getTime() -12000*1000);
        List<RelatedEvent> relatedEvents = getEvent.filterEventFromTo(from, to);
        for(RelatedEvent r:relatedEvents){
        	System.out.println(r.toStringJson());
        }
    }
    public Map<String, String> getEventFromES(Date fromDate,Date toDate) {
        //实例化不必考虑null情况
        Map<String, String> event_words = new HashMap<>();
        boolean search = true;
        while (search) {
            List<RelatedEvent> eventList = filterEventFromTo(fromDate, toDate);
            if(eventList.size()==0 || eventList==null){
                System.out.println("total task event : 0");
                return null;
            }
            for (RelatedEvent event : eventList) {
                String weibo = event.getCorewords();
                if(!(weibo==null))
                    event_words.put(event.getID(), weibo);
            }
            search = false;
        }
        return event_words;
    }
    //获取某段时间的事件
    public List<RelatedEvent> filterEventFromTo(Date from, Date to) {
        //实例初始化为0
        List<RelatedEvent> result = new ArrayList<>();
        int MaxSize = 600;
        SortBuilder timeSort = SortBuilders.fieldSort("time").order(SortOrder.DESC);
        QueryBuilder dateFilter = QueryBuilders.rangeQuery("time").from(from).to(to);
        QueryBuilder participant = QueryBuilders.existsQuery("participant");
        QueryBuilder location = QueryBuilders.existsQuery("location");
        QueryBuilder type_0 = QueryBuilders.matchQuery("e_type", 0);
        QueryBuilder type_9 = QueryBuilders.matchQuery("e_type", 9);
//        QueryBuilder hot = QueryBuilders.rangeQuery("hot").gt(6);.must(hot)
        QueryBuilder type = QueryBuilders.boolQuery().mustNot(type_0).mustNot(type_9);
        QueryBuilder allFilter = QueryBuilders.boolQuery().should(participant).should(location).must(type).must(dateFilter);
        SearchRequestBuilder request = client.prepareSearch(EventIndex).setTypes("msg")
                .setSearchType(SearchType.DEFAULT).setScroll(new TimeValue(30000))
                .setPostFilter(allFilter).addSort(timeSort).setSize(MaxSize);
        SearchResponse scrollResp = request.execute().actionGet();
        System.out.println("total event hits:" + scrollResp.getHits().getTotalHits());
        while (true) {
            for (SearchHit hit : scrollResp.getHits()) {
                String id = hit.getId();
                RelatedEvent member = new RelatedEvent(id);
                Map<String, Object> map = hit.getSource();
                if (map.get("participant").toString().equals("")||map.get("location").toString().equals("")||map.get("participant").toString().contains("某")||map.get("desc").toString().equals("")) {
                	continue;
                }
                result.add( new DataAssembler().bindEvent(id, map));
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
