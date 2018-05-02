package com.example.aliano.clientcircadian_va.Data;

import com.example.aliano.clientcircadian_va.Config.ConfigurationParameters;

/**
 * Created by John on 20.05.2015.
 */
public class PressureData implements Data<Float>{
    private final String TAG_TYPE = ConfigurationParameters.PRESSURE_DATA_TAG;
    private Float data;

    public PressureData(){

    }

    @Override
    public String getType() {
        return TAG_TYPE;
    }

    @Override
    public Float getData() {
        return data;
    }

    @Override
    public void setData(Float data) {
        this.data = data;
    }

    @Override
    public String toJsonString() {
        if(data != null){
            return "\""+TAG_TYPE+"\": \""+data.toString()+"\"";
        } else {
            return "\""+TAG_TYPE+"\": \"-1\"";
        }
    }
}
