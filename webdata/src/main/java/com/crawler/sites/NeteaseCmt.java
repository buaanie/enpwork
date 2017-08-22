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

import java.util.*;
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
        String  news_id = "nts-CSDMLO5N0001899N";
        Request cmt_req = new Request(String.format(api_url,news_id.split("-")[1],0)).putExtra("id",news_id);
        Spider net_cmt = Spider.create(new NeteaseCmt()).addRequest(cmt_req).addPipeline(new ItemPipeLine(ItemType.NewsCmt));
        net_cmt.run();
    }

    public void run(List<String> ids){
        List<Request> requests = new ArrayList<>();
        for (String id : ids) {
            Request cmt_req = new Request(String.format(api_url,id.split("-")[1],0)).putExtra("id",id);
            requests.add(cmt_req);
        }
        Spider net_cmt = Spider.create(new NeteaseCmt()).startRequest(requests).addPipeline(new ItemPipeLine(ItemType.NewsCmt)).thread(2);
        net_cmt.run();
    }
    @Override
    public void process(Page page) {
        Json json = new Json(page.getRawText());
        int cmt_num = Integer.valueOf(json.jsonPath("$.newListSize").toString());
        if(cmt_num==0) {
            page.setSkip(true);
            return;
        }
        String target_id = page.getRequest().getExtra("id").toString();
        JSONObject comments = JSONObject.parseObject(json.jsonPath("$.comments").toString());
        Iterator<String> iter = comments.keySet().iterator();
        List<CmtUser> cmtuserList = new ArrayList<>();
        HashMap<String,NewsCmt> commentsSet = new HashMap<>();
        int count = 0;
        while(iter.hasNext()){
            JSONObject temp = comments.getJSONObject(iter.next());
            String cmt_id = temp.getString("commentId");
            Long time = temp.getLong("createTime");
            String content = temp.getString("content");
            String up = temp.getString("vote");
            JSONObject _user = temp.getJSONObject("user");
            String user_id = _user.getString("userId");
            if(user_id.equals("0"))
                user_id= "nts-t"+System.currentTimeMillis();
            String user_nick = String.valueOf(_user.getOrDefault("nickname","-"));
            String user_region = String.valueOf(_user.getOrDefault("location","-"));
            String user_gender = "-";
            String user_avatar = String.valueOf(_user.getOrDefault("avatar","-"));
            NewsCmt comment = new NewsCmt("nts-"+cmt_id,target_id,user_id,up,time,content);
            CmtUser user = new CmtUser(user_id,user_nick,user_region,user_gender,user_avatar);
            commentsSet.put(cmt_id,comment);
            cmtuserList.add(user);
            count++;
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
                commentsSet.get(ids[i]).setPid("nts-"+ids[i-1]);
                commentsSet.get(ids[i]).setRid("nts-"+ids[0]);
            }
        }
        List<NewsCmt> commentsList = new ArrayList<>();
        Iterator<NewsCmt> iterator = commentsSet.values().iterator();
        while(iterator.hasNext())
            commentsList.add(iterator.next());
        page.putField(ItemType.CmtUser,cmtuserList);
        page.putField(ItemType.NewsCmt,commentsList);

        Matcher m = Pattern.compile("offset=(\\d+)").matcher(page.getUrl().toString());
        if(m.find()){
            int offset = Integer.valueOf(m.group(1));
            if(offset+count<cmt_num){
                String page_next = String.format(api_url,target_id.split("-")[1],offset+count);
                page.addTargetRequest(new Request(page_next).putExtra("id",target_id));
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
