package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.example.aliano.clientcircadian_va.Data.Data;
import com.example.aliano.clientcircadian_va.Data.PositionData;

/**
 * Created by John on 21.05.2015.
 */
public class PositionProvider implements Runnable, ProviderInterface, LocationListener {
    private static PositionProvider INSTANCE = new PositionProvider();
    private PositionData data;

    private PositionProvider(){
        data = new PositionData();
    }

    public static PositionProvider getInstance(){
        Log.w("PositionProvider", "PositionProvider is giving the instance");
        return INSTANCE;
    }

    @Override
    public void run() {

    }


    @Override
    public Sensor getSensor() {
        return null;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setSensorMng(SensorManager sensorMng) {

    }

    @Override
    public void stop() {
    }

    @Override
    public void onLocationChanged(Location location) {
        Double[] pos = new Double[2];
        pos[0] = location.getLatitude();
        pos[1] = location.getLongitude();
        data.setData(pos);
        Log.w("PositionProvider", "Pos0 = " + pos[0] + "Pos1 = " + pos[1]);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
