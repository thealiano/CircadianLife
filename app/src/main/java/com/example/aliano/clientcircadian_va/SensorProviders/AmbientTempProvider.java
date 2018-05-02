package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Data.AmbientTempData;
import com.example.aliano.clientcircadian_va.Data.Data;


/**
 * Created by John on 20.05.2015.
 */
public class AmbientTempProvider implements SensorEventListener, ProviderInterface {
    private static AmbientTempProvider INSTANCE = new AmbientTempProvider();
    private SensorManager sensorMng;
    private Sensor ambientTempSensor;
    private AmbientTempData data;
    private float ambientTemp_mean;
    private int numberOfPicks;

    private AmbientTempProvider(){
        // private contructor for the singleton
        data = new AmbientTempData();
        ambientTemp_mean = 0.0f;
        numberOfPicks = 0;
    }

    public static AmbientTempProvider getInstance(){
        return INSTANCE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            // values[0]: ambient (room) temperature in degree Celsius
            numberOfPicks++;
            ambientTemp_mean = (event.values[0]+(ambientTemp_mean*(numberOfPicks-1)))/numberOfPicks;
            data.setData(ambientTemp_mean);
        }
    }

    @Override
    public Sensor getSensor() {
        return ambientTempSensor;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setSensorMng(SensorManager sensorMng) {
        this.sensorMng = sensorMng;
        ambientTempSensor = sensorMng.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    @Override
    public void stop() {
        sensorMng.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch(accuracy){
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                Log.w("StepCounterProvider", "SENSOR_STATUS_ACCURACY_HIGH");
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                Log.w("StepCounterProvider", "SENSOR_STATUS_ACCURACY_MEDIUM");
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                Log.w("StepCounterProvider", "SENSOR_STATUS_ACCURACY_LOW");
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                Log.w("StepCounterProvider", "SENSOR_STATUS_UNRELIABLE");
                break;
            case SensorManager.SENSOR_STATUS_NO_CONTACT:
                Log.w("StepCounterProvider", "SENSOR_STATUS_NO_CONTACT");
                break;
        }
    }
}
