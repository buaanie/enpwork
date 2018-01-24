package com.ansj.ner;

import com.store.ESClient13;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ACT-NJ on 2017/6/25.
 */
public class GetEventInfo {
    private final String EventIndex = "events_v3";
    private TransportClient esClient;
    private Logger logger;
    public GetEventInfo(){
        esClient = ESClient13.getInstance();
        logger = LoggerFactory.getLogger(this.getClass());
    }
    public List<String> filterEvent(Date from, Date to){
        int MaxSize = 600;
        SortBuilder timeSort = SortBuilders.fieldSort("time").order(SortOrder.DESC);
        QueryBuilder dateFilter = QueryBuilders.rangeQuery("time").from(from).to(to);
        QueryBuilder participant = QueryBuilders.existsQuery("participant");
        QueryBuilder location = QueryBuilders.existsQuery("location");
        QueryBuilder type_0 = QueryBuilders.matchQuery("e_type", 0);
        QueryBuilder type_9 = QueryBuilders.matchQuery("e_type", 9);
        QueryBuilder type = QueryBuilders.boolQuery().mustNot(type_0).mustNot(type_9);
        QueryBuilder allFilter = QueryBuilders.boolQuery().should(participant).should(location).must(type).must(dateFilter);
        SearchRequestBuilder request = esClient.prepareSearch(EventIndex).setTypes("msg")
                .setSearchType(SearchType.DEFAULT).setScroll(new TimeValue(30000))
                .setPostFilter(allFilter).addSort(timeSort).setSize(MaxSize);
        SearchResponse scrollResp = request.execute().actionGet();
        while (true) {
            for (SearchHit hit : scrollResp.getHits()) {
                String id = hit.getId();
                Map<String, Object> map = hit.getSource();
                if (map.get("participant").toString().equals("")||map.get("location").toString().equals("")||map.get("participant").toString().contains("某")||map.get("desc").toString().equals("")) {
                    continue;
                }
//                result.add(bind.assembleEvent(member, map));
            }
            //通过scrollid来实现深度翻页
            scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
        return null;
    }
}
