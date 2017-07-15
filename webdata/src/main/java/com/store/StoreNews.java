package com.store;


import com.crawler.beans.NewsItem;

public class StoreNews {
	private static CrawlerNewsIndex crawlerNewsIndex = new CrawlerNewsIndex();
	private static StoreHBaseNews storeHBaseNews = new StoreHBaseNews();
	public void storeNews(NewsItem news) throws Exception{
		try {
			crawlerNewsIndex.insertAllIndex(news);
			storeHBaseNews.storeNews(news);
		} catch (Exception e1) {
			System.out.println("ES异常");
			e1.printStackTrace();
		}
	}
	public void closeConn(){
		crawlerNewsIndex.close();
		storeHBaseNews.close();
	}
}
