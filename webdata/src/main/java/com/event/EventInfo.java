package com.event;

import java.io.Serializable;

public class EventInfo implements Serializable{
	private String eventId;		//事件ID
    private String time;        //时间
    private String description; //简述
    private String summary;       //事件标题
    private String location;    //事件地点
    private String participant;//参与人
    private String keywords;//关键词
    private String articleId;//新闻ids，以,隔开
    private String relatedEventId;//相关事件id
    private int etype;  //事件类型
    private int emotion;//事情情感极性
//    private int hot;//热度
    private int show;//是否显示，0为不显示，1为合并，>=2为显示 标记新闻条数
//    private String chain ="";//事件演化链，','隔开事件id
    public EventInfo(String eventId,String articleId,int show){
        this.eventId = eventId;
        this.articleId = articleId;
        this.show = show;
    }
    public String getEventId(){
        return eventId;
    }
    public String getArticleId(){
        return articleId;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return time;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }
    public String getSummary(){
        return summary;
    }
    public void setLocation(String location){
        this.location = location;
    }
    public String getLocation(){
        return location;
    }
    public void setParticipant(String participant){
        this.participant = participant;
    }
    public String getParticipant(){
        return participant;
    }
    public void setKeywords(String keywords){
        this.keywords = keywords;
    }
    public String getKeywords(){
        return keywords;
    }
    public void setRelatedEventId(String relatedEventId){
        this.relatedEventId = relatedEventId;
    }
    public void addRelatedId(String id){
        relatedEventId += id;
    }
    public String getRelatedEventId(){
        return relatedEventId;
    }
    public void setEtype(int type){
        this.etype = type;
    }
    public int getEtype(){
        return etype;
    }
    public void setEmotion(int emotion){
        this.emotion = emotion;
    }
    public int getEmotion(){
        return emotion;
    }
//    public void setHot(int hot){
//        this.hot = hot;
//    }
//    public int getHot(){
//        return hot;
//    }
    public void setShow(int show) {
        this.show = show;
    }
    public int getShow() {
        return show;
    }
//    public void addChain(String id) {
//        chain = chain+","+id;
//    }
//    public String getChain() {
//        return chain;
//    }
}
