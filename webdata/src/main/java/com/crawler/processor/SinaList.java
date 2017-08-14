package com.crawler.processor;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.RequestMatcher;
import us.codecraft.webmagic.handler.SubPageProcessor;

import java.util.Date;
import java.util.Iterator;

import static com.crawler.utils.StirngUtil.parseJsonp;

/**
 * Created by ACT-NJ on 2017/7/19.
 */
public class SinaList implements SubPageProcessor {

    @Override
    public MatchOther processPage(Page page) {
        System.out.println(page.getUrl().toString());
        String raw = page.getRawText();
        JSONObject json = parseJsonp(raw,"{");
        JSONArray jsonArray = json.getJSONArray("list");
        for (Object o : jsonArray) {
            JSONObject temp = (JSONObject) o;
            Request r = new Request(temp.getString("url"));
            r.setCharset("utf-8");
            r.putExtra("title",temp.getString("title"));
            r.putExtra("time",temp.getString("time"));
            r.putExtra("type",getType(temp.getJSONObject("channel").getString("id")));
            System.out.println(r.getUrl());
            page.addTargetRequest(r);
        }
        page.setSkip(true); //无需保存
        return MatchOther.NO;
    }

    private String getType(String input){
        switch (input){
            case "90":return "国内-gn";
            case "91":return "国际-gj";
            case "92":return "社会-sh";
            case "93":return "军事-jc";
            default: return "国内-gn";
        }
    }

    @Override
    public boolean match(Request request) {
        return request.getUrl().matches("http://roll\\.news\\.sina\\.com\\.cn/\\S+");
    }
}
