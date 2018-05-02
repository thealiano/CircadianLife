package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Data.MagneticFieldData;


/**
 * Created by John on 05.05.2015.
 */
public class MagneticFieldProvider implements SensorEventListener,ProviderInterface {
    private static MagneticFieldProvider INSTANCE = new MagneticFieldProvider();
    private SensorManager sensorMng;
    private Sensor magneticFieldSensor;
    private MagneticFieldData data;
    private Float meanX, meanY, meanZ;
    private int numberOfPicks;

    private MagneticFieldProvider(){
        // private contructor for the singleton
        data = new MagneticFieldData();
        numberOfPicks = 0;
        meanX = 0.0f;
        meanY = 0.0f;
        meanZ = 0.0f;
    }

    public static MagneticFieldProvider getInstance(){
        return INSTANCE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            // Values are in micro-tesla
            numberOfPicks++;
            meanX = (event.values[0]+(meanX*(numberOfPicks-1)))/numberOfPicks;
            meanY = (event.values[1]+(meanY*(numberOfPicks-1)))/numberOfPicks;
            meanZ = (event.values[2]+(meanZ*(numberOfPicks-1)))/numberOfPicks;
            Float[] values = new Float[3];
            values[0] = meanX;
            values[1] = meanY;
            values[2] = meanZ;
            data.setData(values);
//            Log.w("MagneticFieldProvider", "Magnetic Field X: " + event.values[0]);
//            Log.w("MagneticFieldProvider", "Magnetic Field Y: " + event.values[1]);
//            Log.w("MagneticFieldProvider", "Magnetic Field Z: " + event.values[2]);
        }
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

    @Override
    public void setSensorMng(SensorManager sensorMng){
        this.sensorMng = sensorMng;
        this.magneticFieldSensor = sensorMng.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void stop() {
        sensorMng.unregisterListener(this);
    }

    @Override
    public Sensor getSensor() {
        return magneticFieldSensor;
    }

    @Override
    public MagneticFieldData getData() {
        return data;
    }


}
