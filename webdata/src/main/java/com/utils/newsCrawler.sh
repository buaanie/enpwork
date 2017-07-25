echo '123'
cd /root/niej/NewsCrawler
nohup mvn exec:java -Dexec.mainClass=com.news.crawler.NeteaseCrawler >/dev/null 2>news.log &

nohup mvn exec:java -Dexec.mainClass=com.news.crawler.SinaCrawler >/dev/null 2>news.log &

nohup mvn exec:java -Dexec.mainClass=com.news.crawler.PaperCrawler >/dev/null 2>news.log &
