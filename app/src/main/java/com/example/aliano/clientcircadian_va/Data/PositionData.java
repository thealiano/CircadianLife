package com.example.aliano.clientcircadian_va.Data;

import com.example.aliano.clientcircadian_va.Config.ConfigurationParameters;

/**
 * Created by John on 21.05.2015.
 */
public class PositionData implements Data<Double[]> {
    private final String TAG_TYPE = ConfigurationParameters.POSITIOIN_DATA;
    private Double[] data;

    public PositionData() {

    }

    @Override
    public String getType() {
        return TAG_TYPE;
    }

    @Override
    public Double[] getData() {
        return data;
    }

    @Override
    public void setData(Double[] data) {
        this.data = data;
    }

    @Override
    public String toJsonString() {
        if(data != null){
            return "\""+TAG_TYPE+"\": "+"["+data[0]+","+data[1]+"]";
        } else {
            return "\""+TAG_TYPE+"\": []";
        }

    }
}
