package com.example.aliano.clientcircadian_va;

import org.json.JSONArray;

/**
 * Created by Aliano on 05/05/2016.
 */
public class View_Event {

    private long eid; // id unique
    private String name; // Nom de l'evenement
    private String value; // valeur de la description
    private String type; //type d'evenement
    private String content =""; // contenu de l'evenement
    private JSONArray answers;
    private int resImg1;
    private int resImg2;
    private int eventPoints;

    public View_Event(long eid,String type,  String name, String value) {
        // for the visualisation in the EventList
        this.eid = eid;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public View_Event(long eid,String type,  String name, String value, String content) {
        //for the custom view
        this.eid = eid;
        this.type = type;
        this.name = name;
        this.value = value;
        this.content = content;
    }
    public View_Event(long eid,String type,  String name, String value, String content, int resImg1) {
        this.eid = eid;
        this.type = type;
        this.name = name;
        this.value = value;
        this.content = content;
        this.resImg1 = resImg1;
        //this.resImg2 = resImg2;
    }
    public View_Event(long eid, String type, String name, String value, String content, JSONArray answers) {
        // QCM and FORM
        this.eid = eid;
        this.type = type;
        this.name = name;
        this.value = value;
        this.content = content;
        this.answers = answers;

        //this.resImg2 = resImg2;
    }
    //...Getters
    public long getEid(){return eid;}
    public String getName(){
        return name;
    }
    public String getValue(){
        return value;
    }
    public String getType(){return type;}
    public String getContent() {return content;}
    public int getResImg1(){return resImg1;}
    public int getResImg2(){return resImg2;}
    public JSONArray getAnswers(){return answers;}
    //
}
