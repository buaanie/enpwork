package com.store;


import com.crawler.beans.NewsItem;

public class StoreNews {
	private static CrawlerAllIndex crawlerNewsIndex = new CrawlerAllIndex();
	private static CrawlerAllHBase storeHBaseNews = new CrawlerAllHBase();
	public void storeNews(NewsItem news){
		try {
			crawlerNewsIndex.insertNews(news);
			storeHBaseNews.storeNews(news);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	public void closeConn(){
		crawlerNewsIndex.close();
		storeHBaseNews.close();
	}
}
