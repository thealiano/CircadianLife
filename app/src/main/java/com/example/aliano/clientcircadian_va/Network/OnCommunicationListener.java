package com.example.aliano.clientcircadian_va.Network;

import org.json.JSONException;

/**
 * Created by Alexis on 18.11.2016.
 */
public interface OnCommunicationListener {
        public void onCommunicationSuccess(String output) throws JSONException;
        public void onCommunicationFailure();
}
