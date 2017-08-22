package com.store;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import com.crawler.beans.CmtUser;
import com.crawler.beans.NewsCmt;
import com.crawler.beans.NewsItem;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;


public class CrawlerHBase {
	private static CrawlerHBase crawlerHBase = new CrawlerHBase();
	private HTable newsInfo;
	private HTable newsCmt;
	private HTable cmtUser;
	private List<NewsItem> newsList;
	private Logger logger;
	private CrawlerHBase(){
		logger =  Logger.getLogger(this.getClass());
	    registerShutdownHook();
	}
	public static CrawlerHBase getHBase(boolean comment){
		if(comment){
			try {
				crawlerHBase.newsCmt = HBaseClient.getTable("ncmts");
				crawlerHBase.cmtUser = HBaseClient.getTable("nusers");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				crawlerHBase.newsInfo = HBaseClient.getTable("nnews");
				crawlerHBase.newsList = new ArrayList<NewsItem>(70);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return crawlerHBase;
	}
	public void close(){
    	try {
    		if (newsList != null && newsList.size() != 0) {
    			storeBulkNews(newsList);
    		}
    		if(newsInfo!=null){
    			newsInfo.close();
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

	public synchronized void storeNews(NewsItem anews){
		if (newsList.size() <= 50) {
			newsList.add(anews);
		}else{
			try {
				storeBulkNews(newsList);
				newsList.clear();
				newsList.add(anews);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void storeBulkNews(List<NewsItem> newsList) throws IOException {
		List<Put> puts = new ArrayList<>(70);
		for(NewsItem news : newsList){
			Put put = new Put(Bytes.toBytes(news.getId()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("title"), Bytes.toBytes(news.getTitle()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("desp"), Bytes.toBytes(news.getDesp()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("url"), Bytes.toBytes(news.getURL()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("source"), Bytes.toBytes(news.getSource()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("time"), Bytes.toBytes(news.getStringTime()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("content"), Bytes.toBytes(news.getContent()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("keywords"), Bytes.toBytes(news.getKeywords()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("type"), Bytes.toBytes(news.getType()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("cmtid"), Bytes.toBytes(news.getCmtID()));
			puts.add(put);
		}
		newsInfo.put(puts);
		newsInfo.flushCommits();
		logger.info("=====><===== store into nnews ====><====");
		System.out.println(Thread.currentThread().getName() + " store into nnews");
	}
	public synchronized void storeBulkCmt(List<NewsCmt> cmtList,List<CmtUser> userList) {
		List<Put> cmt_puts = new ArrayList<>(70);
		List<Put> user_puts = new ArrayList<>(70);
		for(NewsCmt cmt : cmtList){
			Put put = new Put(Bytes.toBytes(cmt.getID()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("cmtid"), Bytes.toBytes(cmt.getID()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("time"), Bytes.toBytes(cmt.getStringTime()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("content"), Bytes.toBytes(cmt.getContent()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("newsid"), Bytes.toBytes(cmt.getTargetId()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("userid"), Bytes.toBytes(cmt.getUid()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("upnum"), Bytes.toBytes(cmt.getUpNum()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("cpid"), Bytes.toBytes(cmt.getPid()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("crid"), Bytes.toBytes(cmt.getRid()));
			cmt_puts.add(put);
		}
		for(CmtUser user : userList){
			Put put = new Put(Bytes.toBytes(user.getUid()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("uid"), Bytes.toBytes(user.getUid()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("nickname"), Bytes.toBytes(user.getNickname()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("region"), Bytes.toBytes(user.getRegion()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("gender"), Bytes.toBytes(user.getGender()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("avatar"), Bytes.toBytes(user.getAvatar()));
			if(user.getExtra()!=null)
				put.add(Bytes.toBytes("info"), Bytes.toBytes("extra"), Bytes.toBytes(user.getExtra()));
			user_puts.add(put);
		}
		try {
			newsCmt.put(cmt_puts);
			newsCmt.flushCommits();
			cmtUser.put(user_puts);
			cmtUser.flushCommits();
			logger.info("=====><===== store into ncmt ====><====");
		} catch (InterruptedIOException e) {
			e.printStackTrace();
		} catch (RetriesExhaustedWithDetailsException e) {
			e.printStackTrace();
		}
	}
}
