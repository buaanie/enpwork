package com.store;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.util.Properties;

/**
 * Created by ACT-NJ on 2017/5/31.
 */
public class ESClient {
    public static TransportClient client;
    private static Logger logger;

    public ESClient() {
        logger = Logger.getLogger(this.getClass());
        buildESClient();
    }

    public TransportClient getESClient() {
        return client;
    }

    private void buildESClient() {
        Settings settings = Settings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("cluster.name", "elasticsearch").build();
        try {
            client = TransportClient.builder().settings(settings).build();
            InputStream stream = this.getClass().getResourceAsStream("/es.properties");
            Properties properties = new Properties();
            try {
                properties.load(stream);
                int port = Integer.valueOf(properties.getProperty("port", "9300"));
                String hosts = properties.getProperty("hosts", "127.0.0.1");
                for (String host : hosts.split(",")) {
                    client.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName(host), port));
                }
                logger.info("----><----- connect to ES ----><-----");
            } catch (IOException e) {
                e.printStackTrace();
            }
//			client.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.33"), 9300))
//					.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.34"), 9300))
//					.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.35"), 9300))
//					.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.36"), 9300))
//					.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.37"), 9300));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(){
        client.close();
    }
    public static void main(String[] args) {
        ESClient es = new ESClient();
        //具体配置请在具体方法中填写
        es.createIndex("hello");
    }

    public void createIndex(String indexName) {
        deleteIndex(indexName);
        //创建索引
        client.admin().indices().prepareCreate(indexName).execute().actionGet();//.setAliases("")
        //创建索引结构
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("msg")
//	                    .startObject("_source").field("enabled", false).endObject() excludes
                    .startObject("_source").field("includes", "a1", "a2").endObject()
                    .startObject("properties")
                    .startObject("a1").field("type", "string").field("index", "not_analyzed").endObject()
                    .startObject("a2").field("type", "string").field("index", "analyzed").field("analyzer", "ik").endObject()
                    .startObject("a3").field("type", "string").field("index", "analyzed").field("analyzer", "ik").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            PutMappingRequest mapping = Requests.putMappingRequest(indexName).type("msg").source(builder);
            client.admin().indices().putMapping(mapping).actionGet();
            System.out.println("创建成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modifyIndexMap(String indexName) {
        //更改map
        try {
            PutMappingRequestBuilder mappingRequest = client.admin().indices().preparePutMapping(indexName);
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("msg")
                    .startObject("_source").field("includes", "reply", "ge2").endObject()
                	.startObject("properties")
						.startObject("reply").field("type", "long").field("index","not_analyzed").endObject()
						.startObject("ge2").field("type", "string").field("index","analyzed").field("analyzer","ik").endObject()
						.startObject("ge3").field("type", "string").field("index","not_analyzed").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            System.out.println(builder.string());
            mappingRequest.setSource(builder).setType("msg").execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteIndex(String indexName){
        //删除索引
        IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
        if (res.isExists()) {
            DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
            delIdx.execute().actionGet();
            System.out.println("删除成功");
        }
    }
}
