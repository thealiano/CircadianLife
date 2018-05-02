package com.example.aliano.clientcircadian_va.ActivityRecognition;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Config.ConfigurationParameters;
import com.example.aliano.clientcircadian_va.Data.AccelerometerData;
import com.example.aliano.clientcircadian_va.SensorProviders.AccelerometerProvider;
import com.example.aliano.clientcircadian_va.SensorProviders.ProviderInterface;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TUserActivity;
import com.example.aliano.clientcircadian_va.SunriseAndSet.SunriseSunsetCalculator;
import com.example.aliano.clientcircadian_va.SunriseAndSet.dto.Location;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.pathsense.android.sdk.location.PathsenseDetectedActivities;
import com.pathsense.android.sdk.location.PathsenseDetectedActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Aliano on 07/06/2016.
 *
 * Problems : android 6.0 marshmallow (have to ask the permission at each write on the file
 */

public class ActivityRecognitionService extends IntentService {
    // to get accelerometer datas
    static private SensorManager sensorManager;
    static AccelerometerProvider accelProvider;
    static private ArrayList<SensorEventListener> availableSensorList;

    static Context ctx;
    float MET_indice = 0; // activity indice

    // for pathsense
    static final int MESSAGE_ON_ACTIVITY_UPDATE = 1;
    InternalActivityUpdateReceiver mActivityUpdateReceiver;
    static InternalHandler mHandler;
    static final long frequencyMillis = 60000; // doit pouvoir être changé sur le serveur
    static AtomicBoolean isRunning = new AtomicBoolean(false) ; // false

    // class to handle and record activity recognition
    /* log plus possible simplement depuis marshmallow */
// TODO: REMPLACER PAR GENERATION DE CSV VIA LA database locale
    String filename = "test_circadian_activity_save_14.txt";

    File root = android.os.Environment.getExternalStorageDirectory(); // only works with version < marshmallow

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    public ActivityRecognitionService(String name) {
        super(name);
    }

    // Internal Handler to process activity when update
    static class InternalActivityUpdateReceiver extends BroadcastReceiver {
        ActivityRecognitionService mActivity;

        //
        InternalActivityUpdateReceiver(ActivityRecognitionService activity) {
            mActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final ActivityRecognitionService activity = mActivity;
            final InternalHandler handler = activity != null ? activity.mHandler : null;
            //
            if (activity != null && handler != null ) {

                PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) intent.getSerializableExtra("detectedActivities");
                Message msg = Message.obtain();
                msg.what = MESSAGE_ON_ACTIVITY_UPDATE;
                msg.obj = detectedActivities;
                handler.sendMessage(msg);

            }
        }
    }
    static class InternalHandler extends Handler // static = une seule instance
    { // définit un nouveau handler qui recoit les updates envoyé par le broadcast
        // va définir quoi faire au traitement d'une activité
        static InternalHandler instance;

        //
        private InternalHandler()//ActivityRecognitionService activity)
        {
            // mActivity = activity;
            InternalHandler.instance = this;
        }

        public static InternalHandler getInstance() {
            if (instance == null) instance = new InternalHandler();
            return instance;
        }

        @Override
        public void handleMessage(Message msg) {
            //final ActivityRecognitionService activity = mActivity;
            String textDetectedActivity0, textStationary, name,s_confidence;
            int confidence = 0;
            Log.i("ACTIVITY_UPDATE_HMsg", "enter The method");

            PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) msg.obj;

            PathsenseDetectedActivity mostProbableActivity = detectedActivities.getMostProbableActivity();
            if (mostProbableActivity != null) {
                List<PathsenseDetectedActivity> detectedActivityList = detectedActivities.getDetectedActivities();
                StringBuilder detectedActivityString = new StringBuilder(mostProbableActivity.getDetectedActivity().name());

                // accéléromètre
                Float[] accdatas = new Float[3];
                accdatas[0] = 0f;
                accdatas[1] = 0f;
                accdatas[2] = 0f;
                float sumacc2 = 0.00f;
                accdatas = getAccData();

                // préférences partagées
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
                long last_ts_millis = sharedPref.getLong("lastRecordMillis",0);
                Float shared_locationLat = sharedPref.getFloat("locationlat", 46.5333f);
                Float shared_locationLong = sharedPref.getFloat("locationlong", 6.6668f);

                Location lastposition = new Location(shared_locationLat,shared_locationLong);
                if (accdatas !=null) { // record if the acccelerometer is set
                    // chaque valeur est au carré - gravité pour avoir l'accélération indépendement de celle-ci
                    sumacc2 = (accdatas[0] * accdatas[0]) + (accdatas[1] * accdatas[1]) + (accdatas[2] * accdatas[2]); // magnitude
                    sumacc2 = (float) Math.sqrt((double) sumacc2);
                    sumacc2 -= ConfigurationParameters.GRAVITY_CONSTANT;
                }
                // concerne les pas et les calories cumulées
                String last_ts = sharedPref.getString("lastRecord",""); // last record du compteur de pas
                int shared_steps = 0;
                int shared_kcal = 0;
                // dans le cas ou le détécteur d'activité se lance avant que le compteur de pas n'ai détécté un changement de jour
                Log.d("ActivityReco","lts="+last_ts+";"+getDate() +";"+(getDate().equals(last_ts)));
                if(getDate().equals(last_ts)) {
                    // si la date n'a pas changé depuis la dernière mise à jour du service de compteur de pas
                    // on enregistre le nombre de pas cumulé
                    // sinon on laisse à 0
                    shared_steps = Integer.valueOf(sharedPref.getString("numSteps", "0"));
                    shared_kcal = Math.round(sharedPref.getFloat("numKcal",0f));
                }

                //float met_indice = sharedPref.getFloat("MET_indice",0.9f);
                SharedPreferences.Editor editor = sharedPref.edit();

                name = detectedActivityString.toString();
                if(name.equals("ON_FOOT")){ // precise
                    name = walkingOrOnFoot(detectedActivityList);
                }
                confidence = (int) (100 * mostProbableActivity.getConfidence());
                s_confidence = Double.toString(mostProbableActivity.getConfidence());
                float indice_met = activityToMet(name);
                editor.putFloat("indiceMET",indice_met);
                editor.commit(); // apply > delay to background / commit -> immediatly

                int numDetectedActivityList = detectedActivityList != null ? detectedActivityList.size() : 0;
                if (numDetectedActivityList > 0) {
                    DecimalFormat decimalF = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "US"));
                    decimalF.applyPattern("0.00000");
                    //
                    StringBuilder detectedActivityStringList = new StringBuilder();
                    for (int i = 0; i < numDetectedActivityList; i++) {
                        PathsenseDetectedActivity detectedActivity = detectedActivityList.get(i);
                        if (i > 0) {
                            detectedActivityStringList.append("<br />");
                        }
                        detectedActivityStringList.append(detectedActivity.getDetectedActivity().name() + " " + decimalF.format(detectedActivity.getConfidence()));
                    }
                    textStationary = detectedActivities.isStationary() ? "STATIONARY" : "NOT STATIONARY";
                    textDetectedActivity0 = Html.fromHtml(detectedActivityStringList.toString()).toString();
                    Log.i("ACTIVITY_UPDATE", "stationary:" + textStationary + " list:" + textDetectedActivity0 +"ts:"+getTimeStamp());
                    Log.i("Activity_String", msg.toString());
                    // préférences partagées

                    long mseconds =  System.currentTimeMillis(); // number of milliseconds since January 1, 1970 00:00:00 UTC
                    // /!\ ne pas convertir en date

                    // Log.i("record","mseconds-f:"+(mseconds - last_ts) +mostProbableActivity.toString());
                    if((mseconds - last_ts_millis) > frequencyMillis) { // 60000 = 1minute
                        editor.putLong("lastRecordMillis",mseconds);
                        editor.commit(); // apply delay, commit is immediate
                        Log.i("record",getTimeStamp() +mostProbableActivity.toString() );
                        //if(name=="IN_VEHICLE") name = "IN_VEHICULE";
                        writeToLocalDatabase(name,confidence,sumacc2,accdatas[0],accdatas[1],accdatas[2],shared_locationLat,shared_locationLong,NightOrDay(lastposition), shared_steps, shared_kcal);
                        Log.v("SqLiteWrite","name:"+name +",confidence:"+confidence+",sAcc:"+sumacc2+",accDATAS:"+accdatas[0]+","+accdatas[1]+","+accdatas[2]+",Coord:"+shared_locationLat+","+shared_locationLong+"steps:"+shared_steps+" kcal:"+shared_kcal+"nod: "+NightOrDay(lastposition));
                        Log.v("ARecognitionService",getTimeStamp()+":"+sumacc2);

                        //enregistrement toutes les minutes
                       // writeToFileExternal(name+";"+confidence+";"+getTimeStamp()+";"+indice_met);
                        //writeTodb(name,confidence,sumacc2,accdatas[0],accdatas[1],accdatas[2],shared_locationLat,shared_locationLong,NightOrDay(lastposition), shared_steps, shared_kcal);
                    }
                }
            } else {
                name = "";
            }
            Log.i("ACTIVITY_CHANGE", "result:" + name);
        }
    }
    static private String walkingOrOnFoot(List<PathsenseDetectedActivity> detectedActivityList) {
        // précise l'activité ONFOOT
        int numDetectedActivityList = detectedActivityList != null ? detectedActivityList.size() : 0;
        String refineActivity ="ON_FOOT";
        for (int i = 0; i < numDetectedActivityList; i++) {
            PathsenseDetectedActivity detectedActivity = detectedActivityList.get(i);
            if( detectedActivity.getDetectedActivity().name() == "WALKING")
                refineActivity = detectedActivity.getConfidence() > 0 ? "WALKING" : "ON_FOOT";

        }return refineActivity;
    }
    static private Float activityToMet(String name){
        // convertir une activité dans un indice d'activité MET
        switch(name) {
            case "STILL" :
                return 0.9f;
            case "TILTING" :
                return 1.1f;
            case "HOLDING" :
                return 1f;
            case "IN_VEHICLE" :
                return 3f;
            case "ON_FOOT" :
                return 2f;
            case "WALKING" :
                return 3.5f;
            default : // UNKNOW
                return 1f;
        }
    }

    @Override
    public void onCreate(){
        if (!isRunning.get()) { // si le service n'est pas déjà en train de tourner
            super.onCreate();
            ctx = getApplicationContext();
            Log.e("ARS", "onCreate");
            // partie capteur : récupération accelerometre
            Log.i("AR_service","Starting...");
            availableSensorList = new ArrayList<SensorEventListener>();
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            getAccelerotmeterSensor(); // add sensor to the list
            if(availableSensorList == null || availableSensorList.isEmpty()){
                Log.w("ARecognitionService", "availableSensorList is null or empty");
            } else {
                for(SensorEventListener prov : availableSensorList){ // enregistrement des capteurs sur le gestionnaire
                    sensorManager.registerListener(prov, ((ProviderInterface)prov).getSensor(), SensorManager.SENSOR_DELAY_FASTEST);
                /*if(prov instanceof LightProvider){
                    sensorManager.registerListener(prov, ((LightProvider)prov).getProximitySensor(), SensorManager.SENSOR_DELAY_NORMAL);
                }*/
                }
            }
            // partie pathSense
            mHandler = InternalHandler.getInstance();
            // receivers
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this); // recoit les broadcast
            mActivityUpdateReceiver = new InternalActivityUpdateReceiver(this);
            localBroadcastManager.registerReceiver(mActivityUpdateReceiver, new IntentFilter("activityUpdate"));
            //  createNotification("Activity Recognition Service Destroyed");
            Log.i("AR_service", "Starting...");
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // to be sure the service is not started more than once
        if (!isRunning.get()) {
            Log.i("ARS","first start"+isRunning.toString());
            isRunning.set(true);
            return super.onStartCommand(intent, flags, startId);
        }else{
            Log.i("ARS","BLOCKED - SERVICE ALREADY STARTED");
            return ActivityRecognitionService.START_NOT_STICKY; // pas sur
        }
    }

    public void onDestroy(){
        super.onDestroy();
        Log.i("ARS", "Destroyed...");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("AR_Service", "onHandleIntent?");
        // NE PASSE JAMAIS ICI, la redefinition du handler fait qu'il est toujours occupé

        //TODO: ENLEVER WAKE LOCK DANS LES PERMISSIOMS
    }
    static private boolean NightOrDay(Location currentLocation){
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(currentLocation, TimeZone.getDefault().getID().toString());
        long Sunrise = calculator.getOfficialSunriseCalendarForDate(Calendar.getInstance()).getTimeInMillis();
        long Sunset = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance()).getTimeInMillis();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Log.i("NOD","value="+((currentTime > Sunrise) && (currentTime < Sunset)));
        return (( currentTime > Sunrise) && (currentTime < Sunset));
        // 0 = nuit 1 = jour
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
// to do : vider la database quand y'a trop de données
    static public void writeToLocalDatabase(String name, int confidence, float sumAcc2,float x, float y, float z,float plat,float plong, boolean NoD, int step, int kcal ){
        //open local bdd
        CircadianLocalBDD localBdd = new CircadianLocalBDD(ctx);
        localBdd.open(); //On ouvre la base de données pour écrire dedans
        // create informations for Database
        TUserActivity userActivity =new TUserActivity(name, confidence,sumAcc2,x,y,z,plat, plong, NoD, step, kcal);

        Log.v("ARecognitionService", userActivity.toString());

        Log.v("SqlWrite", "ts_outside =" + getTimeStamp());
        //Log.v("ARecognitionService", );
        localBdd.insertUserActivity(userActivity); // Set User basic Informations
        localBdd.close(); //On ferme la base de données
    }
    public void writeToFileExternal(String textToWrite){
        Log.v("WriteFile", "\nExternal file system root: " + root);
        //if (isExternalStorageWritable()) {
        File dir = new File(root.getAbsolutePath() + "/circadian_app");
        dir.mkdirs();
        File file = new File(dir, filename);
        //}
        try {
            FileOutputStream f = new FileOutputStream(file,true);// true = append mode
            PrintWriter pw = new PrintWriter(f);
            pw.println(getTimeStamp() + ";" +textToWrite+";");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("WriteFile", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("WriteFile", "\n\nFile written to " + file);
    }
    // Method to get sensors (add 1 method for each sensor needed).
    static private void getAccelerotmeterSensor() {
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(accelSensor != null){
            Log.w("ARecognitionService", "Sensor.TYPE_ACCELEROMETER Available");
            accelProvider = AccelerometerProvider.getInstance();
            accelProvider.setSensorMng(sensorManager);
            availableSensorList.add(accelProvider);// adding Provider to the list of available sensor

        } else {
            Log.w("ARecognitionService", "Sensor.TYPE_ACCELEROMETER NOT Available");
        }
    }

    static public Float[] getAccData(){
        AccelerometerProvider accProv;
        AccelerometerData data;
        //ProviderInterface prov;
        Float[] data_acc = new Float[3];
        /*data_acc[0]=0.0f;
        data_acc[1]=0.0f;
        data_acc[2]=ConfigurationParameters.GRAVITY_CONSTANT;*/
        if (availableSensorList == null || availableSensorList.isEmpty()) {
            Log.w("TriggerSensorManager", "availableSensorList is null or empty");
        } else {
            for (SensorEventListener sensEvt : availableSensorList) {
                if (sensEvt instanceof AccelerometerProvider){
                    accProv = (AccelerometerProvider) sensEvt;
                    data = (AccelerometerData)accProv.getData();
                    data_acc = data.getData();

                } else {
                    Log.w("TriggerSensorManager", "availableSensorList is null or empty");
                }
            }
        }
        return data_acc;
    }
    static private String getTimeStamp() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        return format.format(cal.getTime());

    }
    static private String getDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(cal.getTime());
        //		return "["+cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+
        //                " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"]";
    }
}