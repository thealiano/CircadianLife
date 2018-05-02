package com.example.aliano.clientcircadian_va.SqLiteDatabase;
/**
 * Created by Aliano on 23.11.2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CircadianLocalBDD {

    private static final int VERSION_BDD = 17; // a changer à chaque modification de structure
    private static final String NOM_BDD = "circadian.db";
    static String dateBdd;
    static int numberOfBadges, numberOfObtainedBadges;

    // ALL THE TABLES NAME
    public static final String TABLE_BADGES = "Badges";
    static final String TABLE_USERINF = "UserInfo";
    static final String TABLE_USERACTIVITY="UserActivity";
    static final String TABLE_EVENT="Event";
    static final String TABLE_GLOBALACTIVITY="GlobalActivity";

    // ALL THE TABLES POSSIBLES FIELDS
    // for all tables
    static final String COL_ID = "ID";
    static final int NUM_COL_ID = 0;
    static final String COL_NAME= "Name";
    static final int NUM_COL_NAME = 1;

    // for table badges
    static final String COL_DESC= "Description";
    static final int NUM_COL_DESC = 2;
    static final String COL_UNLOCK = "IsUnlock";
    static final int NUM_COL_UNLOCK = 3;
    static final String COL_DATE ="Date";
    static final int NUM_COL_DATE = 4;
    static final String COL_RNUM= "ResNumber";
    static final int NUM_COL_RNUM = 5;

    // Table User Information

    // for table UserInfo
    static final String COL_BIRTHDATE ="BirthDate";
    static final int NUM_COL_BIRTHDATE = 2;
    static final String COL_SEXE ="Sexe";
    static final int NUM_COL_SEXE = 3;
    static final String COL_SIZE ="Size";
    static final int NUM_COL_SIZE = 4;
    static final String COL_WEIGHT ="Weight";
    static final int NUM_COL_WEIGHT = 5;
    static final String COL_PROFIL = "Profil";
    static final int NUM_COL_PROFILE = 6;
    static final String COL_XP_EARNED = "XpEarned"; //,,;
    static final int NUM_COL_XP_EARNED = 7;
    static final String COL_LEVEL = "Level";
    static final int NUM_COL_LEVEL = 8;
    //public static final String COL_EMAIL = "Email";
    //static final int NUM_COL_EMAIL = 9;
    // FOR TABLE User Activity

    static final int NUM_COL_NAME_ACTIVITY = 0;
    private static final String COL_CONFIDENCE ="Confidence";
    static final int NUM_COL_CONFIDENCE = 1;
    private static final String COL_SUMACC2 ="SumAccelerometer2";
    static final int NUM_COL_SUMACC2 = 2;
    private static final String COL_X ="X";
    static final int NUM_COL_X = 3;
    private static final String COL_Y ="Y";
    static final int NUM_COL_Y = 4;
    private static final String COL_Z ="Z";
    static final int NUM_COL_Z = 5;
    private static final String COL_LAT ="Latitude";
    static final int NUM_COL_LAT = 6;
    private static final String COL_LONG ="Longitude";
    static final int NUM_COL_LONG = 7;
    public static final String COL_TIME_STAMP = "Timestamp";
    static final int NUM_COL_TIME_STAMP = 8;
    public static final String COL_TIME_ZONE = "Timezone";
    static final int NUM_COL_TIME_ZONE = 9;
    public static final String COL_NOD = "NightOrDay";
    static final int NUM_COL_NOD = 10;
    public static final String COL_STEP = "Step";
    static final int NUM_COL_STEP = 11;
    public static final String COL_KCAL = "Kcal";
    static final int NUM_COL_KCAL = 12;

    // for table events
    // id = 0
    static final int NUM_COL_EVENT_TYPE = 1;
    private static final String COL_EVENT_TYPE ="EventType";
    static final int NUM_COL_EVENT_NAME = 2;
    private static final String COL_EVENT_NAME ="EventName";
    static final int NUM_COL_EVENT_DESCRIPTION = 3;
    private static final String COL_EVENT_DESCRIPTION ="EventDescription";
    static final int NUM_COL_EVENT_VALUE = 4;
    private static final String COL_EVENT_VALUE ="EventValue";
    static final int NUM_COL_EVENT_CONTENT = 5;
    private static final String COL_EVENT_CONTENT ="EventContent";
    static final int NUM_COL_EVENT_IMG = 6;
    private static final String COL_EVENT_IMG ="EventImg";
    static final int NUM_COL_EVENT_DONE = 7;
    public static final String COL_EVENT_DONE = "EventDone";
    // timestamp = 8 ?
    static final int NUM_COL_EVENT_SENT = 8;
    public static final String COL_EVENT_SENT = "EventSent";
    //
    // for table global activity
    static final int NUM_COL_GLO_ACTIVITY_TYPE = 0; // PRIMARY KEY !!!!
    private static final String COL_GLO_ACTIVITY_TYPE ="Type";
    static final int NUM_COL_GLO_ACTIVITY_JSON = 1;
    private static final String COL_GLO_ACTIVITY_JSON ="Json";
    static final int NUM_COL_GLO_ACTIVITY_LASTUPDATE = 2;
    private static final String COL_GLO_ACTIVITY_LASTUPDATE ="LastUpdate";
    static final int NUM_COL_GLO_ACTIVITY_AVGSTEPS = 3;
    private static final String COL_GLO_ACTIVITY_AVGSTEPS ="Avg_steps";
    static final int NUM_COL_GLO_ACTIVITY_AVGKCALS = 4;
    private static final String COL_GLO_ACTIVITY_AVGKCALS ="Avg_kcals";

    private SQLiteDatabase bdd;
    private MaBaseSqLite maBaseSQLite;

    public CircadianLocalBDD(Context context){
        //On créer la BDD et sa table
        maBaseSQLite = new MaBaseSqLite(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        //numberOfSports = numberOfBadges = numberOfMatchs = 0;
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }
// Table TBadge
    public long insertBadge(TBadge badge){
        numberOfBadges++;
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_ID, badge.getId());
        values.put(COL_NAME, badge.getName());
        values.put(COL_DESC, badge.getDescription());
        values.put(COL_UNLOCK, badge.getUnlock());
        values.put(COL_DATE, badge.getObtainDate());
        values.put(COL_RNUM, badge.getResNumber());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_BADGES, null, values);
    }
    // Table tEvent
    public long insertEvent(TEvent event){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_ID, event.getId());
        values.put(COL_EVENT_TYPE, event.getType());
        values.put(COL_EVENT_NAME, event.getName());
        values.put(COL_EVENT_DESCRIPTION, event.getDescription());
        values.put(COL_EVENT_VALUE, event.getValue());
        values.put(COL_EVENT_CONTENT, event.getContenu());
        values.put(COL_EVENT_IMG, event.getImage());
        values.put(COL_EVENT_DONE, event.getDone());
        values.put(COL_EVENT_SENT, false);
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_EVENT, null, values);
    }
    // Table tEvent
    public long insertGlobalActivity(TGlobalActivity globalActivity){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_GLO_ACTIVITY_TYPE, globalActivity.getType());
        values.put(COL_GLO_ACTIVITY_JSON, globalActivity.getJson());
        values.put(COL_GLO_ACTIVITY_LASTUPDATE, globalActivity.getlastUpdate());
        values.put(COL_GLO_ACTIVITY_AVGSTEPS, globalActivity.getAvgSteps());
        values.put(COL_GLO_ACTIVITY_AVGKCALS, globalActivity.getAvgKcals());

        return bdd.insert(TABLE_GLOBALACTIVITY, null, values);
    }
    public long insertUserInfo(TUserInfo userInfo){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_ID, userInfo.getId());
        values.put(COL_NAME, userInfo.getName());
        values.put(COL_BIRTHDATE, userInfo.getBirthdate());
        values.put(COL_SEXE, userInfo.getSexe());
        values.put(COL_SIZE, userInfo.getSize());
        values.put(COL_WEIGHT, userInfo.getWeight());
        values.put(COL_PROFIL, userInfo.getProfile());
        values.put(COL_XP_EARNED, userInfo.getXp());
        values.put(COL_LEVEL, userInfo.getLevel());
        //values.put(COL_EMAIL, userInfo.getEmail());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_USERINF, null, values);
    }
    public long insertUserActivity(TUserActivity userActivity){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_NAME, userActivity.getName());
        values.put(COL_CONFIDENCE, userActivity.getConfidence());
        values.put(COL_SUMACC2, userActivity.getSumAcc2());
        values.put(COL_X, userActivity.getX());
        values.put(COL_Y, userActivity.getY());
        values.put(COL_Z, userActivity.getZ());
        values.put(COL_LAT, userActivity.getLat());
        values.put(COL_LONG, userActivity.getLong());

        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        values.put(COL_TIME_STAMP, dateFormat.format(date));
        TimeZone tz = TimeZone.getDefault(); // GET device default timezone

        values.put(COL_TIME_ZONE, tz.getID());
        values.put(COL_NOD, userActivity.getNod());
        values.put(COL_STEP, userActivity.getStep());
        values.put(COL_KCAL, userActivity.getKcal());
       // Log.i("SqlWrite","tz_id:"+tz.getID()+"tz_name:"+tz.getDisplayName()+"tz_string"+tz.toString());
       // Log.i("SqlWrite", "ts insert = " + dateFormat.format(date).toString());
        // Column Timestamp initilaze by default with current timestamp
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_USERACTIVITY, null, values);
    }

    public int unlockBadge(TBadge badge){
        // to do : Doit appeler BadgeObtainActivity en lui passant les infos de TBadges via Intent
        //numberOfObtainedBadges++;
        //La mise à jour d'un badge dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simple préciser quel badge on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_UNLOCK, 1); // unlock badge
        values.put(COL_DATE, getTimeStamp()); // set unlock date
        // doit envoyé "badge_1" à Badge Obtain Activity
        return bdd.update(TABLE_BADGES, values, COL_ID + " = " + badge.getId(), null);
    }
    public boolean isBadgeUnlock(String resId, Context appContext){
        // moche de passer le context peut mieux faire
        int arrayId = appContext.getResources().getIdentifier(resId, "array", appContext.getPackageName());
        String[] tab = appContext.getResources().getStringArray(arrayId);
        String badge_name = tab[0].replaceAll("'","\'"); // from user so we prevent sqlite errors
        Log.i("checkBadges","tab0:"+tab[0] +"badges_name"+badge_name);
        //insertBadge(new TBadge(777, tab[0], tab[1], 0, "date", getImageResourceByName(tab[2], appContext)));
        String qNbEvent =  "SELECT "+COL_UNLOCK +" FROM "+TABLE_BADGES +" WHERE "+COL_NAME+" LIKE '" +badge_name+"' AND "+COL_UNLOCK +";";
        Cursor e = bdd.rawQuery(qNbEvent,null);

        Log.e("cursor_COUNT_BADGES", "" + e.getCount());
        return (e.getCount()>0) ;
    }
    // convert a String Ressource Name to a res identifier
    private int getImageResourceByName(String resName, Context ctx) {
        int picId = ctx.getResources().getIdentifier(resName, "drawable", ctx.getApplicationContext().getPackageName());
        return picId;
    }
    public int setEventResult(long eventId, String result){
//TODO: Update Table Count
        //numberOfEventTypeDone++;
        // result :
        //          info -> ""
        //          form -> user selection
        //          qcm -> true : good answer found , false : good answer not found
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_DONE, 1); // EVENT DONE
        values.put(COL_EVENT_CONTENT, result); // set unlock date
        // doit envoyé "badge_1" à Badge Obtain Activity
        return bdd.update(TABLE_EVENT, values, COL_ID + " = " + eventId, null);
    }
    public int setEventSent(long eventId){
//TODO: Update Table Count
        //numberOfEventTypeDone++;
        // result :
        //          info -> ""
        //          form -> user selection
        //          qcm -> true : good answer found , false : good answer not found
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_SENT, 1); // EVENT DONE and sent

        // doit envoyé "badge_1" à Badge Obtain Activity
        return bdd.update(TABLE_EVENT, values, COL_ID + " = " + eventId, null);
    }
    /*
    public int updateEmail(TUserInfo userInfo) {

        //La mise à jour d'un badge dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simple préciser quel badge on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, userInfo.getEmail()); // unlock badge
    }*/
    public int updateUserXp(long id, int level, int xp){
        //La mise à jour d'un sport dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simple préciser quelle livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_XP_EARNED, xp);
        values.put(COL_LEVEL,level);
        return bdd.update(TABLE_USERINF, values, COL_ID + " = " + id, null);
    }

    public int removeSportWithID(long id){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_BADGES, COL_ID + " = " + id, null);
    }
    public int getNumberOfBadges(){ // return the number of badges
        return (int)DatabaseUtils.queryNumEntries(bdd, TABLE_BADGES);
    }

    public TBadge getBadgeWithName(String name){
        //Récupère dans un Cursor les valeur correspondant à un BADGE contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        Cursor c = bdd.query(TABLE_BADGES, new String[] {COL_ID, COL_NAME,COL_DESC,COL_UNLOCK,COL_DATE,COL_RNUM}, COL_NAME + " LIKE \"" + name +"\"", null, null, null, null);
        return cursorToBadge(c);
    }
    public TBadge getBadgeWithId(int id){
        //Récupère dans un Cursor les valeur correspondant à un BADGE contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        Cursor c = bdd.query(TABLE_BADGES, new String[] {COL_ID, COL_NAME,COL_DESC,COL_UNLOCK,COL_DATE,COL_RNUM}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return cursorToBadge(c);
    }

    public TUserInfo getUserInfoWithId(int id){
        // only one record for this one
        //Récupère dans un Cursor les valeur correspondant à un BADGE contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        Cursor c = bdd.query(TABLE_USERINF, new String[] {COL_ID, COL_NAME,COL_BIRTHDATE,COL_SEXE,COL_SIZE,COL_WEIGHT,COL_PROFIL,COL_XP_EARNED,COL_LEVEL}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return cursorToUserInfo(c);
    }
    // 12h31 -> 13h moyenne
    public TUserActivity getUserActivityWithTimestamp(String timestamp){
        // only one record for this one
        Log.v("send_timestamp",timestamp);
        //Récupère dans un Cursor les valeur correspondant à un BADGE contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        String qGetActivity =  "SELECT * FROM "+TABLE_USERACTIVITY +" WHERE "+COL_TIME_STAMP +" <= Datetime('"+timestamp+"')";
        String orderClause = " ORDER BY "+ COL_TIME_STAMP+" DESC ;";

        Cursor e = bdd.rawQuery(qGetActivity + orderClause, null);

        //Cursor c = bdd.query(TABLE_USERINF, new String[] {COL_ID, COL_NAME,COL_BIRTHDATE,COL_SEXE,COL_SIZE,COL_WEIGHT,COL_PROFIL,COL_XP_EARNED,COL_LEVEL}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return cursorToUserActivity(e);
    }
    public JSONArray getUserActivitySince(String timestamp)  {
        // only one record for this one
        Log.v("lastAOnServer_ts",timestamp);
        //Récupère dans un Cursor les valeur correspondant à un BADGE contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        String qGetActivity =  "SELECT * FROM "+TABLE_USERACTIVITY +" WHERE "+COL_TIME_STAMP +" >= Datetime('"+timestamp+"')";
        String orderClause = " ORDER BY "+ COL_TIME_STAMP+" DESC ;";

        Cursor e = bdd.rawQuery(qGetActivity+orderClause,null);
        Log.i("countActivity","countA="+e.getCount());
        //Cursor c = bdd.query(TABLE_USERINF, new String[] {COL_ID, COL_NAME,COL_BIRTHDATE,COL_SEXE,COL_SIZE,COL_WEIGHT,COL_PROFIL,COL_XP_EARNED,COL_LEVEL}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return cursorToJson(e);
    }
    public TUserInfo getUserInfos(){
        // only one record for this one
        //Récupère dans un Cursor les valeur correspondant à un BADGE contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        String qGetActivity =  "SELECT * FROM "+TABLE_USERINF ;
        String orderClause = " LIMIT 1;";

        Cursor e = bdd.rawQuery(qGetActivity+orderClause,null);

        //Cursor c = bdd.query(TABLE_USERINF, new String[] {COL_ID, COL_NAME,COL_BIRTHDATE,COL_SEXE,COL_SIZE,COL_WEIGHT,COL_PROFIL,COL_XP_EARNED,COL_LEVEL}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return cursorToUserInfo(e);
    }
    public TGlobalActivity getGlobalActivityWithType(String type){ //type = WDA, WEA , GLO 7, 30 ...

        String qGetActivity =  "SELECT * FROM "+TABLE_GLOBALACTIVITY ;
        String orderClause = " WHERE "+COL_GLO_ACTIVITY_TYPE+" LIKE '" +type+"';";
        Cursor e = bdd.rawQuery(qGetActivity+orderClause,null);
        Log.i("getActivityAll_qry",""+qGetActivity+orderClause);
        Log.i("getActivityAll_bdd",""+e.getCount());
        return cursorToGlobalActivity(e);
    }
    public Cursor getEventsDoneOrNot(Boolean Done){
        // get all the done or not done events
        String clause =((Done)?"":"NOT "); // get all events done or all events not done
        String qGetActivity =  "SELECT * FROM "+TABLE_EVENT +" WHERE "+clause+COL_EVENT_DONE+";";
        Cursor e = bdd.rawQuery(qGetActivity,null);
        return e;
        //return cursorToJson(e);
    }
    public Cursor getEventsDoneByType(String EventType){
        String qGetActivity =  "SELECT * FROM "+TABLE_EVENT +" WHERE "+COL_EVENT_TYPE+" LIKE '" +EventType+"';";
        Log.i("collection_library","query="+qGetActivity);
        Cursor e = bdd.rawQuery(qGetActivity,null);
        return e;
        //return cursorToJson(e);
    }

    public JSONArray getEventsDoneToSent(){
        // get all the done or not done events
        //String clause =((Done)?"":"NOT "); // get all events done or all events not done
        String qGetActivity =  "SELECT " +COL_ID +", "+COL_EVENT_TYPE  +", "+COL_EVENT_CONTENT +" FROM "+TABLE_EVENT +" WHERE "+COL_EVENT_DONE+" AND NOT "+COL_EVENT_SENT +";";
        Cursor e = bdd.rawQuery(qGetActivity,null);
        return cursorToJson(e);
    }
    public int getCountEventDone(){ // get the count of done events
        String qNbEvent =  "SELECT "+COL_ID +" FROM "+TABLE_EVENT +" WHERE "+COL_EVENT_DONE+";";
        Cursor e = bdd.rawQuery(qNbEvent,null);
        Log.e("cursor_COUNT","" +e.getCount());
        //Log.e("cursor", e.getString(e.));
        //Log.e("cursor",e.toString());
        return e.getCount();
    }
    public int getCountEventToSent(){ // get the count of done events
        String qNbEvent =  "SELECT "+COL_ID +" FROM "+TABLE_EVENT +" WHERE NOT "+COL_EVENT_SENT+";";
        Cursor e = bdd.rawQuery(qNbEvent,null);

        //Log.e("cursor", e.getString(e.));
        //Log.e("cursor",e.toString());
        return e.getCount();
    }
