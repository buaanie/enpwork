package com.crawler.sites;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crawler.utils.ItemPipeLine;
import com.crawler.beans.CmtUser;
import com.crawler.beans.NewsCmt;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ACT-NJ on 2017/7/14.
 */
public class TencentCmt implements PageProcessor{
    private static final String tct_cmt = "https://coral.qq.com/article/%s/comment?commentid=%s&tag=&reqnum=40";
    public static void main(String[] args) {
        //https://coral.qq.com/article/2029093517/comment?commentid=0&tag=&reqnum=20
        //https://coral.qq.com/article/2029093517/comment?commentid=6291510543052720519&tag=&reqnum=20
        //https://coral.qq.com/article/2029093517/hotcomment?source=10&commentid=0&reqnum=10&callback=__jp0
        String cmt_url = String.format(tct_cmt,"2029093517","0");
        Spider ten_cmt = Spider.create(new TencentCmt()).addUrl(cmt_url).addPipeline(new ItemPipeLine(ItemType.NewsCmt));
        ten_cmt.run();
    }
    @Override
    public void process(Page page) {
        String jsonp = page.getRawText();
        Json json = new Json(jsonp);
        int cmt_num = Integer.valueOf(json.jsonPath("$.data.total").toString());
        if(cmt_num==0)
            return;
        String target_id = json.jsonPath("$.data.targetid").toString();
        Boolean hasNext = Boolean.valueOf(json.jsonPath("$.data.hasnext").toString());//用于判断是否还有分页
        String last_id = json.jsonPath("$.data.last").toString();//记录最后一条评论id，用于拼接下一分页
        JSONArray comments = JSONArray.parseArray(json.jsonPath("$.data.commentid").toString());
        Iterator<Object> iter = comments.iterator();
        List<NewsCmt> commentsList = new ArrayList<>();
        List<CmtUser> cmtuserList = new ArrayList<>();
        while(iter.hasNext()){
            JSONObject temp = (JSONObject) iter.next();
            String cmt_id = temp.getString("id");
            String root_id = temp.getString("rootid");
            String parent_id = temp.getString("parent");
            Long time = temp.getLong("time");
            String content = temp.getString("content");
            String up = temp.getString("up");
            String user_id = temp.getJSONObject("userinfo").getString("userid");
            String user_nick = temp.getJSONObject("userinfo").getString("nick");
            String user_region = temp.getJSONObject("userinfo").getString("region");
            String user_gender = temp.getJSONObject("userinfo").getString("gender"); //1：男  2：女
            String user_head = temp.getJSONObject("userinfo").getString("head");
            NewsCmt comment = new NewsCmt(cmt_id,target_id,user_id,up,time,content);
            comment.setPid(parent_id);
            comment.setRid(root_id);
            CmtUser user = new CmtUser(user_id,user_nick,user_region,user_gender,user_head);
            commentsList.add(comment);
            cmtuserList.add(user);
        }
        page.putField(ItemType.CmtUser,cmtuserList);
        page.putField(ItemType.NewsCmt,commentsList);
        if(hasNext)
            page.addTargetRequest(new Request(String.format(tct_cmt,target_id,last_id)));
    }

    @Override
    public Site getSite() {
        return site;
    }
    private Site site = Site.me().setDomain("http://xw.qq.com").setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");

}
