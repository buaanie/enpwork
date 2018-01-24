package com.graph.service;

import com.alibaba.fastjson.JSONObject;
import com.event.EventInfo;
import com.store.DataAssembler;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by stcas on 2017/12/24.
 */
public class EventQuery {
    private final String EventIndex = "events_v3";
    private TransportClient esClient;
    private DataAssembler assembler;
    private Logger logger;
    public EventQuery(){
        esClient = ESClient13.getInstance();
        assembler = new DataAssembler();
        logger = LoggerFactory.getLogger(this.getClass());
    }
    public JSONObject requestEvent(int type,int period){
        long from = new Date().getTime();
        long to = from - period*24*3600*1000;
        List<EventInfo> result = new ArrayList<>();
        SortBuilder timeSort = SortBuilders.fieldSort("time").order(SortOrder.DESC);
        QueryBuilder periodFilter = QueryBuilders.rangeQuery("time").from(from).to(to);
        QueryBuilder typeFilter = QueryBuilders.existsQuery("e_type");
        if(type!=0){
            typeFilter = QueryBuilders.matchQuery("e_type", type);
        }
        QueryBuilder allFilter = QueryBuilders.boolQuery().must(typeFilter).must(periodFilter);
        SearchRequestBuilder request = esClient.prepareSearch(EventIndex).setTypes("msg")
                .setSearchType(SearchType.DEFAULT).setScroll(new TimeValue(30000))
                .setPostFilter(allFilter).addSort(timeSort).setSize(300);
        SearchResponse scrollResp = request.execute().actionGet();
        while (true) {
            for (SearchHit hit : scrollResp.getHits()) {
                String id = hit.getId();
                Map<String, Object> map = hit.getSource();
//                if (map.get("participant").toString().equals("")) {
//                    continue;
//                }
                result.add(assembler.bindEvent(id, map));
            }
            scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
        return null;
    }
}
