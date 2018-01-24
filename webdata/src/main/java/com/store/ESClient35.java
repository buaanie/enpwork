package com.store;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.util.Properties;

/**
 * Created by stcas on 2018/1/22.
 */
public class ESClient35 {
    private static ESClient35 conn = new ESClient35();
    private TransportClient client;
    private Logger logger;

    private ESClient35() {
        logger = LoggerFactory.getLogger(this.getClass());
        buildESClient35();
    }

    public static TransportClient getInstance() {
        return conn.client;
    }

    private void buildESClient35() {
        String clusterName = "elasticsearch";
        Settings settings = Settings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("cluster.name", clusterName).build();
        try {
            client = TransportClient.builder().settings(settings).build();
			client.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.34"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.35"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.36"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(Inet4Address.getByName("10.1.1.37"), 9300));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void close(){
        client.close();
    }
    public static void main(String[] args) {
    }
}
