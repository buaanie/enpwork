package com.crawler.sites;

import com.alibaba.fastjson.JSONObject;
import com.crawler.beans.CmtUser;
import com.crawler.beans.NewsCmt;
import com.crawler.utils.ItemPipeLine;
import com.crawler.utils.ItemType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.crawler.utils.StirngUtil.UA1;

/**
 * Created by ACT-NJ on 2017/7/19.
 * 手机版评论页面 http://cmnt.sina.cn/index?product=comos&index=cmt_id&tj_ch=news
 * 电脑版评论页面 http://comment5.news.sina.com.cn/comment/skin/default.html?channel=_type&newsid=comos-cmt_id
 */
public class SinaCmt implements PageProcessor {
    private static final String url = "http://comment5.news.sina.com.cn/page/info?format=json&channel=%s&newsid=comos-%s&compress=1&page=%d&page_size=40";
    private Site site = Site.me().setRetryTimes(6).setCycleRetryTimes(2000).setSleepTime(1000).setUserAgent(UA1);

    public static void main(String[] args) {
        String id = "sin-fyihrit1134254-gn";
        Request cmt_req = new Request(String.format(url, id.split("-")[2], id.split("-")[1], 1)).putExtra("id", id).putExtra("page",1);
        Spider sina_cmt = Spider.create(new SinaCmt()).addRequest(cmt_req).addPipeline(new ItemPipeLine(ItemType.NewsCmt));
        sina_cmt.run();
    }

    @Override
    public void process(Page page) {
        Json json = new Json(page.getRawText());
        if (json.jsonPath("$.result.count.show").toString().equals("0")) {
            return;
        }
        String target_id = page.getRequest().getExtra("id").toString();
        List<String> list = json.jsonPath("$.result.cmntlist[*]").all();
        List<CmtUser> cmtuserList = new ArrayList<>();
        List<NewsCmt> commentsList = new ArrayList<>();
        for (String s : list) {
            Pair<NewsCmt,CmtUser> info = getCmtInfo(s,target_id);
            commentsList.add(info.getLeft());
            cmtuserList.add(info.getRight());
        }
        page.putField(ItemType.CmtUser,cmtuserList);
        page.putField(ItemType.NewsCmt,commentsList);
        int show = Integer.valueOf(json.jsonPath("$.result.count.show").toString());
        int num = (int)page.getRequest().getExtra("page");
        if(show > num*40){
            num++;
            Request next = new Request(String.format(url, target_id.split("-")[2], target_id.split("-")[1], num))
                    .putExtra("id", target_id).putExtra("page",num);
            page.addTargetRequest(next);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public Pair<NewsCmt,CmtUser> getCmtInfo(String infos,String target_id){
        JSONObject temp = JSONObject.parseObject(infos);
        String cmt_id = temp.getString("mid");
        String cmt_time = temp.getString("time");
        String cmt_content = temp.getString("content");
        String up = temp.getString("agree");
        String user_id = temp.getString("uid");
        String user_region = temp.getString("area");
        String user_nick = temp.getString("nick");
        String config = temp.getString("config");
        String user_avatar = getHead(config);
        String user_gender = "-";
        if (user_avatar.contains("_female"))
            user_gender = "2";
        else if (user_avatar.contains("_male"))
            user_gender = "1";
        NewsCmt comment = new NewsCmt("sin-" + cmt_id, target_id, user_id, up, cmt_time, cmt_content);
        String parent = temp.getString("parent");
        comment.setPid(parent.equals("")?"0":"sin-" +parent);
        CmtUser user = new CmtUser(user_id, user_nick, user_region, user_gender, user_avatar);
        user.putExtra("config",config);
        Pair<NewsCmt,CmtUser> res = ImmutablePair.of(comment,user);
        return res;
    }
    public String getHead(String config){
        String regex = "http\\S+\\.jpg";
        Matcher m = Pattern.compile(regex).matcher(config);
        if(m.find())
            return URLDecoder.decode(m.group(0));
        return "-";
    }
}
