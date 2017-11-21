package com.event;

import java.util.*;

/**
 * Created by ACT-NJ on 2016/10/28.
 */
public class NeoTimer {
    public static void main(String[] args) {
        time();
    }
    private static void time() {
        long period = 8*3600*1000;    //8小时为间隔
        long delay = 30*1000;    //30'延迟
//        Calendar cal = Calendar.getInstance();
//        cal.set(2016,9,31,11,18,0);
//        Date firstTime = cal.getTime();
        Timer neoTimer = new Timer();
        TimerTask neoTask =new TimerTask() {
            @Override
            public void run() {
                ScanNewsInfo newsInfo = new ScanNewsInfo();
                ScanWeiboEvent weiboEvent = new ScanWeiboEvent();
//                NeoInserter inserter = new NeoInserter();
                Date to = new Date(new Date().getTime()-3600*1000);
                Date from = new Date(to.getTime()-3600*9*1000);
                List<RelatedEvent> events = weiboEvent.filterEventFromTo(from,to);
                newsInfo.getNewsFromES(events);
//                List<Neo4jData> neo4j = newsInfo.getNewsFromES(events);
//                printData(neo4j);
//                inserter.insert(neo4j);
            }
        };
//        neoTimer.schedule(neoTask,firstTime,period);
        neoTimer.schedule(neoTask,delay,period);
    }

    public static void printData(List<Neo4jData> neo4jDatas){
        if(neo4jDatas.size()==0){
            return;
        }
        for(Neo4jData data:neo4jDatas){
            String title = data.getNewsDesc();
            String time  = data.getTimeString();
            String par = data.getPart();
            String loc = data.getLoca();
            System.out.println(title+"#"+time+"#"+par+"#"+loc);
        }
    }

}
