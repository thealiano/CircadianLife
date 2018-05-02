package com.example.aliano.clientcircadian_va.Network;

/**
 * Created by Alexis on 11.11.2016.
 */
import com.example.aliano.clientcircadian_va.Config.ConfigurationParameters;
import com.loopj.android.http.*;

public class HttpStaticClient {
    //private static final String BASE_URL = "https://api.twitter.com/1/";
    private static final String BASE_URL = "http://" +ConfigurationParameters.HOST_NAME +ConfigurationParameters.HOST_FILE;
    //private static final String BASE_URL ="http://128.179.153.29/circadianServerTest/";
    // maybe add port ?
    private static AsyncHttpClient client = new AsyncHttpClient();
    
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }
    //public AsyncHttpClient getClient(){return client;}

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}