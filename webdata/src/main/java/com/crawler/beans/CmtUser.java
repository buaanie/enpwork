package com.crawler.beans;

/**
 * Created by ACT-NJ on 2017/7/14.
 */
public class CmtUser {
    private String uid;
    private String nickname;
    private String region;
    private String gender;
    private String avatar;
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
    public String getvatar(){
        return avatar;
    }
}
