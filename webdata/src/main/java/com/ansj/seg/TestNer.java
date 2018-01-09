package com.ansj.seg;

import org.ansj.app.crf.Model;
import org.ansj.app.crf.model.CRFModel;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ACT-NJ on 2017/8/28.
 */
public class TestNer {
    public static void main(String[] args) {
        StopRecognition stopFilter = new StopRecognition();
        stopFilter.insertStopNatures("w","m","mq");
        stopFilter.insertStopWords("也","了","仍","将","从","以","使","则","却","又","及","对","就","并","很","或","把","是","的","着","给","而","被","让","在","还","比","等","当","与","于","但");
        String s = "BBC8月28日电 据外媒报道，泰国总理巴育表示，该国政府将注销前总理英拉的泰国护照。他同时表示，目前暂无有关英拉身在何处的准确信息。" +
                "28日，巴育确认，下一步行动将是注销英拉的护照，他表示：“注销护照是此类情况下的标准程序。”据泰国外交部消息，英拉拥有两本泰国护照，一本是她担任总理时获得的外交护照，另一本则是因私护照。" +
                "巴育还表示：“目前没有有关她身在何处的准确信息。”巴育同时否认，泰国政府有意放英拉离境。" +
                "当地时间25日，泰国最高法院对英拉所涉大米收购案进行宣判，不过英拉未出庭宣判。根据未经证实的消息，她已离开泰国。";
        List<Term> terms = NlpAnalysis.parse(s).recognition(stopFilter).getTerms();
        HashSet<String> entities = new HashSet<>();
        for (Term term : terms) {
            String nature =term.getNatureStr();
            //nr：人名 nt：机构团体名 nz：专名 ns：地名
            if(nature.startsWith("nr") || nature.startsWith("nt")|| nature.startsWith("nz") || nature.startsWith("ns")) {
                entities.add(term.getRealName());
                System.out.println(term.getNatureStr()+" "+term.getName());
            }
        }
        try {
            Model model = CRFModel.load("D:\\Jworkspace\\enpwork\\library\\crf.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
