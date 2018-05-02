package com.example.aliano.clientcircadian_va.SqLiteDatabase;

/***********************************************
 * Created by Aliano on 23.11.2015.
 ***********************************************/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MaBaseSqLite extends SQLiteOpenHelper {
    // BASE CREATION
    // Table TBadges
    private static final String TBadges = "Badges", TUserInfo = "UserInfo", TUserActivity = "UserActivity", TEvent="Event",TGlobalActivity="GlobalActivity"; //
    private static final String COL_ID = "ID";
    private static final String COL_NAME= "Name";
    private static final String COL_DESC= "Description";
    private static final String COL_RNUM= "ResNumber";
    // Table User Information
    private static final String COL_PROFIL = "Profil",COL_XP_EARNED = "XpEarned",COL_LEVEL = "Level",COL_UNLOCK = "IsUnlock";
    private static final String COL_DATE ="Date";
    private static final String COL_BIRTHDATE ="BirthDate";
    private static final String COL_SEXE ="Sexe";
    private static final String COL_WEIGHT ="Weight";
    private static final String COL_SIZE ="Size";
   // private static final String COL_EMAIL ="Email";

    // Table User Activity
    // 0 = name
    private static final String COL_CONFIDENCE ="Confidence";
    private static final String COL_SUMACC2 ="SumAccelerometer2";
    private static final String COL_X ="X";
    private static final String COL_Y ="Y";
    private static final String COL_Z ="Z";
    private static final String COL_LAT ="Latitude";
    private static final String COL_LONG ="Longitude";
    public static final String COL_TIME_STAMP = "Timestamp";
    public static final String COL_TIME_ZONE = "Timezone";
    public static final String COL_NOD = "NightOrDay";
    public static final String COL_STEP = "Step";
    public static final String COL_KCAL = "Kcal";

    // Table Event
    private static final String COL_EVENT_TYPE ="EventType";
    private static final String COL_EVENT_NAME ="EventName";
    private static final String COL_EVENT_DESCRIPTION ="EventDescription";
    private static final String COL_EVENT_VALUE ="EventValue";
    private static final String COL_EVENT_CONTENT ="EventContent";
    private static final String COL_EVENT_IMG ="EventImg";
    public static final String COL_EVENT_DONE = "EventDone";
    public static final String COL_EVENT_SENT = "EventSent";

    // Table Global Activity
    private static final String COL_GLO_ACTIVITY_TYPE ="Type";
    private static final String COL_GLO_ACTIVITY_JSON ="Json";
    private static final String COL_GLO_ACTIVITY_LASTUPDATE ="LastUpdate";
    private static final String COL_GLO_ACTIVITY_AVGSTEPS ="Avg_steps";
    private static final String COL_GLO_ACTIVITY_AVGKCALS ="Avg_kcals";

    private static final String CREATE_TBadges = "CREATE TABLE IF NOT EXISTS " + TBadges + " ("
            + COL_ID + " LONG PRIMARY KEY, "
            + COL_NAME + " TEXT NOT NULL, "
            + COL_DESC +" TEXT NOT NULL, "
            + COL_UNLOCK +" INTEGER NOT NULL, "
            + COL_DATE +" TEXT, " // can be null it's the obtain date
            + COL_RNUM +" INTEGER NOT NULL);";

    private static final String CREATE_TEvent = "CREATE TABLE IF NOT EXISTS " + TEvent + " (" // ajouter la date
            + COL_ID + " LONG PRIMARY KEY, "
            + COL_EVENT_TYPE +" TEXT NOT NULL, "
            + COL_EVENT_NAME + " TEXT NOT NULL, "
            + COL_EVENT_DESCRIPTION +" TEXT, " // can be null
            + COL_EVENT_VALUE +" TEXT NOT NULL, "
            + COL_EVENT_CONTENT +" TEXT, " // can be null (in case of information)
            + COL_EVENT_IMG + " INTEGER, "
            + COL_EVENT_DONE + " BOOLEAN NOT NULL DEFAULT 0, "
            + COL_EVENT_SENT + " BOOLEAN NOT NULL DEFAULT 0);";

    private static final String CREATE_TUserActivity = "CREATE TABLE IF NOT EXISTS " + TUserActivity + " (" // ajouter la date
            //+ COL_ID + " LONG PRIMARY KEY, "
            + COL_NAME + " TEXT NOT NULL, " // activity recognition name
            + COL_CONFIDENCE +" INTEGER NOT NULL, "
            + COL_SUMACC2 +" FLOAT NOT NULL, " // SUM of the accelerometer axis
            + COL_X +" FLOAT NOT NULL, " // SUM of the accelerometer axis
            + COL_Y +" FLOAT NOT NULL, " // SUM of the accelerometer axis
            + COL_Z +" FLOAT NOT NULL, " // SUM of the accelerometer axis
            + COL_LAT +" FLOAT, " // can be null if not active
            + COL_LONG +" FLOAT, "
            + COL_TIME_STAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + COL_TIME_ZONE + " TEXT, "
            + COL_NOD +" BOOLEAN NULL, "
            + COL_STEP +" INT, "
            + COL_KCAL +" INT"
            +");";
    private static final String CREATE_TUserInfo = "CREATE TABLE IF NOT EXISTS " + TUserInfo + " (" // ajouter la date
            + COL_ID + " LONG PRIMARY KEY, "
            + COL_NAME + " TEXT NOT NULL, "
            + COL_BIRTHDATE +" TEXT NOT NULL, "
            + COL_SEXE +" TEXT NOT NULL, "
            + COL_WEIGHT +" INTEGER NOT NULL, "
            + COL_SIZE +" INTEGER NOT NULL, "
            + COL_PROFIL + " INTEGER NOT NULL, "
            + COL_XP_EARNED + " LONG NOT NULL, "
            + COL_LEVEL + " INTEGER NOT NULL);";
// activité de la population
    private static final String CREATE_TGlobalActivity = "CREATE TABLE IF NOT EXISTS " + TGlobalActivity + " ("
            + COL_GLO_ACTIVITY_TYPE + " TEXT PRIMARY KEY, "
            + COL_GLO_ACTIVITY_JSON +" TEXT NOT NULL, "
            + COL_GLO_ACTIVITY_LASTUPDATE + " DATETIME NOT NULL, "
            + COL_GLO_ACTIVITY_AVGSTEPS +" LONG NOT NULL, " // can be null
            + COL_GLO_ACTIVITY_AVGKCALS +" LONG NOT NULL, "
            +"UNIQUE ("+COL_GLO_ACTIVITY_TYPE +") ON CONFLICT REPLACE );";
            // dès qu'il y'a un conflit on remplace la valeur précédente par la nouvelle recue
            // très utile pour la synchronisation de donnée mais nécessite de bien faire attention lors de la reception de nouvelles données
            //If a row an INSERT or UPDATE statement tries to add a row with an id which already exists, the existing row is replaced with the new one.


    public MaBaseSqLite(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on créé la table à partir de la requête écrite dans la variable CREATE_BDD

        db.execSQL(CREATE_TBadges); // create table BADGES
        db.execSQL(CREATE_TUserInfo); // create table User Info
        db.execSQL(CREATE_TUserActivity); // create table U ser activity
        db.execSQL(CREATE_TEvent); // create table for events
        db.execSQL(CREATE_TGlobalActivity); // create table global activity
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) { // create if not exist
            db.execSQL(CREATE_TBadges); // create table BADGES
            db.execSQL(CREATE_TUserInfo); // create table User Info
            db.execSQL(CREATE_TUserActivity); // create table U ser activity
            //db.execSQL("DROP TABLE " + TEvent + ";");
            db.execSQL(CREATE_TEvent); // create table for events
            //db.execSQL("DROP TABLE " + TGlobalActivity + ";");
            db.execSQL(CREATE_TGlobalActivity); // create table global activity


        }
// to do : methode de mise à jour des badges (entre autre)
       // db.execSQL("CREATE TABLE IF NOT EXISTS");
        /*
        clearDb(db); // suppress tabla
        onCreate(db); // and recreate it
        */
    }
    public void clearDb(SQLiteDatabase db){
        db.execSQL("DROP TABLE " + TBadges + ";");
        db.execSQL("DROP TABLE " + TUserInfo + ";");
        db.execSQL("DROP TABLE " + TUserActivity + ";");
        db.execSQL("DROP TABLE " + TEvent + ";");
        db.execSQL("DROP TABLE " + TGlobalActivity + ";");
    }

}
