package com.crawler.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;

import java.util.Iterator;
import java.util.List;

import static com.crawler.utils.StirngUtil.parseJsonp;

/**
 * Created by ACT-NJ on 2017/7/17.
 */
public class NeteaseList implements SubPageProcessor {
//    private final String req_url ="http://3g.163.com/news/article/%s.html";
    private final String mark_url ="http://news.163.com/%s";
    @Override
    public MatchOther processPage(Page page) {
        JSONObject jsonObject = parseJsonp(page.getRawText(),"(");
        JSONArray jsonArray = jsonObject.getJSONArray(page.getRequest().getExtra("identity").toString());
        for (Object o : jsonArray) {
            JSONObject temp = (JSONObject) o;
            if(temp.containsKey("skipType"))
                continue;
//            Request r = new Request(String.format(req_url,temp.getString("docid")));
            String tail = temp.getString("url").replace("http://3g.163.com/news/","");
            Request r = new Request(String.format(mark_url,tail));
            r.putExtra("type",page.getRequest().getExtra("type"));
            r.putExtra("id",temp.getString("docid"));
            r.putExtra("source",temp.getString("source"));
            r.putExtra("time",temp.getString("ptime"));
            r.putExtra("title",temp.getString("title"));
            page.addTargetRequest(r);
        }
        page.setSkip(true); //无需保存
        return MatchOther.NO;
    }

    @Override
    public boolean match(Request request) {
        return request.getUrl().matches("http://3g\\.163\\.com/touch/\\S+");
    }
}
