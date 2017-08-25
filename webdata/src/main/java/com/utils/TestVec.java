package com.utils;

import com.alibaba.fastjson.JSONObject;
import com.ansj.vec.Learn;
import com.ansj.vec.Word2VEC;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.util.IOUtil;

import javax.jws.Oneway;
import java.io.*;
import java.util.List;

/**
 * Created by @NieJ on 2017/8/24.
 */
public class TestVec {
    private static StopRecognition stopFilter = new StopRecognition();
    public static void main(String[] args) {
        stopFilter.insertStopNatures("w","m");
        stopFilter.insertStopWords("的","再","和","在");
        File pathIn = new File("D:\\Github\\enpwork\\newsclust");
        File pathOut = new File("D:\\Github\\enpwork\\log\\result.txt");
        if(!pathOut.exists())
            try {
                pathOut.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        File[] files =  pathIn.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("txt");
            }
        });
        try(FileOutputStream fos =new FileOutputStream(pathOut)){
            for (File file : files) {
                parseFile(fos,file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Learn learn = new Learn();
            learn.learnFile(pathOut);
            learn.saveModel(new File("D:\\Github\\enpwork\\log\\vec.mod"));

            Word2VEC vec = new Word2VEC();
            vec.loadJavaModel("D:\\Github\\enpwork\\log\\vec.mod");
            System.out.println(vec.distance("深圳"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseFile(FileOutputStream fos, File file) {
        try(BufferedReader br = IOUtil.getReader(file.getAbsolutePath(),IOUtil.UTF8)){
            String temp =null;
            JSONObject json = null;
            while((temp=br.readLine())!=null){
                json = JSONObject.parseObject(temp);
                fos.write(ToAnalysis.parse(json.getString("title")).recognition(stopFilter).toStringWithOutNature(" ").getBytes());
                fos.write(ToAnalysis.parse(json.getString("content")).recognition(stopFilter).toStringWithOutNature(" ").getBytes());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
