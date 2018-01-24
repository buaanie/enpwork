package com.utils;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by ACT-NJ on 2017/7/24.
 */
public class FilesOpt {
    private Logger logger = LoggerFactory.getLogger(FilesOpt.class);

    public static void main(String[] args) {
        String p = "D:\\Jworkspace\\enpwork\\log";
//        System.out.println(new FilesOpt().readFile(p,"json"));
        new FilesOpt().mergerFiles(p,".txt");
    }
    public Object readFile(String path,String type){
        File file = new File(path);
        if(file.exists()){
            //采用处理流读取文件
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String temp = null;
                StringBuffer result = new StringBuffer();
                while((temp=reader.readLine())!=null && !temp.equals(""))
                    result.append(temp);
                if(type.equals("json"))
                    return JSONObject.parseObject(result.toString());
                else if(type.equals("txt"))
                    return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void storeFile(String data,String name){
        File file = new File("./log/"+name+".txt");
        boolean append = true;
        BufferedWriter writer = null;
        if(!file.exists()){
            append = false;
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer = new BufferedWriter(new FileWriter(file,append));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("{} 写入信息成功",Thread.currentThread().getName());
    }

    private void mergerFiles(String path,String suffer){
        File dir = new File(path);
        if(!dir.exists() || !dir.isDirectory()){
            logger.error("not a vaild Directory");
            return;
        }
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(suffer);
            }
        });
        StringBuilder sb = new StringBuilder();
        for (File fileName : files) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(fileName));
                String temp = null;
                while((temp=reader.readLine())!=null && !temp.equals(""))
                    sb.append(temp+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(reader!=null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        storeFile(sb.toString(),"merge0");
    }
}
