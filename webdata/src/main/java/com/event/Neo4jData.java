package com.event;

import org.apache.commons.lang3.time.DateFormatUtils;
import java.util.Date;

/**
 * Created by ACT-NJ on 2016/10/28.
 */
public class Neo4jData {
    String eventId;
    String type;
    String keywords;
    String name;
    Date time;
    String news_desc;
    String location;
    String participant;
    public Neo4jData(String eventId,String name,String news_desc,Date time,String location,String participant,int type,String keywords) {
        setID(eventId);
        setName(name);
        setNewsDesc(news_desc);
        setTime(time);
        setLoca(location);
        setPart(participant);
        setType(type);
        setCorewords(keywords);
    }
    public String getID() {
        return this.eventId;
    }
    public void setID(String id) {
        this.eventId = id;
    }
    public String getType() {
        return this.type;
    }

    public void setType(int type) {
        switch (type)
        {
            case 1: this.type="自然灾害";break;
            case 2: this.type="安全事故";break;
            case 3: this.type="环境生态";break;
            case 5: this.type="公共卫生";break;
            case 6: this.type="社会安全";break;
            case 7: this.type="政治新闻";break;
            case 8: this.type="军事新闻";break;
            case 9: this.type="社会焦点";break;
            default: this.type="其他";
        }
    }
    public String getLoca() {
        return this.location;
    }

    public void setLoca(String location) {
        this.location = location;
    }
    public String getPart() {
        return this.participant;
    }
    public void setPart(String participant) {
        this.participant = participant;
    }
    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String desc) {
        this.name = desc;
    }
    public String getNewsDesc() {
        return this.news_desc;
    }
    public void setNewsDesc(String desc) {
        this.news_desc = desc;
    }
    public String getTimeString() {
        return this.time == null?"-": DateFormatUtils.format(this.time, "yyyy-MM-dd HH:mm");
    }
    public String getCorewords() {
        return this.keywords;
    }

    public void setCorewords(String keywords) {
        this.keywords = keywords;
    }
    public String toString(){
        return "事件ID:"+eventId+
                ",标题:"+name+
                ",新闻:"+news_desc+
                ",类型:"+type+
                ",关键词:"+keywords+
                ",时间:"+getTimeString()+
                ",地点:"+location+
                ",人物:"+participant;
    }
}
