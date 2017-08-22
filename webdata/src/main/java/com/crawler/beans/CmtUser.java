package com.crawler.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ACT-NJ on 2017/7/14.
 */
public class CmtUser {
    private String uid;
    private String nickname;
    private String region;
    private String gender;
    private String avatar;
    private HashMap<String,String> extras;
    public CmtUser(String uid, String name, String region, String gender, String avatar)
    {
        this.uid = uid;
        this.nickname = name;
        this.region = region.equals("::")?"-":region;
        this.gender = gender;
        this.avatar = avatar;
    }
    public String getUid(){
        return uid;
    }
    public String getNickname(){
        return nickname;
    }
    public String getRegion(){
        return region;
    }
    public String getGender(){
        return gender;
    }
    public String getAvatar(){
        return avatar;
    }
    public String getExtra(String key) {
        if (extras == null || extras.size()==0) {
            return null;
        }
        return extras.get(key);
    }
    public String getExtra() {
        if (extras == null || extras.size()==0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : extras.keySet()) {
            sb.append(s+":{"+extras.get(s)+"}");
        }
        return  sb.toString();
    }
    public void putExtra(String key, String value) {
        if (extras == null) {
            extras = new HashMap<String, String>();
        }
        extras.put(key, value);
    }
}
