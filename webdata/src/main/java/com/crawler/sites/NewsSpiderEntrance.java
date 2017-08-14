package com.crawler.sites;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ACT-NJ on 2017/8/7.
 */
public class NewsSpiderEntrance {
    public static void main(String[] args) {
        NewsSpiderEntrance entrance = new NewsSpiderEntrance();
        entrance.timerAll(args[0],args[1]);
    }
    private void timerAll(String type,String env){
        Date firstTime = getFirstTime().getTime();
//		System.out.println(hour+" "+firstTime.toLocaleString());
        long period = 4*3600*1000;//4小时为间隔
        long delay = 3;    //3'延迟
        Timer spiderTimer = new Timer();
        TimerTask spiderTask;
        if(type.equals("news")){
            spiderTask =new TimerTask() {
                @Override
                public void run() {
                    runNewsCrawler();
                }
            };
        }else{
            spiderTask =new TimerTask() {
                @Override
                public void run() {
                    runCmtCrawler();
                }
            };
        }
        if(env.equals("test")){
            spiderTimer.schedule(spiderTask, delay);
        }else{
            spiderTimer.schedule(spiderTask, firstTime, period);
        }
    }

    private void runNewsCrawler(){
        new NeteaseNews().run();
        new SinaNews().run();
        new TencentNews().run();
        new CNR().run();
        new People().run();
        new Paper().run();
    }
    private void runCmtCrawler(){

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
}
