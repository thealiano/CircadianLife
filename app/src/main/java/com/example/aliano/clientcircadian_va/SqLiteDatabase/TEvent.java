package com.example.aliano.clientcircadian_va.SqLiteDatabase;

/**
 * Created by Aliano on 29/06/2017.
 */
public class TEvent {
    private long id;
    private String type;
    private String name;
    private String description;
    private String value; // contenu ou question
    private String contenu; // réponse ou rien
    private int image; // res drawable
    private boolean done; // quand l'event a été réalisé
    //private boolean sent; // quand l'event a été envoyé
    public TEvent(){}

    public TEvent(long id, String type,String name,String description, String value, String contenu,int image, boolean done){
        this.id = id; // given by the server
        this.type = type;
        this.name = name; // F ou M
        this.description = description; // int
        this.value = value; // valeur de l'evenement / question
        this.contenu = contenu; // valeur de la réponse (form / qcm)
        this.image = image;
        this.done = done; // 0 = pas fait , 1 = fait (pour un form le contenu est remplacé par la réponse utilisateur)
        //this.email = email;
    }


    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public void setType(String type){ this.type = type; }
    public String getType(){ return this.type; }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public void setDescription(String description){ this.description = description; }
    public String getDescription(){ return this.description; }

    public void setValue(String value){ this.value = value; }
    public String getValue(){return this.value;}

    public void setContenu(String contenu){ this.contenu = contenu; }
    public String getContenu(){return this.contenu;}

    public void setImage(int image){ this.image = image; }
    public int getImage(){return this.image;}

    public void setDone(boolean done){ this.done = done; }
    public boolean getDone(){return this.done;}
/*
    public void setSent(boolean sent){ this.sent = sent; }
    public boolean getSent(){return this.sent;}
    //public String getEmail() {return email;}
    //public void setEmail(String name) {this.email = email;}
*/
    public String toString(){
        return "ID : "+id+"\ntype : "+ type +"\nname:"+name+"\ndescription:"+description +"\nvalue:"+value +"\ncontenu:"+contenu+"\nimage:"+image;
    }
}
