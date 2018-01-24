package com.ansj.seg;

import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;

/**
 * Created by stcas on 2018/1/23.
 */
public class SegFile {
    private StopRecognition stopFilter = new StopRecognition();
    public SegFile(){
        stopFilter.insertStopNatures("w","m","mq");
        stopFilter.insertStopWords("也","了","仍","将","从","以","使","则","却","又","及","对","就","并","很","或","把","是","的","着","给","而","被","让","在","还","比","等","当","与","于","但");
    }
    public static void main(String[] args) {
        SegFile segFile = new SegFile();
        segFile.seg("./log/merge0.txt","./log/segmerge.txt");
    }
    public void seg(String input,String output){
        File inFile = new File(input);
        File outFile = new File(output);
        if(!outFile.exists()){
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(inFile.exists()){
            System.out.println("start");
            //采用处理流读取文件
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                reader = new BufferedReader(new FileReader(inFile));
                writer = new BufferedWriter(new FileWriter(outFile,true));
                String temp = null;
                while((temp=reader.readLine())!=null && !temp.equals("")){
                    writer.write(ToAnalysis.parse(temp).recognition(stopFilter).toStringWithOutNature(" ")+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                    try {
                        if(reader!=null)
                            reader.close();
                        if(writer!=null)
                            writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

}
