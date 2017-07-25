package com.event;

/**
 * Created by ACT-NJ on 2016/10/10.
 */
import java.util.Arrays;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

public class RelatedEvent {
    String eventId;		//事件ID
    int type;
    Date time;
    String[] weiboIDs;		//相关微博MID
    String corewords;
    String detailwords;
    String desc;
    String location;
    String participant;
    public RelatedEvent(String eventId) {
        this.eventId = eventId;
    }
    public String getID() {
        return this.eventId;
    }
    public void setID(String id) {
        this.eventId = id;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
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
    public String getDesc() {
        return this.desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDateString() {
        return this.time == null?null: DateFormatUtils.format(this.time, "yyyy-MM-dd HH:mm");
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
        //有待测试，不知多少关键词合适
        if(split.length > 6) {
            split = (String[]) Arrays.copyOfRange(split, 0, 5);
        }
        this.corewords = StringUtils.join(split, " ");
    }
    public String getDetailwords() {
        return this.detailwords;
    }
    public void setDetailwords(String detailwords) {
        this.detailwords = detailwords.trim();
    }
    public String toStringJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", eventId);
        jsonObject.put("type", type);
        jsonObject.put("stringTime", time.toLocaleString());
        jsonObject.put("corewords", corewords);
        jsonObject.put("detailwords", detailwords);
        jsonObject.put("desc", desc);
        jsonObject.put("participant", participant);
        jsonObject.put("location", location);
        return jsonObject.toString();
    }
}
