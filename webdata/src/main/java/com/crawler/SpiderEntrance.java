package com.crawler;

import com.crawler.sites.*;
import com.utils.GetIndexDocs;

import java.util.*;

/**
 * Created by ACT-NJ on 2017/8/7.
 */
public class SpiderEntrance {
    public static void main(String[] args) {
        SpiderEntrance entrance = new SpiderEntrance();
        entrance.timerAll(args[0],args[1]);
    }
    private void timerAll(String type,String env){
        Date firstTime = getFirstTime().getTime();
//		System.out.println(hour+" "+firstTime.toLocaleString());
        long period = 4*3600*1000L;//4小时为间隔
        long delay = 3;    //3'延迟
        Timer spiderTimer = new Timer();
        TimerTask spiderTask = null;
        if(type.equals("news")){
            spiderTask =new TimerTask() {
                @Override
                public void run() {
                    runNewsCrawler();
                }
            };
        }else if(type.equals("cmts")){
            spiderTask =new TimerTask() {
                @Override
                public void run() {
                    runCmtCrawler();
                }
            };
        }else{
            System.out.println("sorry no task");
            return;
        }
        if(env.equals("test")){
            spiderTimer.schedule(spiderTask, delay);
        }else{
            spiderTimer.schedule(spiderTask, firstTime, period);
        }
    }

    private void runNewsCrawler(){
        new NeteaseNews().start();
        new SinaNews().start();
        new TencentNews().start();
        new CNR().start();
        new People().start();
        new Paper().start();
    }
    private void runCmtCrawler(){
        HashMap<String,List<String>> ids = getCurrentCmtIds();
        List<String> nts = ids.get("nts");
        List<String> sin = ids.get("sin");
        List<String> tct = ids.get("tct");
        System.out.println(nts.size()+" "+sin.size()+" "+tct.size());
        new SinaCmt().run(sin);
        new NeteaseCmt().run(nts);
        new TencentCmt().run(tct);
    }

    private Calendar getFirstTime(){
        Calendar calendar = Calendar.getInstance();
        int temp  = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int hour = (temp/4+1)*4;
        if(hour==24){
            calendar.add(Calendar.DATE, +1);
            hour=0;
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 35);
        calendar.set(Calendar.SECOND, 00);
        return calendar;
    }

    private HashMap getCurrentCmtIds(){
        long from = System.currentTimeMillis();
        long to = from-9*60*60*1000;
        return new GetIndexDocs().getCmtIds(to,from,"nnews");
    }
}
