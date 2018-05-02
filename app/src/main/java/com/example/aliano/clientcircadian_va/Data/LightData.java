package com.example.aliano.clientcircadian_va.Data;


import com.example.aliano.clientcircadian_va.Config.ConfigurationParameters;

/**
 * Created by John on 06.05.2015.
 */
public class LightData implements Data<Float> {
    private final String TAG_TYPE = ConfigurationParameters.LIGHT_DATA_TAG;
    private Float data;

    public LightData(){

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

//    @Override
//    public String toString() {
//        return "\""+TAG_TYPE+"\": "+"\""+data.toString()+"\"";
//    }

    @Override
    public String getType() {
        return TAG_TYPE;
    }
}
