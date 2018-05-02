package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.example.aliano.clientcircadian_va.Data.Data;


/**
 * Created by John on 06.05.2015.
 */
public interface ProviderInterface {
    public Sensor getSensor();
    public Data getData();
    public void setSensorMng(SensorManager sensorMng);
    public void stop();
}
