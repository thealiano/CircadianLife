package com.example.aliano.clientcircadian_va.SensorProviders.SoftwarePodometer;

/**
 * Created by Aliano on 17/05/2017.
 */
// Will listen to step alerts
public interface StepListener {

    public void step(long timeNs);

}