//TODO: réécrire en pur sql
    public Float getUserActivityBetween2Dates(String date1, String date2){
        Float userAvgActivity = 0f;
        int countStill = 0;
        Log.v("Received dates ",date1 +" and "+date2);
        String[] splited1 = date1.split(" ");
        String[] splited2 = date2.split(" ");
        String h1 = splited1[1];
        String h2 = splited2[1];
        Date hdeb, hfin; // heure de début et heure de fin
        hdeb = new Date();
        hfin = new Date();
     
        Log.v("hours_splitted","hours1>"+h1+"<and hours 2 >"+h2+"<");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); // récupère juste l'heure
        try {
            hdeb = sdf.parse(h1); // converti sur la même base  = 1 janvier
            hfin = sdf.parse(h2);
            if (h1.equals("23:30:00")) {
                hfin.setDate(hfin.getDate() + 1);
                Log.v("hfin hours changed","to "+hfin.toString());
            }
            Log.v("Received hours ",hdeb +" and "+hfin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Récupère dans un Cursor les valeur correspondant à une activité contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        //String qGetActivity =  "SELECT AVG(" +COL_SUMACC2 +") FROM "+TABLE_USERACTIVITY +" WHERE "+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"')";
        String qGetActivity =  "SELECT " +COL_NAME+", "+COL_TIME_STAMP +" FROM "+TABLE_USERACTIVITY +" WHERE ("+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"')) ";//

        /*String orderClause = " ORDER BY "+ COL_TIME_STAMP+" DESC LIMIT 1;";
        SELECT * FROM test WHERE date*/
        Cursor e = bdd.rawQuery(qGetActivity,null);
        int totalfound = 0;  //= e.getCount();
        try {
            while (e.moveToNext()) {
                try {
                    String hs[] = e.getString(1).toString().split(" ");
                    Date h = sdf.parse(hs[1]);
                    Log.v("parce hours from bdd ",">"+h +"<");
                    Log.i("StringE",">"+e.getString(0)+"<");
                    if ((hdeb.before(h)) &&(hfin.after(h))){ // si l'heure trouvée est dans la tranche horaire on la considère
                        totalfound++; //
                        if (e.getString(0).toString().equals("STILL"))
                            countStill++;
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            e.close();
        }
        if(totalfound >0)
            userAvgActivity = 100 - (float)(100*countStill/totalfound);// 1- nombre de still
        Log.i("Activity Calcul","userAvgAct="+userAvgActivity+" calcul: (countS)"+countStill+"(totalFound)"+totalfound);

        //Cursor c = bdd.query(TABLE_USERINF, new String[] {COL_ID, COL_NAME,COL_BIRTHDATE,COL_SEXE,COL_SIZE,COL_WEIGHT,COL_PROFIL,COL_XP_EARNED,COL_LEVEL}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return userAvgActivity;
    }
    public Float getUserActivityBetween2DatesACC(String date1, String date2){
        Float userAvgActivity = 0f;
        Log.v("Received dates ",date1 +" and "+date2);
        String[] splited1 = date1.split(" ");
        String[] splited2 = date2.split(" ");
        String h1 = splited1[1];
        String h2 = splited2[1];
        Date hdeb, hfin; // heure de début et heure de fin
        hdeb = new Date();
        hfin = new Date();

        Log.v("hours_splitted","hours1>"+h1+"<and hours 2 >"+h2+"<");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); // récupère juste l'heure
        try {
            hdeb = sdf.parse(h1); // converti sur la même base  = 1 janvier
            hfin = sdf.parse(h2);
            if (h1.equals("23:30:00")) {
                hfin.setDate(hfin.getDate() + 1);
                Log.v("hfin hours changed","to "+hfin.toString());
            }
            Log.v("Received hours ",hdeb +" and "+hfin);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String qGetActivity =  "SELECT " +COL_SUMACC2 +", "+COL_TIME_STAMP  +" FROM "+TABLE_USERACTIVITY +" WHERE "+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"')";
        Cursor e = bdd.rawQuery(qGetActivity,null);
        int totalfound = 0;  //= e.getCount();
        try {

            while (e.moveToNext()) {
                try {
                    String hs[] = e.getString(1).toString().split(" ");
                    Date h = sdf.parse(hs[1]);
                    Log.v("parce hours from bdd ", ">" + h + "<");
                    if ((hdeb.before(h)) &&(hfin.after(h))){ // si l'heure trouvée est dans la tranche horaire on la considère
                        totalfound++; //
                        userAvgActivity += e.getFloat(0);
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            e.close();
        }
        if(totalfound >0)
            userAvgActivity /= totalfound;
        Log.i("ACC_Activity Calcul","userAvgAct="+userAvgActivity+" : (totalFound)"+totalfound);

        return userAvgActivity;
    }

    // statistiques
  public long[] getPickOfActivity(int numberOfDays, int mode){// 0 = global , 1= workingdays 2= weekend
        //utiliser le calendrier par défaut
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        //définir le format de la date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        calendar.add(Calendar.DATE, -numberOfDays); // date il y'a numberOfDays en arrière (30 = 1 mois)
        calendar2.add(Calendar.DATE, +1);// on prend un jour de plus pour tenir compte des pas du jour courant

        //Log.i("gUserStep","Il y a 1 mois, nous étions le: "+sdf.format(calendar.getTime()));

        String date1 = sdf.format(calendar.getTime());
        String date2 = sdf.format(calendar2.getTime());
// SELECT strftime('%s','now') %w 0= sunday 1 = monday
          // utiliser date actuelle et date jusqu'ou on veut remonter
        Log.v("gUserActivity dates",date1 +" and "+date2);
        long[] htab = new long[24];
        String weekKind=""; //mode global
        if (mode == 1) // semaine (working days)
            weekKind = " WHERE strftime('%w',+DATETIME("+COL_TIME_STAMP +")) BETWEEN '1' and '5'"; // bien penser à convertir en datetime sinon celà fonctionne pas
        else if(mode == 2) // week end
            weekKind = " WHERE strftime('%w',+DATETIME("+COL_TIME_STAMP +")) ='0' OR strftime('%w',+DATETIME("+COL_TIME_STAMP +")) ='6' "; // 0 (DIMANCHE) ou 6 (SAMEDI)
      // 3 requêtes imbriquées
      // la première filtre les jours souhaités (tout / semaine / week end)
      String qGetRecordsByMode ="SELECT " +COL_TIME_STAMP +", "+COL_NAME+" FROM "+TABLE_USERACTIVITY +weekKind; // que l'actité we ou wd
      // la 2ème compte le nombre d'enregistrement par Heure entre la période donnée
      String qGetActivityByHour = "SELECT COUNT(*) AS numberOfActivityPerHour, strftime('%H',"+COL_TIME_STAMP +") AS sdate FROM ("+qGetRecordsByMode +") WHERE ("+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"'))" +" AND "+COL_NAME +" NOT LIKE 'STILL' GROUP BY sdate ORDER BY sdate ";//
      //String qGetActivityHourMaxPick ="SELECT MAX(numberOfActivityPerHour) as hourPick, sdate FROM ("+qGetActivityByHour +");";

        Cursor e = bdd.rawQuery(qGetActivityByHour,null);
        Log.i("gUserActivity","count_results="+e.getCount() +" for "+mode); // à enlever après


        int hourfound =0;
        while (e.moveToNext()) {
            Log.i("gUserActivity","nombreActivity ="+e.getLong(0));//nombre d'activité
            Log.i("gUserActivity","heure de pic :"+e.getInt(1));//heure de pic
            hourfound = e.getInt(1);
            htab[hourfound] = e.getLong(0);
        }

      //String testquery ="SELECT strftime('%w','now') ;";
      /*Cursor t = bdd.rawQuery(qGetRecordsByMode,null);
      while (t.moveToNext()) {
          Log.i("TUserActivity","TS ="+t.getString(0));//pas cumulé max
          Log.i("TUserActivity","NAME ="+t.getString(1));//pas cumulé max
          Log.i("TUserActivity","weekday ="+t.getString(2));//pas cumulé max

        }*/
      return htab;
    }

    public int getUserStepCountSince(int numberOfDays,int mode){
        //utiliser le calendrier par défaut
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        //définir le format de la date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        calendar.add(Calendar.DATE, -numberOfDays); // date il y'a numberOfDays en arrière (30 = 1 mois)
        calendar2.add(Calendar.DATE, +1);// on prend un jour de plus pour tenir compte des pas du jour courant

        //Log.i("gUserStep","Il y a 1 mois, nous étions le: "+sdf.format(calendar.getTime()));

        String date1 = sdf.format(calendar.getTime());
        String date2 = sdf.format(calendar2.getTime());

        // utiliser date actuelle et date jusqu'ou on veut remonter
        Log.v("gUserStep dates",date1 +" and "+date2);
        String weekKind=""; //mode global = 0
        if (mode == 1) // semaine (working days)
            weekKind = " AND strftime('%w',DATETIME("+COL_TIME_STAMP +")) BETWEEN '1' and '5'"; // bien penser à convertir en datetime sinon celà fonctionne pas
        else if(mode == 2) // week end
            weekKind = " AND (strftime('%w',DATETIME("+COL_TIME_STAMP +")) LIKE '0' OR strftime('%w',DATETIME("+COL_TIME_STAMP +")) LIKE '6')"; // 0 (DIMANCHE) ou 6 (SAMEDI)

        String qGetStep =  "SELECT MAX(" +COL_STEP+") AS highScoreSteps, DATE("+COL_TIME_STAMP +") AS sdate FROM "+TABLE_USERACTIVITY +" WHERE ("+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"'))"+weekKind +" GROUP BY sdate; ";//
        //String qGetStep =  "SELECT " +COL_STEP+" AS highScoreSteps, "+COL_TIME_STAMP +" FROM "+TABLE_USERACTIVITY +" WHERE ("+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"')); ";//

        Cursor e = bdd.rawQuery(qGetStep,null);
        Log.i("gUserStep","count="+e.getCount()); // à enlever après
        Log.i("gUserStep_query",qGetStep);
        int totalfound =0;
        int totalStep = 0;
        while (e.moveToNext()) {
            Log.i("gUserStep","pas max ="+e.getInt(0));//pas cumulé max
            Log.i("gUserStep","jour :"+e.getString(1));//date = jour
            totalStep += e.getInt(0);
            totalfound++;
        }
        return totalStep;
    }
    public int getUserKcalCountSince(int numberOfDays, int mode){
        //utiliser le calendrier par défaut
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        //définir le format de la date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        calendar.add(Calendar.DATE, -numberOfDays); // date il y'a numberOfDays en arrière (30 = 1 mois)
        calendar2.add(Calendar.DATE, +1);// on prend un jour de plus pour tenir compte des pas du jour courant

        String date1 = sdf.format(calendar.getTime());
        String date2 = sdf.format(calendar2.getTime());

        String weekKind=""; //mode global = 0
        if (mode == 1) // semaine (working days)
            weekKind = " AND strftime('%w',DATETIME("+COL_TIME_STAMP +")) BETWEEN '1' and '5'"; // bien penser à convertir en datetime sinon celà fonctionne pas
        else if(mode == 2) // week end
            weekKind = " AND (strftime('%w',DATETIME("+COL_TIME_STAMP +")) ='0' OR strftime('%w',DATETIME("+COL_TIME_STAMP +")) ='6' )"; // 0 (DIMANCHE) ou 6 (SAMEDI)

        Log.v("gUserStep dates",date1 +" and "+date2);
        String qGetStep =  "SELECT MAX(" +COL_KCAL+") AS highScoreKcal, DATE("+COL_TIME_STAMP +") AS sdate FROM "+TABLE_USERACTIVITY +" WHERE ("+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"'))"+weekKind +" GROUP BY sdate; ";//

        Cursor e = bdd.rawQuery(qGetStep,null);
        Log.i("gUserStep","count="+e.getCount()); // à enlever après
        int totalfound =0;
        int totalKcal = 0;
        while (e.moveToNext()) {
            Log.i("gUserStep","kcal max ="+e.getInt(0));//kcal cumulé max
            Log.i("gUserStep","jour :"+e.getString(1));//date = jour
            totalKcal += e.getInt(0);
            totalfound++;
        }
        return totalKcal;
    }
    public Float getUserActivityBetween2Dates_withConfidence(String date1, String date2){
        // basée sur la moyenne des % de probabilité inverse de still
        Float userAvgActivity = 0f;
        Log.v("Received dates ",date1 +" and "+date2);
        String[] splited1 = date1.split(" ");
        String[] splited2 = date2.split(" ");
        String h1 = splited1[1];
        String h2 = splited2[1];
        Date hdeb, hfin; // heure de début et heure de fin
        hdeb = new Date();
        hfin = new Date();

        Log.v("hours_splitted","hours1>"+h1+"<and hours 2 >"+h2+"<");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); // récupère juste l'heure
        try {
            hdeb = sdf.parse(h1); // converti sur la même base  = 1 janvier
            hfin = sdf.parse(h2);
            if (h1.equals("23:30:00")) {
                hfin.setDate(hfin.getDate() + 1);
                Log.v("hfin hours changed","to "+hfin.toString());
            }
            Log.v("Received hours ",hdeb +" and "+hfin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Récupère dans un Cursor les valeur correspondant à une activité contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        //String qGetActivity =  "SELECT AVG(" +COL_SUMACC2 +") FROM "+TABLE_USERACTIVITY +" WHERE "+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"')";
        String qGetActivity =  "SELECT " +COL_NAME+", "+COL_TIME_STAMP +", "+COL_CONFIDENCE +" FROM "+TABLE_USERACTIVITY +" WHERE ("+COL_TIME_STAMP +" BETWEEN Datetime('"+date1+"') AND "+"Datetime('"+date2+"')) ";//

        /*String orderClause = " ORDER BY "+ COL_TIME_STAMP+" DESC LIMIT 1;";
        SELECT * FROM test WHERE date*/
        Cursor e = bdd.rawQuery(qGetActivity,null);
        float sumActivity = 0f;
        int activityProbability;
        int totalfound = 0;  //= e.getCount();
        try {
            while (e.moveToNext()) {
                try {
                    String hs[] = e.getString(1).toString().split(" ");
                    Date h = sdf.parse(hs[1]);
                    Log.v("parce hours from bdd ",">"+h +"<");
                    Log.i("StringE", ">" + e.getString(0) + "<");
                    if ((hdeb.before(h)) &&(hfin.after(h))){ // si l'heure trouvée est dans la tranche horaire on la considère
                        totalfound++; //
                        if (e.getString(0).toString().equals("STILL")) {
                            sumActivity += (100 - e.getInt(2));
                        }else{
                            sumActivity += (e.getInt(2));
                        }
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            e.close();
        }
        if(totalfound >0)
            userAvgActivity = (sumActivity/totalfound);// 1- nombre de still
        Log.i("Activity Calcul_2","userAvgAct="+userAvgActivity+" calcul: (ProbaStill_or_NotStill)"+userAvgActivity+"(totalFound)"+totalfound);

        //Cursor c = bdd.query(TABLE_USERINF, new String[] {COL_ID, COL_NAME,COL_BIRTHDATE,COL_SEXE,COL_SIZE,COL_WEIGHT,COL_PROFIL,COL_XP_EARNED,COL_LEVEL}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return userAvgActivity;
    }

    public JSONArray cursorToJson(Cursor cursor)  {

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("JSONIFY", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public Float getUserActivityBetween2DatesTest(String date1, String date2){
        // only one record for this one
        Float userAvgActivity = 0f;
        Log.v("Received dates ",date1 +" and "+date2);
        //Récupère dans un Cursor les valeur correspondant à un BADGE contenu dans la BDD (ici on sélectionne le BADGE grâce à son NOM)
        String qGetActivity =  "SELECT AVG(" +COL_SUMACC2 +") FROM "+TABLE_USERACTIVITY +" WHERE "+COL_TIME_STAMP +" BETWEEN \""+date1+"\" AND \""+date2+"\"";
        /*String orderClause = " ORDER BY "+ COL_TIME_STAMP+" DESC LIMIT 1;";
        SELECT * FROM test WHERE date*/
        Cursor e = bdd.rawQuery(qGetActivity,null);
        if (e.moveToFirst())
           userAvgActivity = e.getFloat(0);
        else
            userAvgActivity = -1f;
        //Cursor c = bdd.query(TABLE_USERINF, new String[] {COL_ID, COL_NAME,COL_BIRTHDATE,COL_SEXE,COL_SIZE,COL_WEIGHT,COL_PROFIL,COL_XP_EARNED,COL_LEVEL}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return userAvgActivity;
    }
    //Cette méthode permet de convertir un cursor en un badge
    public TBadge cursorToBadge(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();

        //On créé un badge
        TBadge badge = new TBadge();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        badge.setId(c.getLong(NUM_COL_ID));
        badge.setName(c.getString(NUM_COL_NAME));
        badge.setDescription(c.getString(NUM_COL_DESC));
        badge.setUnlock(c.getInt(NUM_COL_UNLOCK)); // INT
        badge.setObtainDate(c.getString(NUM_COL_DATE));
        badge.setResNumber(c.getInt(NUM_COL_RNUM));

        //On ferme le cursor
        c.close();

        //On retourne le badge
        return badge;
    }
    //Cette méthode permet de convertir un cursor en un event
    public TEvent cursorToEvent(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        //Sinon on se place sur le premier élément
        //c.moveToFirst();
        //On créé un event
        TEvent event = new TEvent();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        event.setId(c.getLong(NUM_COL_ID));
        event.setType(c.getString(NUM_COL_EVENT_TYPE));
        event.setName(c.getString(NUM_COL_EVENT_NAME));
        event.setDescription(c.getString(NUM_COL_EVENT_DESCRIPTION));
        event.setValue(c.getString(NUM_COL_EVENT_VALUE));
        event.setContenu(c.getString(NUM_COL_EVENT_CONTENT));
        event.setImage(c.getInt(NUM_COL_EVENT_IMG));
        event.setDone((c.getInt(NUM_COL_EVENT_DONE)) == 1); // cheat to use boolean

        //On ferme le cursor
        //c.close();
        //On retourne l'event
        return event;
    }
    //Cette méthode permet de convertir un cursor en un event
    public TGlobalActivity cursorToGlobalActivity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        //Sinon on se place sur le premier élément
        c.moveToFirst();

        TGlobalActivity globalActivity = new TGlobalActivity();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        globalActivity.setType(c.getString(NUM_COL_GLO_ACTIVITY_TYPE));
        globalActivity.setJson(c.getString(NUM_COL_GLO_ACTIVITY_JSON));
        globalActivity.setLastUpdate(c.getString(NUM_COL_GLO_ACTIVITY_LASTUPDATE));
        globalActivity.setAvgSteps(c.getLong(NUM_COL_GLO_ACTIVITY_AVGSTEPS));
        globalActivity.setAvgKcals(c.getLong(NUM_COL_GLO_ACTIVITY_AVGKCALS));

        return globalActivity;
    }
    //Cette méthode permet de convertir un cursor en INFO UTILISATEUR
    public TUserInfo cursorToUserInfo(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();

        //On créé un badge
        TUserInfo userInfo = new TUserInfo();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        userInfo.setId(c.getLong(NUM_COL_ID));
        userInfo.setName(c.getString(NUM_COL_NAME));
        userInfo.setBirthdate(c.getString(NUM_COL_BIRTHDATE));
        userInfo.setSexe(c.getString(NUM_COL_SEXE));
        userInfo.setSize(c.getInt(NUM_COL_SIZE));
        userInfo.setWeight(c.getInt(NUM_COL_WEIGHT));
        userInfo.setProfile(c.getInt(NUM_COL_PROFILE));
        userInfo.setXp(c.getInt(NUM_COL_XP_EARNED));
        userInfo.setLevel(c.getInt(NUM_COL_LEVEL));
       // userInfo.setEmail(c.getString(NUM_COL_EMAIL));

        //On ferme le cursor
        c.close();

        //On retourne le badge
        return userInfo;
    }
    //Cette méthode permet de convertir un cursor en INFO UTILISATEUR
    public TUserActivity cursorToUserActivity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();

        //On créé un badge
        TUserActivity userActivity = new TUserActivity();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        userActivity.setName(c.getString(NUM_COL_NAME_ACTIVITY));
        userActivity.setConfidence(c.getInt(NUM_COL_CONFIDENCE));
        userActivity.setSumAcc2(c.getFloat(NUM_COL_SUMACC2));
        userActivity.setX(c.getFloat(NUM_COL_X));
        userActivity.setY(c.getFloat(NUM_COL_Y));
        userActivity.setZ(c.getFloat(NUM_COL_Z));
        userActivity.setLat(c.getFloat(NUM_COL_LAT));
        userActivity.setLong(c.getFloat(NUM_COL_LONG));
        userActivity.setTimestamp(c.getString(NUM_COL_TIME_STAMP));
        userActivity.setTimezone(c.getString(NUM_COL_TIME_ZONE));
        userActivity.setNod((c.getInt(NUM_COL_NOD)==1)); //CHEAT to use boolean
        userActivity.setStep(c.getInt(NUM_COL_STEP));
        userActivity.setKcal(c.getInt(NUM_COL_KCAL));
        //On ferme le cursor
        c.close();
        //On retourne le badge
        return userActivity;
    }
    public void setBddDate(String dateBdd){
        this.dateBdd = dateBdd;
    }
    private String getTimeStamp(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd' 'HH:mm:ss");
        return format.format(cal.getTime());

        //		return "["+cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+
        //                " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"]";
    }


}

