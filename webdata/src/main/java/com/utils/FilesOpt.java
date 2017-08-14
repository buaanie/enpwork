package com.utils;


import com.alibaba.fastjson.JSONObject;

import java.io.*;

/**
 * Created by ACT-NJ on 2017/7/24.
 */
public class FilesOpt {

    public static void main(String[] args) {
        String p = "webdata/files/test.json";
        System.out.println(new FilesOpt().readFile(p,"json"));
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

    public void storeFile(String data,String path){
        File file = new File(path);
        BufferedWriter writer = null;
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer = new BufferedWriter(new FileWriter(file));
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
        System.out.println(Thread.currentThread().getName()+" 写入信息成功");
    }
}
