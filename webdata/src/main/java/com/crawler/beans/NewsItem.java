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
    private String comment_url;     //新闻评论页面

	public NewsItem(){
		
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
     * @param keywords
     */
	public NewsItem(String id, String url, String title, String content, String time, String source, String type, String desc, String keywords)
	{
		this.id = id;
		this.url = url;
		this.stringTime = time;
		this.title = title;
		this.content = content.matches("\\s*")? title:content;
		this.source = source;
		this.type = type;
		this.description = desc;
		this.keywords = keywords;
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
    	DateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm", Locale.CHINA);
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
    public void setComment(String url){
	    this.comment_url= url;
    }
    public String getCommentUrl(){
        return this.comment_url;
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            String info = mapper.writeValueAsString(this.clone());
            return info;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return "error------------>";
    }
}