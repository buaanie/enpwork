package com.crawler.beans;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ACT-NJ on 2017/7/14.
 */
public class NewsCmt {
    private String content;     //评论内容
    private long milliseTime;     //评论时间
    private String stringTime;	    //发布时间
    private String id;          //评论id
    private String target;      //评论文章的id
    private String pid = "0";         //评论上一级id
    private String rid = "0";
    private String uid;         //评论人id
    private String upNum;       //评论被点赞数

    /**
     * 构造函数
     * @param id
     * @param target
     * @param uid
     * @param upNum
     * @param time
     * @param content
     */
    public NewsCmt(String id, String target, String uid, String upNum, long time, String content)
    {
        this.id = id;
        this.target = target;
        this.uid = uid;
        this.upNum = upNum;
        this.milliseTime = time;
        this.content = content;
    }
    public NewsCmt(String id, String target, String uid, String upNum, String time, String content){
        this.id = id;
        this.target = target;
        this.uid = uid;
        this.upNum = upNum;
        this.stringTime = time;
        this.content = content;
    }
    public void setPid(String pid){
        this.pid = pid;
    }
    public void setRid(String rid){
        this.rid = rid;
    }
    public String getID() {
        return id;
    }
    public Date getDateTime() throws ParseException {
        return new Date(milliseTime);
    }
    public String getStringTime(){
        if(stringTime==null){
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            return sdf.format(new Date(milliseTime));
        }else{
            return stringTime;
        }
    }
    public String getContent() {
        return content;
    }

    public String toString(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String info = mapper.writeValueAsString(this);
            return info;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error------------>";
    }
}
