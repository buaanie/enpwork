package com.crawler.utils;

import com.store.ESClient;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ACT-NJ on 2017/7/22.
 */
public class GetIndexDocs {
    private TransportClient client = new ESClient().getESClient();
    private String index = "tjnews";

    private void deleteDocs(List<String> input){
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
            System.out.println(response.hasFailures());
        }
    }

    private List<String> findDocs(String filter){
        List<String> result = new ArrayList<>();
        int MaxSize = 4000;
		QueryBuilder url = QueryBuilders.termQuery("url", filter);
//		QueryBuilder url = QueryBuilders.existsQuery("url");
        QueryBuilder time = QueryBuilders.rangeQuery("time").from("2017-04-06");
        QueryBuilder allFilter= QueryBuilders.boolQuery().must(time).mustNot(url);
        SearchRequestBuilder request = client.prepareSearch(index).setTypes("msg")
                .setSearchType(SearchType.DEFAULT).setScroll(new TimeValue(30000))
                .setQuery(allFilter).setSize(MaxSize);
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

    public static void main(String[] args){
        GetIndexDocs g = new GetIndexDocs();
        GetResponse test = g.client.prepareGet("crawler_all", "msg", "4089924896513098").execute().actionGet();
        System.out.println(test.getSource().get("releasedate"));

//		SearchResponse response = client.prepareSearch(index).setSize(0).execute().actionGet();
//		System.out.println(response.getHits().getTotalHits());

//		List<String> idStrings = c.findDocs("tjrb");
//		System.out.println(idStrings.size());
//		for(String s:idStrings)
//			System.out.println(s);

//		List<String> idStrings = Arrays.asList("bftjyw031677525","bftjyw031677491","bftjyw031677423","bftjyw031677513");
//		deleteDocs(idStrings);
    }
}
