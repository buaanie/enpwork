package com.crawler.beans;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by ACT-NJ on 2017/7/14.
 */
public class NewsCmt {
    private String content;     //评论内容
    private long milliseTime;     //评论时间
    private String id;          //评论id
    private String target;      //评论文章的id
    private String pid;         //评论上一级id
    private String rid;
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
        try {
            return getDateTime().toLocaleString();
        } catch (ParseException e) {
            return "";
        }
    }
    public String getContent() {
        return content;
    }

    public String toString(){
        return "评论：{" + "id= "+ id+ " 回复='" + content +
                "', 时间='" + getStringTime()+"'}";
    }
}
