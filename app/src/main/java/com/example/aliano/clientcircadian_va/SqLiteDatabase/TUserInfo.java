package com.example.aliano.clientcircadian_va.SqLiteDatabase;

/**
 * Created by Alexis on 22.09.2016.
 */

// to do : id should be given by the server
public class TUserInfo {
    private long id;
    private String name;
    private String birthdate;
    private String sexe;
    private int weight;
    private int size;
    private int profile;
    private long xp;
    private int level;
   // private String email;

    public TUserInfo(){}

    public TUserInfo(long id, String name,String birthdate,String sexe, int weight, int size, int profile, long xp, int level){
        this.name = name;
        this.id = id; // given by the server
        this.birthdate = birthdate;
        this.sexe = sexe; // F ou M
        this.weight = weight; // int
        this.size = size; // cm
        this.profile = profile;
        this.xp = xp;
        this.level = level;
        //this.email = email;
    }

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public void setBirthdate(String birthdate){ this.birthdate = birthdate; }
    public String getBirthdate(){ return this.birthdate; }

    public void setSexe(String sexe){ this.sexe = sexe; }
    public String getSexe(){ return this.sexe; }

    public void setSize(int size){ this.size = size; }
    public int getSize(){return this.size;}

    public void setWeight(int weight){ this.weight = weight; }
    public int getWeight(){return this.weight;}

    public void setProfile(int profile){ this.profile = profile; }
    public int getProfile(){return this.profile;}

    public void setXp(int xp){ this.xp = xp; }
    public long getXp(){return this.xp;}

    public void setLevel(int level){ this.level = level; }
    public int getLevel(){return this.level;}

    //public String getEmail() {return email;}
    //public void setEmail(String name) {this.email = email;}

    public String toString(){
        return "ID : "+id+"\nNAME : "+ name +"\nSEXE:"+sexe+"\nBIRTHDATE:"+birthdate +"\nWEIGHT:"+weight +"\nPROFILE:"+profile+"\n"+xp +"\nLEVEL:"+level;
    }
}
