package com.ansj.summary;

import com.ansj.ner.Boson;
import com.ansj.seg.AnsjSeg;
import com.crawler.beans.NewsItem;
import com.event.EventInfo;
import com.utils.GetTableRows;
import com.utils.Pair;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by stcas on 2018/1/26.
 */
public class NewsSummary {
    private static Map<String, String> staticLocation;
    private static Map<String, String> staticLocationMap;
    private static Map<String, String> matchLocation;
    private static AnsjSeg ansjSeg = new AnsjSeg();
    private static GetTableRows getHBase = new GetTableRows();
    private static Boson bosonNLP = new Boson();
//    private static Logger logger = LoggerFactory.getLogger(NewsSummary.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");;
    static{
        InputStream in = NewsSummary.class.getResourceAsStream("./files/pos.txt");
        InputStream match = NewsSummary.class.getResourceAsStream("./files/match.txt");
        try {
            List<String> lines = IOUtils.readLines(in, "utf-8");
            List<String> pairs = IOUtils.readLines(match, "utf-8");
            staticLocation = newHashMap();
            staticLocationMap = newHashMap();
            matchLocation = newHashMap();
            for (String string : lines) {
                String fullLoc = GetGPS.getFullLoc(string)[0];
                staticLocation.put(string, string);
                staticLocationMap.put(string, fullLoc);
                String city = string.replaceAll("市", "");
                if (!string.equals(city) && (city.length() > 1)) {
                    staticLocation.put(city, string);
                    staticLocationMap.put(city, fullLoc);
                }
                String country = string.replaceAll("县", "");
                if (!string.equals(country) && (country.length() > 1)) {
                    staticLocation.put(country, string);
                    staticLocationMap.put(country, fullLoc);
                }
            }
            String[] temp;
            for (String string : pairs) {
                temp = string.split(" ");
                matchLocation.put(temp[0], temp[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //根据事件相关新闻id，获取新闻内容
    public void analysisEvent2Step(List<EventInfo> events){
        HashMap<String,Integer> participant = new HashMap<>();
        HashMap<String,Integer> location = new HashMap<>();
        HashMap<String,Integer> time = new HashMap<>();
        for (EventInfo event : events) {
            //首先获取相关事件文章id ，new NewsItem(id,_title,_content,_time,_source,_type,_keywords)
            List<String> articals = Arrays.asList(event.getArticleIds().split(","));
            List<NewsItem> news = getHBase.getNews4Event(articals);
            List<String> contents = new ArrayList<>();
            for (NewsItem newsItem : news) {
                if(!newsItem.getContent().isEmpty())
                    contents.add(newsItem.getContent());
            }
            //进而分析这些新闻
             bosonNLP.nerByBoson(contents,participant,location,time); //获取命名实体，包括人物机构、时间、地点

        }
    }
    /**
     * 文本情感 高兴1 愤怒2 悲伤3
     * TODO 针对评论做情感分析
     */
    private static int getTextSentiment(String text) throws Exception {
        String[] cmdArray = {
                "python",
                "src/main/python/triple_classifier/triple_sentiment_classifier.py",
                text};
        ProcessBuilder builder = new ProcessBuilder();
        builder.redirectError(ProcessBuilder.Redirect.PIPE);
        builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        builder.redirectInput(ProcessBuilder.Redirect.PIPE);
        builder.command(cmdArray);

        final Process pr = builder.start();

        String line = "";

        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        if ((line = in.readLine()) != null) {
            System.out.println("!!!!!!");
        } else {
            line = "0";
        }
        in.close();
        pr.waitFor();
        return Integer.parseInt(line);
    }

    /*
    extract participant and location from topic
    */
    public static Pair extractFromTopic(String topicString) {  //标题中地点参与人优先提取
        HashMap<String, Integer> participantMap = new HashMap<>();
        HashMap<String, Integer> locCount = new HashMap<String, Integer>();
        String cutCmt = "";
        cutCmt = ansjSeg.ansjSeg(topicString, 1);
        String[] cmtWords = cutCmt.split("\\s+");
        String lastLocation = " ";
        for (String wordPosStr : cmtWords) {
            String[] wordPosPair = wordPosStr.split("/");
            if (wordPosPair.length < 2) {
                continue;
            } else if (wordPosPair[0].length() > 1) {
                if (wordPosPair[1].equals("nr") || wordPosPair[1].equals("nt")) {// 人名或者机构团体名
                    // LOG.info("NR or NT：" + wordPosPair[0]);
                    if (participantMap.containsKey(wordPosPair[0])) {
                        participantMap.put(wordPosPair[0],
                                participantMap.get(wordPosPair[0]) + 1);
                    } else {
                        participantMap.put(wordPosPair[0], 1);
                    }
                }
            }
        }
        for (String wordPosStr : cmtWords) {
            String[] wordPosPair = wordPosStr.split("/");
            if (wordPosPair.length < 2) {
                continue;
            } else if (wordPosPair[0].length() > 1) {
                if (wordPosPair[1].equals("n")
                        || wordPosPair[1].equals("nt")
                        || wordPosPair[1].equals("b")
                        || wordPosPair[1].equals("ns")) { //地点转换
                    if (matchLocation.containsKey(wordPosPair[0])) {
                        wordPosPair[0] = matchLocation.get(wordPosPair[0]);
                        wordPosPair[1] = "ns";
                    }
                }
                if (wordPosPair[1].equals("ns")
                        || wordPosPair[1].equals("nsf")) { //地名
                    if (staticLocation.containsKey(wordPosPair[0])) {
                        lastLocation = extractLocation(locCount, lastLocation, wordPosPair[0], 0);
                    }
                }
            }
        }

        ArrayList<String> participantList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : participantMap.entrySet()) {
            int i = 0;
            for (; i < participantList.size(); i++) {
                if (participantMap.get(participantList.get(i)) < entry.getValue()) {
                    break;
                }
            }
            participantList.add(i, entry.getKey());
        }
        String participants = "";
        for (int i = 0; (i < participantList.size()) && (i < 5); i++) {
            participants += participantList.get(i) + " ";
        }
        String location = chooseLocation(locCount);
        return new Pair(participants,location);
    }

    private static String extractLocation(HashMap<String, Integer> locCount, String lastLocation, String location, int weight) {
        if (staticLocation.containsKey(location)) {
            boolean flag = false;
            if (location.contains("市") || location.contains("县")) {
                flag = true;//提高权重
            }
            String locationMapVal = staticLocation.get(location);

            Integer count = locCount.get(locationMapVal);
            if (count == null) {
                count = weight;
            }
            count += 1;
            if (staticLocationMap.get(locationMapVal).contains(lastLocation)) {
                count += 1;
            }
            if (flag) {
                count += 1;
            }
            locCount.put(locationMapVal, count);
            lastLocation = location;
        }
        return lastLocation;
    }
    private static String chooseLocation(HashMap<String, Integer> locCount) {
        String weiboLoc = "";
        if (locCount.size() > 0) {
            List<Map.Entry<String, Integer>> locsCount = new ArrayList<Map.Entry<String, Integer>>(locCount.entrySet());
            Collections.sort(locsCount, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return -o1.getValue().compareTo(o2.getValue());
                }
            });
            weiboLoc = locsCount.get(0).getKey();
        }
        return weiboLoc;
    }
    /*
    获得一篇文章的关键词
    ！！！关键词可能出现重复如推荐/推荐人选，可考虑去重
 */
    public String getKeywords(String context){
        String corewords = "";
//        corewords = NlpirCLibrary.instance.NLPIR_GetKeyWords(context, 15,false);
        String []words=corewords.split("#");
        int n=5;
        if(words.length<n) n=words.length;
        if(n == 0)return "";
        String result=words[0];
        for(int i=1;i<n;i++) result+=" "+words[i];
//        System.out.println("Key:" + result);
        return result;
    }

    /*
    * 根据一段文字的##或【】获取其中的现有标题
    */
    public static String detectTopic(String text) {
//        List<String> rs = Lists.newArrayList();
        int maxLength = 4;
        String result = "";
        String N = "#([^#]*)#";
        String M = "【([^【】]*)】";
        String MN = N + "|" + M;
        Pattern p = Pattern.compile(MN);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String s = m.group();
            s = s.substring(1, s.length() - 1);
            s = s.replace("#", " ").trim();
            if (s.length() > maxLength) {
                maxLength = s.length();
                result = s;
            }
        }
        return result;
    }
}
