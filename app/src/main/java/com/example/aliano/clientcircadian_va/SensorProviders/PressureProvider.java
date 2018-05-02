package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Data.Data;
import com.example.aliano.clientcircadian_va.Data.PressureData;


/**
 * Created by John on 20.05.2015.
 */
public class PressureProvider implements SensorEventListener,ProviderInterface {
    private static PressureProvider INSTANCE = new PressureProvider();
    private SensorManager sensorMng;
    private Sensor pressureSensor;
    private PressureData data;
    private float mean_pressure;
    private int numberOfPicks;


    private PressureProvider(){
        data = new PressureData();
        numberOfPicks = 0;
        mean_pressure = 0.0f;
    }

    public static PressureProvider getInstance(){
        return INSTANCE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
            // Values are in millibars of pressure hPa
            numberOfPicks++;
            mean_pressure = (event.values[0]+(mean_pressure*(numberOfPicks-1)))/numberOfPicks;

            data.setData(mean_pressure);
        }
    }

    @Override
    public Sensor getSensor() {
        return pressureSensor;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setSensorMng(SensorManager sensorMng) {
        this.sensorMng = sensorMng;
        this.pressureSensor = sensorMng.getDefaultSensor(Sensor.TYPE_PRESSURE);
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
