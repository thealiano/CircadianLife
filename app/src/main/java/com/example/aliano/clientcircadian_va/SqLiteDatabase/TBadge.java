package com.example.aliano.clientcircadian_va.SqLiteDatabase;

/**
 * Created by Alexis on 23.11.2015.
 */
public class TBadge {

        private long id;
        private String name;
        private String description;
        private int unlock;
        private String obtainDate;
        private int resNumber;

        public TBadge(){}

        public TBadge(long id, String name,String description,int unlock, String obtainDate, int resNumber){
            this.name = name;
            this.id = id;
            this.description = description;
            this.unlock = unlock;
            this.obtainDate = obtainDate;
            this.resNumber = resNumber;
        }

        public long getId() {return id;}
        public void setId(long id) {this.id = id;}

        public String getName() {return name;}
        public void setName(String name) {this.name = name;}

        public void setDescription(String description){ this.description = description; }
        public String getDescription(){ return this.description; }

        public void setObtainDate(String obtainDate){ this.obtainDate = obtainDate; }
        public String getObtainDate(){return this.obtainDate;}

        public void setUnlock(int unlock){ this.unlock = unlock; }
        public int getUnlock(){return this.unlock;}

        public void setResNumber(int resNumber){ this.resNumber = resNumber; }
        public int getResNumber(){return this.resNumber;}

        public String toString(){
                return "ID : "+id+"\nNAME : "+ name +"\n"+ description +"\n"+unlock +"\n"+obtainDate+"\n"+resNumber;
        }
 }


