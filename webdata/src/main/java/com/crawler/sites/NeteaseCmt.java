package com.crawler.sites;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crawler.beans.CmtUser;
import com.crawler.beans.NewsCmt;
import com.crawler.utils.ItemPipeLine;
import com.crawler.utils.ItemType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.crawler.utils.StirngUtil.UA2;

/**
 * Created by ACT-NJ on 2017/7/18.
 * 手机版评论页面 http://3g.163.com/touch/comment.html?docid=CPI6A4LQ000187VE
 */
public class NeteaseCmt implements PageProcessor{
    private Site site = Site.me().setRetryTimes(3).setCycleRetryTimes(1000).setSleepTime(2000).setUserAgent(UA2);
    private static final String api_url = "http://comment.news.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/%s/comments/newList?offset=%d&limit=40&headLimit=3&tailLimit=1&ibc=newswap";

    public static void main(String[] args) {
        String  news_id = "CPI6A4LQ000187VE";
        String cmt_url = String.format(api_url,news_id,0);
        Spider net_cmt = Spider.create(new NeteaseCmt()).addRequest(new Request(cmt_url).putExtra("id",news_id)).addPipeline(new ItemPipeLine(ItemType.NewsCmt));
        net_cmt.run();
    }
    @Override
    public void process(Page page) {
        Json json = new Json(page.getRawText());
        int cmt_num = Integer.valueOf(json.jsonPath("$.newListSize").toString());
        if(cmt_num==0)
            return;
        String target_id = page.getRequest().getExtra("id").toString();
        JSONArray comments = JSONArray.parseArray(json.jsonPath("$.comments").toString());
        Iterator<Object> iter = comments.iterator();
        List<CmtUser> cmtuserList = new ArrayList<>();
        HashMap<String,NewsCmt> commentsSet = new HashMap<>();
        while(iter.hasNext()){
            JSONObject temp = (JSONObject) iter.next();
            String cmt_id = "nts-"+temp.getString("commentId");
            Long time = temp.getLong("createTime");
            String content = temp.getString("content");
            String up = temp.getString("vote");
            JSONObject _user = temp.getJSONObject("user");
            String user_id = _user.getString("userId");
            String user_nick = _user.getString("nickname");
            String user_region = _user.getString("location");
            String user_gender = "-";
            String user_avatar = _user.getString("avatar");
            NewsCmt comment = new NewsCmt(cmt_id,target_id,user_id,up,time,content);
            CmtUser user = new CmtUser(user_id,user_nick,user_region,user_gender,user_avatar);
            commentsSet.put(cmt_id,comment);
            cmtuserList.add(user);
        }

        String levels = json.jsonPath("$.commentIds").toString();
        Pattern pattern = Pattern.compile("\"(.+?)\"");
        Matcher matcher = pattern.matcher(levels);
        while(matcher.find()){
            String level = matcher.group(1);
            String[] ids = level.split(",");
            if(ids.length<2)
                continue;
            for(int i = 1;i<ids.length;i++){
                commentsSet.get(ids[i]).setPid(ids[i-1]);
                commentsSet.get(ids[i]).setRid(ids[0]);
            }
        }
        List<NewsCmt> commentsList = new ArrayList<>();
        Iterator<NewsCmt> iterator = commentsSet.values().iterator();
        while(iterator.hasNext())
            commentsList.add(iterator.next());
        page.putField(ItemType.CmtUser,cmtuserList);
        page.putField(ItemType.NewsCmt,commentsList);

        Boolean hasNext = false;
        Matcher m = Pattern.compile("offset=(\\d+)").matcher(page.getUrl().toString());
        if(m.find()){
            int offset = Integer.valueOf(m.group(1));
            if(offset<cmt_num){
                String page_next = String.format(api_url,target_id,offset+30);
                page.addTargetRequest(new Request(page_next).putExtra("id",target_id));
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}