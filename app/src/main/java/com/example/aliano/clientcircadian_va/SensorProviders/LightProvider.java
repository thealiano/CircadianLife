package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Data.LightData;

/**
 * Created by John on 05.05.2015.
 */
public class LightProvider implements SensorEventListener, ProviderInterface {
    private static LightProvider INSTANCE = new LightProvider();
    private SensorManager sensorMng;
    private Sensor lightSensor;
    private LightData data;
    private Sensor proximitySensor;
    private double proximityValue;
    private float mean_light;
    private int numberOfPicks;

    private LightProvider(){
        // private contructor for the singleton
        data = new LightData();
        mean_light = 0;
        numberOfPicks = 0;
    }

    public static LightProvider getInstance(){
        return INSTANCE;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                proximityValue = event.values[0];
            Log.w("LightProvider", "proximity: " + event.values[0]);
        }
        else if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            // Values are in Lux


            if(proximityValue > 0){

                //Calculate a mean with all the light value
                numberOfPicks++;
                mean_light = (event.values[0]+(mean_light*(numberOfPicks-1)))/numberOfPicks;


//                Log.w("LightProvider", "LIGHT: " + event.values[0]);
//                Log.w("LightProvider", "LIGHT MEAN: " + mean_light);
//                Log.w("LightProvider", "numberOfPicks: " + numberOfPicks);

                data.setData(mean_light);


            }


        }


    }

    public void setProximitySensor(Sensor proximitySensor){
        this.proximitySensor = proximitySensor;
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

    public void setSensorMng(SensorManager sensorMng){
        this.sensorMng = sensorMng;
        this.lightSensor = sensorMng.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void stop() {
        sensorMng.unregisterListener(this);
    }

    @Override
    public Sensor getSensor() {
        return lightSensor;
    }

    @Override
    public LightData getData() {
        return data;
    }


    public Sensor getProximitySensor(){
        return proximitySensor;
    }
}
