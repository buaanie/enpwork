package com.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.crawler.beans.NewsItem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;


public class StoreHBaseNews {
	private HBaseClient hbaseClient;
	private HTable newsInfo;
	private List<Put> newsList;
	private Logger logger;
	public StoreHBaseNews(){
		try {
			hbaseClient = new HBaseClient();
			newsInfo = hbaseClient.getTable("tjnews");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    newsList = new ArrayList<Put>();
	    logger =  Logger.getLogger(this.getClass());
	    registerShutdownHook();
	}
	public void close(){
    	try {
    		if (newsList != null && newsList.size() != 0) {
    			System.out.println(Thread.currentThread().getName() + " insert HBase");
    			newsInfo.put(newsList);
    			newsInfo.flushCommits();
    			newsList.clear();
    		}
    		if(newsInfo!=null){
    			newsInfo.close();
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
            	close();
            }
        });
	}
	public synchronized void storeNews(NewsItem newsSubject){
		try {
			Put put = new Put(Bytes.toBytes(newsSubject.getId()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("title"), Bytes.toBytes(newsSubject.getTitle()));			
			put.add(Bytes.toBytes("info"), Bytes.toBytes("url"), Bytes.toBytes(newsSubject.getURL()));
			if(newsSubject.getSource()!=null && !newsSubject.getSource().equals(""))
				put.add(Bytes.toBytes("info"), Bytes.toBytes("source"), Bytes.toBytes(newsSubject.getSource()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("time"), Bytes.toBytes(newsSubject.getStringTime()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("content"), Bytes.toBytes(newsSubject.getContent()));
			if(newsSubject.getType()!=null && !newsSubject.getType().equals(""))
				put.add(Bytes.toBytes("info"), Bytes.toBytes("type"), Bytes.toBytes(newsSubject.getType()));
			newsList.add(put);
//			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ "+newsList.size());
			if (newsList.size() >= 40) {
				System.out.println(Thread.currentThread().getName() + " " + "store news into hbase");
				newsInfo.put(newsList);
				newsInfo.flushCommits();
				newsList.clear();
//				logger.info("*************** store tjnews into hbase");
			}
		} catch(IOException e) {
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
		Configuration conf = HBaseConfiguration.create();
		@SuppressWarnings("resource")
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
			Configuration conf = HBaseConfiguration.create();
            @SuppressWarnings("resource")
			HBaseAdmin admin = new HBaseAdmin(conf);  
            if (!admin.tableExists(tableName)) {  
                System.out.println("表不存在, 无需进行删除操作！");  
            }else{  
                admin.disableTable(tableName);  
                admin.deleteTable(tableName);  
                System.out.println(" 表删除成功！");  
            }  
        } catch (MasterNotRunningException e) {  
            e.printStackTrace();  
        } catch (ZooKeeperConnectionException e) {  
            e.printStackTrace();  
        }  
	}
	
	public static void main(String[] args) {
		//	private HTable newsInfo;
		//private HTable newsRelated;
		StoreHBaseNews hbase = new StoreHBaseNews();
		String[] strings = {"info"};
		try {
			hbase.createTable("newsInfo", strings);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
