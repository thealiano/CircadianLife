package com.example.aliano.clientcircadian_va.Config;


import com.example.aliano.clientcircadian_va.R;

/**
 * Created by John on 18.05.2015.
 */
public class ConfigurationParameters {

    // ADDRESS AND PORT OF THE SERVER
    public static final String HOST_NAME =  "128.178.244.4";//"128.179.161.2";//"128.178.244.4";// adresse du vrai serveur "128.178.244.4"; //"37.187.180.173";
    // ONLY IP WITHOUT HTTP:// ALSO VERIFY THAT DEVICE AND SERVER ARE ON THE SAME NETWORK (epfl for both)

    public static final String HOST_FILE = "/CircadianServer/";//avec network: chemin a partir de var/www/
    //public static final String HOST_FILE = "/circadianServerTest/";//avec localhost: /circadianServerTest/";

    public static final int PORT = 6524; //6524 //4444; //ap :1900

    // SERVER PHP SCRIPT
    public static final String NEW_USER_SCRIPT  = "newUser.php";
    public static final String GET_LAST_ACTIVITY_SCRIPT  = "getLastActivity.php";
    public static final String RECORD_ACTIVITY_SCRIPT ="record_activity_new.php";
    public static final String IS_EMAIL_SET ="isSetEmail.php";
    public static final String RECORD_EMAIL ="record_email.php";
    public static final String RECORD_EVENTS_SCRIPT="record_events_answers.php";
    public static final String GET_EVENTS="getEvents.php";
    public static final String GET_GLOBALACTIVITY="getLastActivityAll.php";
    public static final String DDL_DATAS="downloadDB.php";
    // TITLE OF THE SCREENS
    public static final int HOME_TITLE = R.string.home_title;
    public static final int FORMS_LIST_TITLE = R.string.forms_list_title;
    public static final int INFORMATION_TITLE = R.string.informations_title;
    public static final int MY_STATS_TITLE = R.string.my_stats_title;

    // FREQUENCY OF THE COLLECTED DATA IN MILLISECONDS 120000 = every 2min 900'000
    public static final int COLLECT_DATA_INTERVALL = 90000;// 120000 2min //900000  15min // 60000 1min // 10000 10s

    // FORMS ID
    public static final int DAILY_FORM_ID = 1;
    public static final int MCTQ_LIKE_ID = 2;
    public static final int THOROUGH_PROFILE_ID = 3;
    public static final int MUSIC_PROFILE_ID = 4;
    public static final int EPFL_UNIL_FORM_ID = 5;


    // FORMS SELECTOR ID
    public static final int THOROUGH_PROFILE_SELECTOR = 10;
    public static final int MCTQ_LIKE_SELECTOR = 11;
    public static final int DAILY_FORM_SELECTOR = 12;
    public static final int EPFL_UNIL_FORM_SELECTOR = 14;

    // SETTINGS SELECTOR ID
    public static final int SETTINGS_SELECTOR = 100;
    //SETTINGS TITLE
    public static final int SETTINGS_TITLE = R.string.action_settings;

    // DURATION OF THE DIFFERENTS FORM IN SECONDS
    public static final int DAILY_FORM_DURATION = 60;
    public static final int MCTQ_LIKE_FORM_DURATION = 60;
    public static final int THOROUGH_PROFILE_FORM_DURATION = 60;
    public static final int MUSIC_PROFILE_FORM_DURATION = 60;
    public static final int EPFL_UNIL_FORM_DURATION = 60;

    //NAMING CONVENTION FOR DATA. THESE HAVE TO BE RESPECTED FOR THE JSON FORMAT
    public static final String LIGHT_DATA_TAG = "light_data";
    public static final String MAGNETIC_FIELD_DATA_TAG = "magnetic_field_data";
    public static final String PRESSURE_DATA_TAG = "pressure_data";
    public static final String AMBIENT_TEMPERATURE_DATA_TAG = "ambient_temperature_data";
    public static final String RELATIVE_HUMIDITY_DATA_TAG = "relative_humidity_data";
    public static final String STEP_COUNTER_DATA_TAG = "step_counter_data";
    public static final String ACCELEROMETER_DATA_TAG = "accelerometer_data";
    public static final String SOUND_DATA_TAG = "sound_data";
    public static final String POSITIOIN_DATA = "position_data";

    //GOOGLE ACTIVITY RECOGNITION
    public static final int ACTIVITY_REFRESH_TIME_MILLIS = 30000; // time between activity detection in millis
    public static final float GRAVITY_CONSTANT = 9.80665f;
}
