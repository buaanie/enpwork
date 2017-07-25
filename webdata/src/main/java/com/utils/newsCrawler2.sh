echo '123'
cd /root/niej/NewsCrawler
nohup /root/huanghf/jdk1.7.0_55/bin/java -Xms128m -Xmx512m -classpath /root/huanghf/maven-3.1.1/boot/plexus-classworlds-2.5.1.jar -Dclassworlds.conf=/root/huanghf/maven-3.1.1/bin/m2.conf -Dmaven.home=/root/huanghf/maven-3.1.1 org.codehaus.plexus.classworlds.launcher.Launcher exec:java -Dexec.mainClass=com.news.crawler.PaperCrawler -Dexec.cleanupDaemonThreads=false >/dev/null 2>./crawlerLog/news.log &
sleep 45
nohup /root/huanghf/jdk1.7.0_55/bin/java -Xms128m -Xmx512m -classpath /root/huanghf/maven-3.1.1/boot/plexus-classworlds-2.5.1.jar -Dclassworlds.conf=/root/huanghf/maven-3.1.1/bin/m2.conf -Dmaven.home=/root/huanghf/maven-3.1.1 org.codehaus.plexus.classworlds.launcher.Launcher exec:java -Dexec.mainClass=com.news.crawler.NeteaseCrawler -Dexec.cleanupDaemonThreads=false >/dev/null 2>./crawlerLog/news.log &
sleep 85
nohup /root/huanghf/jdk1.7.0_55/bin/java -Xms128m -Xmx512m -classpath /root/huanghf/maven-3.1.1/boot/plexus-classworlds-2.5.1.jar -Dclassworlds.conf=/root/huanghf/maven-3.1.1/bin/m2.conf -Dmaven.home=/root/huanghf/maven-3.1.1 org.codehaus.plexus.classworlds.launcher.Launcher exec:java -Dexec.mainClass=com.news.crawler.SinaCrawler -Dexec.cleanupDaemonThreads=false >/dev/null 2>./crawlerLog/news.log &
