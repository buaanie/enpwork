package com.ansj.vec;

import com.alibaba.fastjson.JSONObject;
import com.ansj.vec.Learn;
import com.ansj.vec.Word2VEC;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.util.IOUtil;

import javax.jws.Oneway;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.crawler.utils.MathUtil.*;

/**
 * Created by @NieJ on 2017/8/24.
 */
public class TestVec {
    private static StopRecognition stopFilter = new StopRecognition();
    public static void main(String[] args) {
        stopFilter.insertStopNatures("w","m","mq");
        stopFilter.insertStopWords("也","了","仍","从","以","使","则","却","又","及","对","就","并","很","或","把","是","的","着","给","而","被","让","在","还","比","等","当","与","于","但");
        stopFilter.insertStopWords("一","二","三","四","五","六","七","八","九");
//        File pathIn = new File("./webdata/log");
//        File pathOut = new File("./webdata/files/news_res.txt");
//        if(!pathOut.exists())
//            try {
//                pathOut.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        File[] files =  pathIn.listFiles((dir, name) -> name.endsWith("txt"));
//        try(FileOutputStream fos =new FileOutputStream(pathOut)){
//            for (File file : files) {
//                parseFile(fos,file);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
//            Learn learn = new Learn();
//            learn.learnFile(pathOut);
//            learn.saveModel(new File("./webdata/files/vec.mod"));

            Word2VEC vec = new Word2VEC();
            vec.loadJavaModel("./files/vec.mod");
            String s1 = "郎永淳犯危险驾驶罪被判处拘役3个月";
            String s2 = "郎永淳因醉驾被判拘役3个月 并处罚金4000元";
            String s3 = "郎永淳犯危险驾驶罪被判拘役三个月 处罚金四千元";
            String[] ss = {s1,s2,s3};
            List<float[]> lf = new ArrayList<>();
            for (String s : ss) {
                List<Term> t  = DicAnalysis.parse(s).recognition(stopFilter).getTerms();
                float[] v = new float[200];
                int cout = 0;
                for (Term term : t) {
                    if(term.getName().length()>1 && vec.getWordVector(term.getName())!=null){//term.getName().length()>1 &&
                        cout++;
                        if(term.getName().equals("北京"))
                            v = minusVec(v,vec.getWordVector(term.getName()));
                        else
                            v = addVec(v,vec.getWordVector(term.getName()));
                    }
                }
                lf.add(normalize(v,cout));
            }
            for(int i=0;i<lf.size();i++){
                System.out.println(Arrays.toString(lf.get(i)));
                System.out.println(cosineSimilarity(lf.get(0),lf.get(i)));
            }
//            System.out.println(vec.analogy("深圳","广州","武汉"));
//            System.out.println(vec.distance("深圳"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseFile(FileOutputStream fos, File file) {
        try(BufferedReader br = IOUtil.getReader(file.getAbsolutePath(),IOUtil.UTF8)){
            String temp =null;
            JSONObject json = null;
            while((temp=br.readLine())!=null){
                temp = temp.replaceAll("[(|《|（].*？[)|》|）]","").replace(" ","");
                if(temp.length()<20 && temp.contains("编辑"))
                    continue;
//                json = JSONObject.parseObject(temp);
//                fos.write(ToAnalysis.parse(json.getString("title")).recognition(stopFilter).toStringWithOutNature(" ").getBytes());
//                fos.write(ToAnalysis.parse(json.getString("content")).recognition(stopFilter).toStringWithOutNature(" ").getBytes());
                fos.write((ToAnalysis.parse(temp).recognition(stopFilter).toStringWithOutNature(" ")+"\n").getBytes());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ansj 词性： http://blog.csdn.net/lb521200200/article/details/53504839
    private static void testVec(){
        //        String s = "分黄网综合报道（你好）(zheren) 责任编辑";
//        s = s.replaceAll("[(|（].*[)|）]","");
//        System.out.println(s);
        Word2VEC vec = new Word2VEC();
        StopRecognition stopFilter = new StopRecognition();
        stopFilter.insertStopNatures("w","m","mq");
        stopFilter.insertStopWords("也","了","仍","从","以","使","则","却","又","及","对","就","并","很","或","把","是","的","着","给","而","被","让","在","还","比","等","当","与","于","但");
        stopFilter.insertStopWords("一","二","三","四","五","六","七","八","九");
        try {
            vec.loadJavaModel("./webdata/files/vec.mod");
            String title1 = "杭州姑娘代购半年逃税80多万 涉嫌走私被查";
            String title2 = "杭州85后姑娘职业代购半年 逃税80多万被公诉";
            String title3 =  "杭州姑娘代购半年 逃税80多万面临刑罚";
//            String title4 = "杭州女孩代购半年 逃税80多万被查";
//            String title5 = "这种情况下代购会涉嫌走私，已有杭州姑娘因此被查";
//            String title6 = "杭州女老板店内被捅杀 浑身是血边喊救命边往外爬";
//            String title7 = "杭州市检察院对“保姆放火案”被告人提起公诉";
//            String title8 = "全国首家互联网法院在杭州成立";
//            String title9 = "中国首家互联网法院在杭州揭牌";
            String[] ss = new String[]{title1,title2,title3};
            List<float[]> lf = new ArrayList<>();
            for (String s : ss) {
                List<Term> t  = ToAnalysis.parse(s).recognition(stopFilter).getTerms();
                float[] v = new float[200];
                int cout = 0;
                for (Term term : t) {
                    if(term.getName().length()>1){
                        cout++;
                        if(null!=vec.getWordVector(term.getName()))
                            v = addVec(v,vec.getWordVector(term.getName()));
                    }
                }
                lf.add(normalize(v,cout));
            }
            for(int i=1;i<lf.size();i++){
                System.out.println(cosineSimilarity(lf.get(0),lf.get(i)));
            }
//            System.out.println(Arrays.toString(vec.getWordVector("杭州")));
//            System.out.println(vec.distance("杭州"));

//            TreeSet<WordEntry> rev = vec.analogy("上海", "成都", "杭州");
//            rev.iterator().forEachRemaining(key-> System.out.println(key));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String st = "天津爆炸事件的影响非常可怕，...一 1 二所以我们以及他们都不敢再怠慢。<《论语》？";
//        StopRecognition stopFilter = new StopRecognition();
//        stopFilter.insertStopNatures("w","m");
//        stopFilter.insertStopWords("的","再");
//        System.out.println(ToAnalysis.parse(StringUtil.rmHtmlTag(st)).toStringWithOutNature(" "));
//        System.out.println(ToAnalysis.parse(s).recognition(stopFilter).toString());
//        List<Term> terms = ToAnalysis.parse(s).recognition(stopFilter).getTerms();
//        for (Term term : terms) {
//            System.out.print(term.getName()+" ");
//        }
//        System.out.println();
//        System.out.println(ToAnalysis.parse(s).toString());

//        String title = "中国四纵四横高铁网基本建成 雄安新区将直达香港";
//        String content = "连接黑龙江省哈尔滨和牡丹江的哈牡高铁最长隧道——虎峰岭隧道将于今天（20日）贯通，这意味着我国八纵八横高铁网最北边的一横突破了又一重大难关。虎峰岭隧道位于东北小兴安岭支脉的林海雪原区域，全长8755米，是哈牡高铁的重难点控制工程之一。隧道地处高寒地带 冬季最低气温接近零下四十度 为了保证冬季正常施工 我们采取了一系列保温措施 比如用热水拌和混凝土 在洞口安装保温帘 保证洞内温度达到五度以上。哈牡高铁建成后，哈尔滨到牡丹江的行车时间仅需一个多小时，未来沿线地区也将通过哈尔滨与哈大高铁对接。同样连接哈大线的京沈高铁重要一站——阜新站即将封顶，未来这里将成为东北地区进京客流便捷通道。阜新站开通后将承接哈大高速铁路，从阜新到达沈阳只需40多分钟、预计到达北京只需要2个小时30分钟左右，比过去出行到这些大城市的时间缩短了约2/3。根据八纵八横高铁网的规划，未来京沈高铁与京广哈大高铁对接，成为我国南北走向里程最长的高铁大动脉。去年出台的八纵八横高铁网规划最近有了一个新变化：连接北京和雄安新区的京雄铁路规划出台，未来从雄安新区出发，乘坐高铁可以直达香港九龙。让我们一起去看看，调整后的八纵八横高铁网，哪些线路可能会改变我们的出行？中国铁建第四勘察设计院规划师 郑洪：京雄铁路从雄安引出以后到阜阳分两支 一支到香港九龙 另外一支到福建 它是八纵八横重要的一纵。八纵八横高铁网中，新规划的纵向线路还有：呼和浩特到南宁的呼南通道；北京到昆明的京昆通道；包头到海口的包海通道；兰州到广州的兰广通道。新规划的横向线路有：绥芬河到满洲里的绥满通道；北京到兰州的京兰通道；厦门到重庆的厦渝通道；广州到昆明的广昆通道。八纵八横高铁网建成后，全国高速铁路总里程将达到4.5万公里，比现在增加一倍。它将连接起总里程超过20万公里的全国铁路网，基本覆盖20万人口以上的城市。?去年出台的《中长期铁路网规划》首次明确提出要建设八纵八横高铁网，它是建立在2004年规划的四纵四横高铁网的基础之上。原有的四纵四横高铁网中的纵线：北京到上海、北京到广州、哈尔滨到沈阳，以及东南沿海高铁；四条横线中的上海到昆明、上海到成都、徐州到兰州都已经通车，除正在建设的北京到沈阳、青岛到济南、石家庄外，我国四纵四横高铁网已经全部建成。";
//        KeyWordComputer keyWordComputer = new KeyWordComputer(5);
//        SummaryComputer summaryComputer = new SummaryComputer(title,content);
//        System.out.println(summaryComputer.toSummary().getSummary());
//        List<Keyword> resss = keyWordComputer.computeArticleTfidf(title, content);
//        List<Keyword> ress = keyWordComputer.computeArticleTfidf(content);
//
//        System.out.println(resss);
//        System.out.println(ress);
    }
}
