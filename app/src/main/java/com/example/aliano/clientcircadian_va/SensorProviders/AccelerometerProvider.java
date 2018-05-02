package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Data.AccelerometerData;
import com.example.aliano.clientcircadian_va.Data.Data;


/**
 * Created by John on 20.05.2015.
 */
public class AccelerometerProvider implements SensorEventListener,ProviderInterface {
    private static AccelerometerProvider INSTANCE = new AccelerometerProvider();
    private SensorManager sensorMng;
    private Sensor accelerometerSensor;
    private AccelerometerData data;
    private Float meanX, meanY, meanZ;
    private int numberOfPicks;


    private AccelerometerProvider(){
        data = new AccelerometerData();
        numberOfPicks = 0;
        meanX = 0.0f;
        meanY = 0.0f;
        meanZ = 0.0f;
    }

    public static AccelerometerProvider getInstance(){
        return INSTANCE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            numberOfPicks++;
            meanX = (event.values[0]+(meanX*(numberOfPicks-1)))/numberOfPicks;
            meanY = (event.values[1]+(meanY*(numberOfPicks-1)))/numberOfPicks;
            meanZ = (event.values[2]+(meanZ*(numberOfPicks-1)))/numberOfPicks;
            Float[] values = new Float[3];
            values[0] = meanX;
            values[1] = meanY;
            values[2] = meanZ;
            data.setData(values);

        }
    }

    @Override
    public Sensor getSensor() {
        return accelerometerSensor;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setSensorMng(SensorManager sensorMng) {
        this.sensorMng = sensorMng;
        accelerometerSensor = sensorMng.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
