package com.store;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.HBaseAdmin;
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

	public static void main(String[] args) {
		String tableName = "newsInfo";
		HBaseClient hbase = new HBaseClient();
		String[] strings = {"info"};
		try {
			hbase.createTable(tableName, strings);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建表操作
	 * @param tableName
	 * @param cfs
	 * @throws IOException
	 */
	public void createTable(String tableName, String[] cfs) throws IOException{
		HBaseAdmin admin = new HBaseAdmin(conf);
		if (admin.tableExists(tableName)) {
			System.out.println("表已经存在！");
		} else {
			TableName tablename = TableName.valueOf(tableName);
			HTableDescriptor tableDesc = new HTableDescriptor(tablename);
			for (int i = 0; i < cfs.length; i++) {
				tableDesc.addFamily(new HColumnDescriptor(cfs[i].getBytes()));
			}
			admin.createTable(tableDesc);
			System.out.println(" 表创建成功！");
		}
	}

	/**
	 * 删除表操作
	 * @param tableName
	 * @throws IOException
	 */
	public void deleteTable(String tableName) throws IOException {
		try {
			HBaseAdmin admin = new HBaseAdmin(conf);
			if (!admin.tableExists(tableName)) {
				System.out.println("表不存在, 无需进行删除操作！");
			}else{
				admin.disableTable(tableName);
				admin.deleteTable(tableName);
				System.out.println("表删除成功!");
			}
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		}
	}
}
