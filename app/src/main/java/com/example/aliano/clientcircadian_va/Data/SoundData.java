package com.example.aliano.clientcircadian_va.Data;


import com.example.aliano.clientcircadian_va.Config.ConfigurationParameters;

/**
 * Created by John on 21.05.2015.
 */
public class SoundData implements Data<Double> {
    private final String TAG_TYPE = ConfigurationParameters.SOUND_DATA_TAG;
    private Double data;

    public SoundData() {

    }

    @Override
    public String getType() {
        return TAG_TYPE;
    }

    @Override
    public Double getData() {
        return data;
    }

    @Override
    public void setData(Double data) {
        this.data = data;
    }

    @Override
    public String toJsonString() {
        if(data != null){
            return "\""+TAG_TYPE+"\": \""+data.toString()+"\"";
        } else {
            return "\""+TAG_TYPE+"\": \"-10000\"";
        }
    }
}
