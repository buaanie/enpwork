package com.crawler.beans;

import com.alibaba.fastjson.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ACT-NJ on 2017/7/25.
 */
public class Weibo {
    private String time;
    private String mid;
    private String uid;
    private String uname;
    private String pid;
    private String content;
    private String source;
    private String reposts;
    private String comments;
    private String attitudes;

    public Weibo(String mid,String time,String uid,String uname,String text,String source,String pid,String reposts,
                      String comments,String attitudes) {
        setTime(time);
        setMid(mid);
        setUid(uid);
        setUname(uname);
        setContent(text);
        setSource(source);
        setPid(pid);
        setRepo(reposts);
        setComm(comments);
        setAtti(attitudes);
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }
    public String getStrTime() {
        // .replace(":", "")
        return time.replaceAll("\\D", "");
    }
    public Date getDateTime() throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(this.getTime());
    }
    public void setRepo(String repo) {
        this.reposts = repo;
    }
    public String getRepo() {
        return reposts;
    }
    public void setComm(String comm) {
        this.comments = comm;
    }
    public String getComm() {
        return comments;
    }
    public void setAtti(String atti) {
        this.attitudes = atti;
    }
    public String getAtti() {
        return attitudes;
    }
    public String getMid() {
        return mid;
    }
    public void setMid(String id) {
        this.mid = id;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String id) {
        this.uid = id;
    }
    public String getUname() {
        return uname;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content.trim();
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String toString(){
        return "weibo：{" +
                "ID=" + mid +
                " , 时间= " + time +
                " , 发布者=" + uname +
                " , 内容='" + content +
                " ', 转发=" + reposts +
                " , 评论='" + comments +
                " , 赞'" + attitudes + '\''+
                '}';
    }
    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mid", mid);
        jsonObject.put("uid", uid);
        jsonObject.put("pid", pid);
        jsonObject.put("time", time);
        jsonObject.put("uname", uname);
        jsonObject.put("source", source);
        jsonObject.put("content", content);
        jsonObject.put("comments", comments);
        jsonObject.put("reposts", reposts);
        jsonObject.put("attitudes", attitudes);
        return jsonObject;
    }
}
