package com.crawler.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.List;

/**
 * Created by ACT-NJ on 2017/7/12.
 */
public class TencentList implements SubPageProcessor{

    @Override
    public MatchOther processPage(Page page) {
        Json jsonobject = new Json(page.getRawText());
        if(!jsonobject.jsonPath("$.response.code").toString().equals("0")){
            page.setSkip(true); //无需保存
            return MatchOther.NO;
        }
        List<String> list = jsonobject.jsonPath("$.data.article_info[*]").all();
        for (String s : list) {
            JSONObject temp = JSONObject.parseObject(s);
            if(temp.getString("column").contains("图片"))
                continue;
            Request r = new Request(temp.getString("url"));
            r.putExtra("column",(temp.getString("column")));
            r.putExtra("time",(temp.getString("time")));
            r.putExtra("title",(temp.getString("title")));
            page.addTargetRequest(r);
        }
        page.setSkip(true); //无需保存
        return MatchOther.NO;
    }

    @Override
    public boolean match(Request request) {
       return request.getUrl().matches("http://roll\\.news\\.qq\\.com/interface/\\S+");
    }
}
