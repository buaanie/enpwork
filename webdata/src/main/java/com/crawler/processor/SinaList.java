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
        String raw = page.getRawText();
        JSONObject json = parseJsonp(raw,"{");
        JSONArray jsonArray = json.getJSONArray("list");
        for (Object o : jsonArray) {
            JSONObject temp = (JSONObject) o;
            Request r = new Request(temp.getString("url"));
            r.putExtra("title",temp.getString("title"));
            r.putExtra("time",temp.getString("time"));
            r.putExtra("type",temp.getJSONObject("channel").getString("title"));
            page.addTargetRequest(r);
        }
        page.setSkip(true); //无需保存
        return MatchOther.NO;
    }

    @Override
    public boolean match(Request request) {
        return request.getUrl().matches("http://roll\\.news\\.sina\\.com\\.cn/\\S+");
    }
}