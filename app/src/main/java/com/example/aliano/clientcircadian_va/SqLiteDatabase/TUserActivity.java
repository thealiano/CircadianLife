package com.example.aliano.clientcircadian_va.SqLiteDatabase;

/**
 * Created by Alexis on 22.09.2016.
 */
public class TUserActivity {
    private String name;
    private int confidence;
    private float sumAcc2,x,y,z;
    private float loc_lat;
    private float loc_long;
    private String timestamp; // use only in return
    private String timezone; // use only in return
    private boolean nod; // night or day
    private int step;
    private int kcal;

    public TUserActivity(){this.timestamp ="?";}

    public TUserActivity(String name,int confidence,float sumAcc2,float x,float y,float z, float loc_lat, float loc_long, boolean Nod, int step, int kcal){
        this.name = name;
        this.confidence = confidence;
        this.sumAcc2 = sumAcc2;
        this.x = x;
        this.y = y;
        this.z = z;
        this.loc_lat = loc_lat; // F ou M
        this.loc_long = loc_long; // int
        this.nod = Nod;
        this.step = step;
        this.kcal = kcal;
        //timestamp and timezone  initialized by the local database (corresponding to the moment of record)
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public int getConfidence(){ return confidence; }
    public void setConfidence(int confidence){ this.confidence = confidence; }

    public float getSumAcc2(){ return this.sumAcc2; }
    public void setSumAcc2(float sumAcc2){ this.sumAcc2 = sumAcc2; }

    public float getX(){ return this.x; }
    public void setX(float X){ this.x = X; }
    public float getY(){ return this.y; }
    public void setY(float Y){ this.y = Y; }
    public float getZ(){ return this.z; }
    public void setZ(float Z){ this.z = Z; }

    public float getLat(){return this.loc_lat; }
    public void setLat(float loc_lat){ this.loc_lat = loc_lat; }

    public float getLong(){ return this.loc_long; }
    public void setLong(float loc_long){ this.loc_long = loc_long; }

    public String getTimestamp(){ return this.timestamp; }
    public void setTimestamp(String timestamp){ this.timestamp = timestamp; }
    public String getTimezone(){ return this.timestamp; }
    public void setTimezone(String timezone){ this.timezone = timezone; }
    public int getStep(){ return this.step; }
    public void setStep(int step){ this.step = step; }
    public int getKcal(){ return this.kcal; }
    public void setKcal(int kcal){ this.kcal = kcal; }
    public boolean getNod(){return this.nod;}
    public void setNod(boolean nod){this.nod = nod;}
    public String toString(){
        return "\nNAME : "+ name +"\nCONF:"+confidence+"\nSUMACC2:"+sumAcc2 +"  x,y,z:"+x+","+y+","+z
                +"\nLAT:"+loc_lat +"\nLONG:"+loc_long+"\n"+"\ntimestamp:"+timestamp+"\n"
                +"\nstep="+step+"\nKcal="+kcal+"\ntimezone="+timezone+"\nNod="+nod;
    }
}
