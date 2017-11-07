package com.ansj.vec;

import com.event.EventInfo;
import com.store.CrawlerIndex;
import com.utils.GetIndexDocs;
import com.utils.Pair;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.crawler.utils.MathUtil.addVec;
import static com.crawler.utils.MathUtil.cosineSimilarity;
import static com.crawler.utils.MathUtil.normalize;

/**
 * Created by ACT-NJ on 2017/8/28.
 */
public class NewsCluster {
    private static Word2VEC vec = new Word2VEC();
    private static StopRecognition stopFilter = new StopRecognition();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
    static {
        stopFilter.insertStopNatures("w","m","mq");
        stopFilter.insertStopWords("也","了","仍","从","以","使","则","却","又","及","对","就","并","很","或","把","是","的","着","给","而","被","让","在","还","比","等","当","与","于","但");
        stopFilter.insertStopWords("一","二","三","四","五","六","七","八","九");
    }
    public static void main(String[] args) {
        NewsCluster cluster_calculator = new NewsCluster();
//        System.out.println(System.getProperty("user.dir"));
        Date now = new Date();
        Date end = DateUtils.addDays(now,0);
        Date start = DateUtils.addDays(now,-1);
        List<EventInfo> events = cluster_calculator.getTitlesVecCluster(start.getTime(),end.getTime());
        CrawlerIndex eventIndex = CrawlerIndex.getIndex();
        eventIndex.indexEvent1Step(events);
        for (EventInfo event : events) {
            System.out.println(event.getSummary()+"     >>> "+event.getArticleId()+"     >>> "+event.getShow());
        }
    }
    public List<EventInfo> getTitlesVecCluster(long time_start,long time_end){
        List<Pair> id_titles = new GetIndexDocs().getPeriodNews(time_start,time_end);
        System.out.println("------------------> news hits: "+id_titles.size());
        int num = id_titles.size();
        List<float[]> lf = new ArrayList<>(num);
        List<EventInfo> resultEvents = new ArrayList<>(num);
        try {
            vec.loadJavaModel("./files/vec.mod");
            for (Pair id_title : id_titles) {
                List<Term> terms  = ToAnalysis.parse(id_title.getText()).recognition(stopFilter).getTerms();
//                System.out.println(id_title.getText());
                float[] v = new float[200];
                int cout = 0;
                for (Term term : terms) {
                    if(term.getName().length()>1 && vec.getWordVector(term.getName())!=null) {
                        cout++;
                        v = addVec(v, vec.getWordVector(term.getName()));
                    }
                }
                lf.add(normalize(v,cout));
            }
            Map<Integer,List<Integer>> maps = getTitleVecSimi(lf);
            Iterator<Integer> it = maps.keySet().iterator();
            int mark = 1;
            String eventid = sdf.format(new Date())+"-";
            while(it.hasNext()){
                int index = it.next();
                int hot = 0;
                List<Integer> nodes = maps.get(index);
                StringBuffer sb = new StringBuffer(id_titles.get(index).getId());
                for (Integer node : nodes) {
                    sb.append(","+id_titles.get(node).getId());
                }
                if(sb.toString().contains("sin")||sb.toString().contains("tct")||sb.toString().contains("nts"))
                    hot = sb.toString().split(",").length;
                EventInfo event = new EventInfo(eventid+mark,sb.toString(),hot);
                event.setSummary(id_titles.get(index).getText());
                resultEvents.add(event);
                mark++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
//            System.out.println("<___________________________________________________>");
            return resultEvents;
        }
    }
    public Map<Integer,List<Integer>> getTitleVecSimi(List<float[]> titleVec){
        int size = titleVec.size();
        int[] mark = new int[size];
        Map<Integer,List<Integer>> maps = new HashMap<>();
        for(int i=0;i<size;i++)
            mark[i]=-1;
        for(int i=0;i<size;i++){
            if(mark[i]!=-1)
                continue;
            for(int j=i+1;j<size;j++){
                if(mark[j]!=-1)
                    continue;
                double sim = cosineSimilarity(titleVec.get(i),titleVec.get(j));
                if(sim>0.75){
                    if(mark[i]==-1)
                        mark[i]=i;
                    mark[j]=mark[i];
                    if(maps.get(i)==null)
                        maps.put(i,new ArrayList<>());
                    maps.get(i).add(j);
                }
            }
        }
        return maps;
    }
}
