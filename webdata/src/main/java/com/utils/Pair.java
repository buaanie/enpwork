package com.utils;

/**
 * Created by stcas on 2017/10/23.
 */
public class Pair {
    private String id;
    private String text;
    public Pair(String id,String text){
        this.id = id;
        this.text = text;
    }
    public String getId(){
        return id;
    }
    public String getText(){
        return text;
    }
}
