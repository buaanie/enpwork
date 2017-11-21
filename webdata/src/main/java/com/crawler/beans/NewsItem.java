package com.crawler.beans;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NewsItem {

    private String title;           //新闻标题
    private String description;		//新闻简介，简述，可用首段代替
    private String content;		    //新闻内容
    private String stringTime;	    //发布时间
    private String id;			    //新闻ID
    private String url;			    //新闻链接
    private String source;		    //新闻来源，原始来源
    private String type;		    //新闻类型，如国内、国际、教育、新闻 etc
    private String keywords;		//新闻类型
    private String cmt_id = "";     //新闻评论页面ID
    private int isHot = 0;          //标记是否需要爬取评论
    /**
     * 构造函数
     * @param id 新闻ID
     * @param url   新闻链接
     * @param title 新闻标题
     * @param content   新闻正文
     * @param time  时间
     * @param source    来源
     * @param type  类型
     * @param desp  简述
     * @param keywords  关键词
     */
	public NewsItem(String id, String url, String title, String content, String time, String source, String type, String desp, String keywords)
	{
		this.id = id;
		this.url = url;
		this.stringTime = time;
		this.title = title;
		this.content = content.matches("\\s*")? title:content;
		this.source = source;
		this.type = type;
		this.description = desp;
		this.keywords = keywords;
	}
	public NewsItem(){
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Date getDateTime() throws ParseException {
    	DateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm", Locale.CHINA);
        return sdf.parse(this.stringTime);
    }
    public String getStringTime() {
        return stringTime;
    }
    public String getClearTime() throws ParseException {
        DateFormat sdf = new SimpleDateFormat ("yyyyMMddHH", Locale.CHINA);
        return sdf.format(getDateTime());
    }
    public void setStringTime(String stringTime) {
        this.stringTime = stringTime;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getURL() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDesp() {
        return description;
    }
    public void setDesp(String description) {
        this.description = description;
    }
    public String getKeywords() {
        return keywords.replace("[","").replace("]","");
    }
    public void setKeyWords(String words) {
	    this.keywords = words;
    }
    public void setCmtID(String cmtid){
	    this.cmt_id= cmtid;
    }
    public String getCmtID(){
        return this.cmt_id;
    }
    public int getHot() {
        return isHot;
    }
    public void setHot(int hot) {
        this.isHot = hot;
    }
    public String toString(){
    	return "新闻：{" + "id= '"+ id+ "' 标题='" + title +
    	    "', 时间='" + stringTime + "'}";
    	}
    public String toDeatailString(){
    	return "新闻：{" + "id= '"+ id+ "' 标题='" + title +
    	    "', 时间='" + stringTime + "', 链接='" + url +
    	    "', 来源='" + source + "', 内容='" + content + "'}";
    	}
    public String toJsonString(){
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
