package com.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class ItemSubject {
    private String title;       //词条名
    private String summary;       //简介
    private String id;       //词条ID
    private String synonym;       //同义词
    private List<String> polysemant;     //多义项
    private Map<String,String> infos;  //infobox
    private Set<String> relative;
    public ItemSubject(){

    }
    public ItemSubject(String title,String id,String summary,Map infos){
        this.title  = title;
        this.summary = summary;
        this.id = id;
        this.infos = infos;
        this.synonym = title;
        this.polysemant = new ArrayList<>();
        this.relative = new HashSet<>();
    }
    public String getAlias(){
        return infos.getOrDefault("别名","");
    }
    public String getTitle(){
        return title;
    }
    public String getID(){
        return id;
    }
    public String getSummary(){
        return summary;
    }
    public Map getInfos(){
        return infos;
    }
    public String getSynonym(){
        return synonym;
    }
    public List<String> getPolysemant(){
        return polysemant;
    }
    public Set<String> getRelative(){
        return relative;
    }
    public void setPolysemant(List<String> polysemant){
        this.polysemant = polysemant;
    }
    public void setSynonym(String synonym){
        this.synonym += ','+synonym;
    }
    public void setRelative(Set<String> relative){
        this.relative = relative;
    }
    public String toString(){
        String item = "title:"+title+" ,id:"+id+" ,summary:"+summary +" ,synonym:"+synonym +" ,relative:"+relative.size();
        String info ="";
        try {
            ObjectMapper mapper = new ObjectMapper();
            info = mapper.writeValueAsString(infos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item+info;
    }
}
