package com.store;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.log4j.Logger;

public class HBaseClient {
	private static  Configuration conf;
	private static HConnection conn;
	private static Logger logger;
	public HBaseClient(){
		logger =Logger.getLogger(this.getClass());
		buildHBaseClient();
	}
	public HTable getTable(String tableName) throws IOException{
		return (HTable) conn.getTable(tableName);
	}
	private void buildHBaseClient() {
		try {
			conf = HBaseConfiguration.create();
			conf.set("hbase.zookeeper.property.clientPort", "2222");
			conf.set("hbase.zookeeper.quorum","10.1.1.34,10.1.1.35,10.1.1.36,10.1.1.37,10.1.1.38");
			conf.set("hbase.zookeeper.property.dataDir","/root/hbase/zookeeper");
			conn = HConnectionManager.createConnection(conf);
			logger.info("connect to HBASE______");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
