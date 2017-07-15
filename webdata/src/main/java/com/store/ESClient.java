package com.store;

import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

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
        logger =Logger.getLogger(this.getClass());
        buildESClient();
    }
    public TransportClient getESClient(){
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
                logger.info("connect to ES______");
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
}
