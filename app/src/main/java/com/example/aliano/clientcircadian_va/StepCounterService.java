package com.example.aliano.clientcircadian_va;
import com.example.aliano.clientcircadian_va.SensorProviders.SoftwarePodometer.StepDetector;
import com.example.aliano.clientcircadian_va.SensorProviders.SoftwarePodometer.StepListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class StepCounterService extends Service implements StepListener, SensorEventListener {

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private ArrayList<SensorEventListener> availableSensorList;
    private ArrayList<Runnable> availableManualSensorList;
    private Thread serviceThread;
    int stepCounter;
    float kcalCounter;
    String last_ts;
    Context ctx;

    // Local Broadcast Manager
    LocalBroadcastManager broadcaster; // local broadcaster
    static final public String STEP_RESULT = "StepCounterService_REQUEST_PROCESSED";
    static final public String STEP_MESSAGE = "StepCounterService_steps";
    static final public String KCAL_MESSAGE = "StepCounterService_kcals";

    //HardwareStepCounterProvider stepCounterProvider;
    public StepCounterService() {
    }

    @Override
    public void onCreate() {
        Log.i("START_SERVICE", "STEPCOUNTER");
        ctx = getApplicationContext();
        // get previous result
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String shared_steps = sharedPref.getString("numSteps", "0");
        stepCounter = Integer.parseInt(shared_steps); // get previous step
        kcalCounter = sharedPref.getFloat("numKcal", 0f); // get previous kcal
        last_ts = sharedPref.getString("lastRecord", "");
        Log.i("Get Date vs last ts","date:"+getDate() +" , lastts: "+last_ts);
        if(!last_ts.equals("")){ // il y'a eu un enregistrement avant
            if(!getDate().equals(last_ts)){ // changement de jour
                // si c'est un enregistrement qui n'est pas  du même jour
                // on l'enregistre dans la base locale et on remet à 0
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("numSteps", "0");
                editor.putFloat("numKcal", 0f);
                editor.putString("lastRecord", getDate());
                editor.commit();
            }
        }

        broadcaster = LocalBroadcastManager.getInstance(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy() {
        //sensorManager.unregisterListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *    *************  GET SENSOR METHODS
     */
    public void sendResult(String step, String kcal) {
        Intent intent = new Intent(STEP_RESULT);
        if(step != null){
            intent.putExtra(STEP_MESSAGE, step);
            intent.putExtra(KCAL_MESSAGE, kcal);
        }
        broadcaster.sendBroadcast(intent);
    }

    private String getDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(cal.getTime());
        //		return "["+cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+
        //                " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"]";
    }

    @Override
    public void step(long timeNs) { // action a faire à chaque pas
        stepCounter++;
        kcalCounter += 0.033f; // a modifier par  : += 1/3600 * poids * nbMET

//TODO: S'ASSURER QUE LA TAILLE EST CORRECTEMENT INSEREE ET LA récupérée en(sharedpref)
        // ajouter distance parcourue :taille / 2,85 = foulée moyenne (en m) d = nb_pas * foulee_moyenne
        // register in shared preference with timestamp and kcal
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        last_ts = sharedPref.getString("lastRecord","");
        //SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(!getDate().equals(last_ts)) { // changement de date remise des pas/kcal à 0
            stepCounter = 0;
            kcalCounter = 0f;
            // puis enregistré une entrée daily
        }

        String sCounter = ""+stepCounter;
        String skCAL = ""+ Math.round(kcalCounter); // arrondis pour l'affichage
        sendResult(sCounter, skCAL);
        editor.putString("numSteps", sCounter);
        editor.putFloat("numKcal", kcalCounter);
        editor.putString("lastRecord", getDate());
        editor.commit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
