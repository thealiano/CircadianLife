package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Data.Data;
import com.example.aliano.clientcircadian_va.Data.RelativeHumidityData;


/**
 * Created by John on 20.05.2015.
 */
public class RelativeHumidityProvider implements SensorEventListener, ProviderInterface {
    private static RelativeHumidityProvider INSTANCE = new RelativeHumidityProvider();
    private SensorManager sensorMng;
    private Sensor humiditySensor;
    private RelativeHumidityData data;

    private RelativeHumidityProvider(){
        // private contructor for the singleton
        data = new RelativeHumidityData();
    }

    public static RelativeHumidityProvider getInstance(){
        return INSTANCE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){
            // Relative ambient air humidity in percent
            data.setData(event.values[0]);
        }
    }

    @Override
    public Sensor getSensor() {
        return humiditySensor;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setSensorMng(SensorManager sensorMng) {
        this.sensorMng = sensorMng;
        humiditySensor = sensorMng.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
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
