package com.utils;

import com.store.ESClient13;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.*;

/**
 * Created by ACT-NJ on 2017/7/22.
 */
public class GetIndexDocs {
    private TransportClient client = ESClient13.getInstance();
    private String index = "nnews";
    private String _index = "nevents";
    private int MaxSize = 3000;

    public HashMap getCmtIds(long from,long to,String index){
        HashMap<String,List<String>> result = new HashMap<>();
        QueryBuilder time = QueryBuilders.rangeQuery("time").from(from).to(to);
        QueryBuilder cmt = QueryBuilders.termQuery("cmtid","");
        BoolQueryBuilder bool = QueryBuilders.boolQuery().must(time).mustNot(cmt);
        SearchRequestBuilder request = client.prepareSearch(index).setTypes("msg")
                .setSearchType(SearchType.DEFAULT).setScroll(new TimeValue(10000))
                .setQuery(bool).setSize(MaxSize);
        SearchResponse scrollResp = request.execute().actionGet();
        System.out.println(scrollResp.getHits().getHits().length);
        if(scrollResp.getHits().getHits().length>0) {
            result.put("nts", new ArrayList<>());
            result.put("sin", new ArrayList<>());
            result.put("tct", new ArrayList<>());
        }
        while (true) {
            for (SearchHit hit : scrollResp.getHits()) {
                String id_prefix = hit.getId().split("-")[0];
                result.get(id_prefix).add(String.valueOf(hit.getSource().get("cmtid")));
            }
            //通过scrollid来实现深度翻页
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(10000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
        return result;
    }

    public List<Pair> getPeriodNews(long from,long to){
        QueryBuilder time = QueryBuilders.rangeQuery("time").from(from).to(to);
        SortBuilder sort = SortBuilders.fieldSort("time").order(SortOrder.DESC);
        SearchRequestBuilder request = client.prepareSearch(index).setTypes("msg")
                .setSearchType(SearchType.DEFAULT).setScroll(new TimeValue(10000))
                .setQuery(time).addSort(sort).setSize(MaxSize);
        SearchResponse scrollResp = request.execute().actionGet();
        int num =  (int)scrollResp.getHits().getTotalHits();
        int cabage = (int) (num*1.5);
        List<Pair> result = new ArrayList<>(cabage);
        int index = 0;
        while (true) {
            for (SearchHit hit : scrollResp.getHits()) {
                String id = String.valueOf(hit.getSource().get("newsid"));
                String title = String.valueOf(hit.getSource().get("title"));
//                System.out.println(index + " - "+ String.valueOf(hit.getSource().get("time")));
                index++;
                result.add(new Pair(id,title));
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(10000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
        return result;
    }

    //删除文档
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

    //利用id查询文档
    private void getDocByID(String id){
        GetResponse get = client.prepareGet(index,"msg",id).execute().actionGet();
        if(get.isExists())
            System.out.println(get.getId());
    }

    //利用scroll查询
    private List<String> findDocsByURL(String filter){
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

    private List<String> findDocsByTimeAndType(long start,long end){
        String _index = "";
        List<String> result = new ArrayList<>();
        int MaxSize = 10000;
        QueryBuilder time = QueryBuilders.rangeQuery("time").from(start).to(end);
        QueryBuilder uid = QueryBuilders.rangeQuery("uid").from(0).to(52);
        QueryBuilder type = QueryBuilders.rangeQuery("eventtype").from(0);
        QueryBuilder allFilter= QueryBuilders.boolQuery().must(time).must(uid).must(type);
        SearchRequestBuilder request = client.prepareSearch(_index).setTypes("msg")
                .setSearchType(SearchType.DEFAULT).setScroll(new TimeValue(30000))
                .setQuery(allFilter).setSize(MaxSize);
        SearchResponse scrollResp = request.execute().actionGet();
        System.out.println("total event hits:" + scrollResp.getHits().getTotalHits());
        while (true) {
            for (SearchHit hit : scrollResp.getHits()) {
                String title = hit.getSource().get("title").toString();
//                    String id = hit.getId();
//                    result.add(id);
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
        long from = System.currentTimeMillis();
        long to = from+8*60*60*1000;
        GetIndexDocs g = new GetIndexDocs();
        HashMap<String,List<String>> ids =  g.getCmtIds(from,to,"nnews");
        System.out.println(from+" "+to);
        List<String> nts = ids.get("nts");
        List<String> sin = ids.get("sin");
        List<String> tct = ids.get("tct");
        System.out.println(nts.size()+" "+sin.size()+" "+tct.size());
//        GetResponse test = g.client.prepareGet("crawler_all", "msg", "4089924896513098").execute().actionGet();
//        System.out.println(test.getSource().get("releasedate"));

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
