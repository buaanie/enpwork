package com.ansj.ner;

/**
 * Created by ACT-NJ on 2017/2/12.
 */
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.utils.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Boson {
    private static final String SENTIMENT_URL = "http://api.bosonnlp.com/sentiment/analysis"; //情感
    private static final String NER_URL = "http://api.bosonnlp.com/ner/analysis"; //命名实体识别
    private static final String DEPPARSER_URL = "http://api.bosonnlp.com/depparser/analysis"; //依存文法分析
    private static final String KEYWORDS_URL = "http://api.bosonnlp.com/keywords/analysis"; //命名实体识别
    private static final String CLASSIFY_URL = "http://api.bosonnlp.com/classify/analysis"; //分类
    private static final String TAG_URL = "http://api.bosonnlp.com/tag/analysis?space_mode=0&oov_level=3&t2s=0&&special_char_conv=0"; //分词标注
    private static final String TIME_URL = "http://api.bosonnlp.com/time/analysis"; //时间转换
    private static final String SUMMARY_URL = "http://api.bosonnlp.com/summary/analysis"; //摘要
    private static final String TOKEN = "j2s5_TZW.11043.b2KisCMmDNe8";
    private static HashMap<String,String> urls = new HashMap<>();
    static {
        urls.put("senti",SENTIMENT_URL);
        urls.put("ner",NER_URL);
        urls.put("deppar",DEPPARSER_URL);
        urls.put("keywords",KEYWORDS_URL);
        urls.put("classify",CLASSIFY_URL);
        urls.put("summary",SUMMARY_URL);
        urls.put("tag",TAG_URL);
        urls.put("time",TIME_URL);
    }

    public static void main(String[] args) {
        List<String> text = new ArrayList<>();
        text.add("我们都爱北京天安门");
        text.add("张顺你是傻逼么");
        text.add("让我一个人静一静");
        text.add("天津开展走基层活动");
        text.add("希望你们好好反省");
        text.add("希望你们好好改进");
        text.add("天津市委向大家拜年，天津政协向大家拜年，天津市委祝大家中秋快乐");
        text.add("你好吗");
        text.add("大家好");
        Boson b = new Boson();
        b.nerByBoson(text);
    }
    private JSONArray bosonNLP(List<String> input,String func){
        // 注意每次最多post 100条新闻进行分析
        String body = new JSONArray(input).toString();
        JSONArray resJson = new JSONArray();
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(urls.get(func))
                    .header("Accept", "application/json")
                    .header("X-Token", TOKEN)
                    .body(body)
                    .asJson();
            resJson = jsonResponse.getBody().getArray();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resJson;
    }

    public void nerByBoson(List<String> input){
        JSONArray resJson = bosonNLP(input,"ner");
        for (Object o : resJson) {
            JSONObject item = (JSONObject) o;
            List<Pair> result = nerArray(item);
            for (Pair pair : result) {
                System.out.println(pair.getId()+" :"+pair.getText());
            }
        }
    }

    public void nerByBoson(List<String> input,HashMap<String,Integer> participantMap,HashMap<String,Integer> locationMap,HashMap<String,Integer> timeMap){
        JSONArray resJson = bosonNLP(input,"ner");
//        HashMap<String,Integer> participantMap = new HashMap<>();
//        HashMap<String,Integer> locationMap = new HashMap<>();
//        HashMap<String,Integer> timeMap = new HashMap<>();

        for (Object o : resJson) {
            JSONObject item = (JSONObject) o;
            List<Pair> result = nerArray(item);
            for (Pair pair : result) {
                if(pair.getId().equals("time")){
                    if(!timeMap.containsKey(pair.getText()))
                        timeMap.put(pair.getText(),0);
                    else{
                        timeMap.put(pair.getText(),timeMap.get(pair.getText())+1);
                    }
                }else if(pair.getId().equals("location")){
                    if(!locationMap.containsKey(pair.getText()))
                        locationMap.put(pair.getText(),0);
                    else{
                        locationMap.put(pair.getText(),locationMap.get(pair.getText())+1);
                    }
                }else{
                    //"person_name","org_name","company_name","product_name","job_title"
                    if(!participantMap.containsKey(pair.getText()))
                        participantMap.put(pair.getText(),0);
                    else{
                        participantMap.put(pair.getText(),participantMap.get(pair.getText())+1);
                    }
                }
            }
        }
//        return participantMap;
    }
    /*
    仅针对NER，输入一个解析结果json，输出其中的ne
     */
    private List<Pair> nerArray(JSONObject input){
        JSONArray entitys = input.getJSONArray("entity");
        JSONArray words = input.getJSONArray("word");
        List<Pair> result = new ArrayList<>();
        for (Object e : entitys) {
            JSONArray entity = (JSONArray) e;
            int start = entity.getInt(0);
            int end = entity.getInt(1);
            StringBuilder sb = new StringBuilder();
            for(int i= start;i<end;i++){
                sb.append(words.getString(i));
            }
//            System.out.println(entity.getString(2)+" : "+sb.toString());
            result.add(new Pair(entity.getString(2),sb.toString()));
        }
        return result;
    }
}
