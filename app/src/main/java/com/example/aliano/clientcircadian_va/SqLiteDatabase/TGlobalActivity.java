package com.example.aliano.clientcircadian_va.SqLiteDatabase;

/**
 * Created by Aliano on 23/02/2018.
 */

public class TGlobalActivity {
    private String type;
    private String json;
    private String lastUpdate;
    private long avg_steps; //
    private long avg_kcal; //

    public TGlobalActivity(){}

    public TGlobalActivity(String type,String json,String lastUpdate, long avg_steps, long avg_kcal){
        this.type = type; //GLO7 , 30 ,90 WDA 7 ,30 ,90 ET WEA 7 30 90
        this.json = json; //HOUR : COUNT :
        this.lastUpdate = lastUpdate; //
        this.avg_steps = avg_steps; //
        this.avg_kcal = avg_kcal; //
    }

    public void setType(String type){ this.type = type; }
    public String getType(){ return this.type; }

    public String getJson() {return json;}
    public void setJson(String json) {this.json = json;}

    public void setLastUpdate(String lastUpdate){ this.lastUpdate = lastUpdate; }
    public String getlastUpdate(){ return this.lastUpdate; }

    public void setAvgSteps(long avg_steps){ this.avg_steps = avg_steps; }
    public long getAvgSteps(){return this.avg_steps;}

    public void setAvgKcals(long avg_kcal){ this.avg_kcal = avg_kcal; }
    public long getAvgKcals(){return this.avg_kcal;}

   public String toString(){
        return "getStingGLO:"+"\ntype : "+ type +"\nJSON:"+json+"\nlastUP:"+lastUpdate +"\navgSteps:"+avg_steps +"\navgKcals:"+avg_kcal;
    }
}
