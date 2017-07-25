package com.event;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

public class EventInfo {
	String isCrawled;	//是否需要爬取
	String eventId;		//事件ID
    String mergedEvents = "";
    Date time;
    long timeSpan = 0L;
    String corewords;
    String[] chain;			//演化链
    String chainRid;
    String[] weiboIDs;		//相关微博MID
    int chainLen;
    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    public String getDateString() {
        return this.time == null?null: DateFormatUtils.format(this.time, "yyyy-MM-dd HH:mm");
    }
    public String getChainRid() {
        return this.chainRid;
    }

    public void setChainRid(String chainRid) {
        this.chainRid = chainRid;
    }
    public String getCrawled() {
    	return this.isCrawled;
    }
    
    public void setCrawler(String isCrawled) {
    	this.isCrawled = isCrawled;
    }
    public EventInfo(String eventId) {
        this.eventId = eventId;
        this.mergedEvents = this.mergedEvents + eventId;
    }
    public String[] getChain() {
        return this.chain;
    }

    public void setChain(String[] chain) {
        this.chain = chain;
    }
    public String[] getWeiboID() {
        return this.weiboIDs;
    }

    public void setWeiboID(String[] weiboID) {
        this.weiboIDs = weiboID;
    }
    public String getCorewords() {
        return this.corewords;
    }

    public void setCorewords(String corewords) {
        String[] split = corewords.trim().split(" ");
        if(split.length > 6) {
            split = (String[]) Arrays.copyOfRange(split, 0, 5);
        }

        this.corewords = StringUtils.join(split, " ");
    }
    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public int getChainLen() {
        return this.chainLen;
    }

    public void setChainLen(String chainLen) {
        this.chainLen = Integer.parseInt(chainLen) ;
    }
    public String getMergedEvents() {
        return this.mergedEvents;
    }

    public void setMergedEvents(String eventsString) {
        this.mergedEvents = eventsString;
    }
}
