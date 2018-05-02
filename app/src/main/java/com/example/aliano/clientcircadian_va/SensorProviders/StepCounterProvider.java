package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Data.Data;
import com.example.aliano.clientcircadian_va.Data.StepCounterData;

/**
 * Created by John on 20.05.2015.
 */
public class StepCounterProvider implements SensorEventListener,ProviderInterface {
    private static StepCounterProvider INSTANCE = new StepCounterProvider();
    private SensorManager sensorMng;
    private Sensor stepCounterSensor;
    private StepCounterData data;
    private float prevStepCntr;
    private float currStepCntr;


    private StepCounterProvider(){
        data = new StepCounterData();
        prevStepCntr = (float) 0.0;
        prevStepCntr = (float) 0.0;
    }

    public static StepCounterProvider getInstance(){
        return INSTANCE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            currStepCntr = event.values[0];
            data.setData(currStepCntr-prevStepCntr);
            prevStepCntr = currStepCntr;
            int accuracy = event.accuracy;

            switch(accuracy){
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    Log.w("StepCounterProvider2", "SENSOR_STATUS_ACCURACY_HIGH");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    Log.w("StepCounterProvider2", "SENSOR_STATUS_ACCURACY_MEDIUM");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    Log.w("StepCounterProvider2", "SENSOR_STATUS_ACCURACY_LOW");
                    break;
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    Log.w("StepCounterProvider2", "SENSOR_STATUS_UNRELIABLE");
                    break;
                case SensorManager.SENSOR_STATUS_NO_CONTACT:
                    Log.w("StepCounterProvider2", "SENSOR_STATUS_NO_CONTACT");
                    break;
            }
        }
    }

    @Override
    public Sensor getSensor() {
        return stepCounterSensor;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setSensorMng(SensorManager sensorMng) {
        this.sensorMng = sensorMng;
        stepCounterSensor = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
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
