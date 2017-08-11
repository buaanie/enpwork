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
    private static ESClient conn = new ESClient();
    private TransportClient client;
    private Logger logger;

    private ESClient() {
        logger = Logger.getLogger(this.getClass());
        buildESClient();
    }

    public static TransportClient getInstance() {
        return conn.client;
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
        es.createIndex("nnewsindex");
    }

    public void createIndex(String indexName) {
        deleteIndex(indexName);
        //创建索引
        /**可以在setting中指定该index特有的analyzer等配置，而无需在es的yml中进行总体配置。
         "analysis": {
            "filter": {
                "my_filter": {
                    "type": "ik",
                    "tokenize": "whitespace"
                }
            },
         //Analyzer 一般由三部分构成，character filters（字符过滤）、tokenizers（分词）、token filters（词单元过滤）
         //-> https://www.elastic.co/guide/cn/elasticsearch/guide/current/custom-analyzers.html
            "analyzer": {
                "my_analyzer": {
                    "type":"dic_ansj", <--这里指定ansj=
                    "stopwords":"stopKey", <--需要在ansj配置中指定stopKey的位置
                    "filter": ["my_filter"],
                    "tokenizer": "ik"
                }
            }
         }
        **/
        String template = "\"analysis\": {" +
                "  \"analyzer\": {" +
                "    \"id_analyzer\": {" +
                "      \"tokenizer\": \"id_tokenizer\"" +
                "    },    " +
                "    \"word_analyzer\": {" +
                "      \"tokenizer\": \"word_tokenizer\"" +
                "    }" +
                "  }," +
                "  \"tokenizer\": {" +
                "    \"id_tokenizer\": {" +
                "      \"type\": \"pattern\"," +
                "      \"pattern\": \",\"" +
                "    }," +
                "    \"word_tokenizer\": {" +
                "      \"type\": \"pattern\"," +
                "      \"pattern\": \"\\s|,|，\"" +
                "    }" +
                "  }" +
                "}";
        client.admin().indices().prepareCreate(indexName).setSettings(Settings.builder().loadFromSource(template).put("index.number_of_shards",10).put("index.refresh_interval","10s")).execute().actionGet();//.setAliases("")
        //创建索引结构
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("msg")
//	                    .startObject("_source").field("enabled", false).endObject() excludes
//                    .startObject("_source").field("includes", "title", "time","type").endObject()
                    .startObject("_source").field("excludes", "content", "source").endObject()
                    .startObject("properties")
                    .startObject("time").field("type", "date").field("format","yyy-MM-dd HH:mm:ss||yyy-MM-dd HH:mm||yyyy-MM-dd||epoch_millis").field("index", "not_analyzed").endObject()
                    .startObject("title").field("type", "string").field("index", "analyzed").field("analyzer", "index_ansj").field("search_analyzer","query_ansj").endObject()
                    .startObject("desp").field("type", "string").field("index", "analyzed").field("analyzer", "index_ansj").field("search_analyzer","query_ansj").endObject()
                    .startObject("content").field("type", "string").field("index", "analyzed").field("analyzer", "index_ansj").field("search_analyzer","query_ansj").endObject()
                    .startObject("news_id").field("type", "string").field("index", "analyzed").field("analyzer", "id_analyzer").endObject()
                    .startObject("url").field("type", "string").field("index", "not_analyzed").endObject()
                    .startObject("news_type").field("type", "string").field("index", "not_analyzed").endObject()
                    .startObject("source").field("type", "string").field("index", "not_analyzed").endObject()
                    .startObject("keywords").field("type", "string").field("index", "analyzed").field("analyzer", "word_analyzer").endObject()
                    .startObject("cmt_id").field("type", "string").field("index", "not_analyzed").endObject()
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
