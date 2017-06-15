package com.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class NewsSubject {
	TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
	private String title;		//新闻标题
	private String description;		//新闻简介
	private String content;		//新闻内容
	private Date dateTime;		//发布时间
	private String stringTime;	//发布时间
	private String id;			//新闻ID
	private String url;			//新闻链接
	private String source;		//新闻来源
	private String type;		//新闻类型
	private String keywords;		//新闻类型
	public NewsSubject(){
		
	}

    /**
     * 构造函数
     * @param id
     * @param url
     * @param title
     * @param content
     * @param time
     * @param source
     * @param type
     * @param desc
     */
	public NewsSubject(String id, String url, String title, String content, String time,String source,String type,String desc)
	{
		this.id = id;
		this.url = url;
		this.stringTime = time;
		this.title = title;
		this.content = content.matches("\\s*") ? title:content;
		this.source = source;
		this.type = type;
		this.description = desc;
	}
    public String getSource() {
        return source;
    }
    public String getTitle() {
        return title;
    }
    public String getType() {
        return type;
    }
    public Date getDateTime() throws ParseException {
    	DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.parse(this.stringTime);
    }
    public String getStringTime() {
        return stringTime;
    }
    public String getId() {
        return id;
    }
    public String getURL() {
        return url;
    }
    public String getContent() {
        return content;
    }
    public String getDesp() {
        return description;
    }
    public void setKeyWords(String words) {
	    this.keywords = words;
    }
    public String getKeywords() {
        return keywords;
    }
    public String toString(){
    	return "新闻：{" + "id= "+ id+ " 标题='" + title +
    	    "', 时间='" + stringTime + "', 链接='" + url +
    	    "', 来源='" + source + "', 内容='" + content + "'}";
    	}
    public String toStringJson(){

    	return null;
    }
}
