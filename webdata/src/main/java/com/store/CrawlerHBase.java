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


public class CrawlerHBase {
	private static CrawlerHBase crawlerHBase = new CrawlerHBase();
	private HTable newsInfo;
	private HTable newsCmt;
	private HTable cmtUser;
	private List<Put> newsList;
	private List<Put> cmtsList;
	private List<Put> userList;
	private Logger logger;
	private CrawlerHBase(){
	    logger =  Logger.getLogger(this.getClass());
	    registerShutdownHook();
	}
	public static CrawlerHBase getHBase(int i){
		try {
			switch (i) {
				case 1:
					crawlerHBase.newsInfo = HBaseClient.getTable("nnews");
					crawlerHBase.newsList = new ArrayList<Put>();
					break;
				case 2:
					crawlerHBase.newsCmt = HBaseClient.getTable("ncmt");
					crawlerHBase.cmtsList = new ArrayList<Put>();
					crawlerHBase.cmtUser = HBaseClient.getTable("nuser");
					crawlerHBase.userList = new ArrayList<Put>();
					break;
			}
			} catch (IOException e) {
			e.printStackTrace();
		}
		return crawlerHBase;
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
			if (cmtsList != null && cmtsList.size() != 0) {
				newsCmt.put(cmtsList);
				newsCmt.flushCommits();
				cmtsList.clear();
			}
			if(newsCmt!=null){
				newsCmt.close();
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
			if (newsList.size() >= 40) {
				System.out.println(Thread.currentThread().getName() + " " + "store news into hbase");
				newsInfo.put(newsList);
				newsInfo.flushCommits();
				newsList.clear();
				logger.info("=====><===== store into hbase ====><====");
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
