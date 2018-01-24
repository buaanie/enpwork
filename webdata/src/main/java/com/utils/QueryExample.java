package com.utils;

import com.crawler.beans.NewsItem;
import com.store.DataAssembler;
import com.store.ESClient13;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ACT-NJ on 2017/7/22.
 */
public class QueryExample {
    private TransportClient client = ESClient13.getInstance();
    private DataAssembler assembler = new DataAssembler();
    public List<NewsItem> getRelatedNewsSubject(String query_word) {
        String indexName = "news_all";
        List<NewsItem> NewsSubjectList =new ArrayList<>();
        int MaxSize = 30;
        SortBuilder time = SortBuilders.fieldSort("time").order(SortOrder.DESC);
        SortBuilder score = SortBuilders.scoreSort();
        //只要满足在标题和正文中其一即可，若有多个关键词需AND
        QueryBuilder query = QueryBuilders.multiMatchQuery(query_word,"title","content").operator(MatchQueryBuilder.Operator.AND);
        //值得注意的是sort限定的顺序会影响最终结果
        SearchRequestBuilder request = client.prepareSearch(indexName).setTypes("msg").setQuery(query)
                .setMinScore((float) 0.6).addSort(score).addSort(time).setSize(MaxSize);
        SearchResponse response = request.execute().actionGet();
        long total = response.getHits().getTotalHits();
        System.out.println("总共"+total+"结果");
        for (int i = 0; i < MaxSize && i < total; i++) {
//            System.out.println(response.getHits().getAt(i).getScore());
            Map<String, Object> map = response.getHits().getAt(i).getSource();
            NewsItem NewsSubject = assembler.bindNews(map);
            NewsSubjectList.add(NewsSubject);
        }
        return NewsSubjectList;
    }
}
