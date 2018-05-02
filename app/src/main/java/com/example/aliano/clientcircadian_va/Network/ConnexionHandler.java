package com.example.aliano.clientcircadian_va.Network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.json.*;
import com.example.aliano.clientcircadian_va.Config.ConfigurationParameters;
import com.example.aliano.clientcircadian_va.DataAccessActivity;
import com.example.aliano.clientcircadian_va.EventSyncService;
import com.example.aliano.clientcircadian_va.MainActivity;
import com.example.aliano.clientcircadian_va.R;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TEvent;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TGlobalActivity;
import com.example.aliano.clientcircadian_va.WelcomeActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Alexis on 11.11.2016.
 */
public class ConnexionHandler {
    Context context;
    WelcomeActivity welcome_activity;
    MainActivity main_activity;
    DataAccessActivity data_activity;
    EventSyncService eventSync_service;
    String uid ="",lastActivityTimeStamp="",email="";

    public ConnexionHandler(WelcomeActivity activity){
        this.context = activity.getApplicationContext();
        this.welcome_activity = activity;
        Log.i("NETWORK_a_welcome","Start Connexion Handler Welcome");
    }
    public ConnexionHandler(MainActivity activity){
        this.context = activity.getApplicationContext();
        this.main_activity = activity;
        Log.i("NETWORK_a_main","Start Connexion Handler Main");
    }
    public ConnexionHandler(DataAccessActivity activity){
        this.context = activity.getApplicationContext();
        this.data_activity = activity;
        Log.i("NETWORK_a_data","Start Connexion Handler Main");
    }
    public ConnexionHandler(EventSyncService service){
        this.context = service.getApplicationContext();
        this.eventSync_service = service;
        Log.i("NETWORK_a_data", "Start Connexion Handler Main");
    }
    public boolean haveInternetConnection(){
        // Fonction haveInternetConnection : return true si connecté, return false dans le cas contraire
        NetworkInfo network = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();


        if (network==null || !network.isConnected()){
            // Le périphérique n'est pas connecté à Internet
            Log.e("NEWORK","NO INTERNET CONNEXION");
            return false;
        }else{
            boolean wifi = network.getType() == ConnectivityManager.TYPE_WIFI;
        }
        if (network.isRoaming()) // for long task
        { // ajouter une préfèrence pour savoir si l'utilisateur veut envoyer des données en roaming ou pas
        }
        // Le périphérique est connecté à Internet
        Log.e("NEWORK", "INTERNET CONNEXION " + network.getTypeName());
        return true;
    }
    public String registerUserAndGetId(RequestParams params2) throws JSONException{
//TODO: ADD ON FAILURE
        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.NEW_USER_SCRIPT, params2, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK", "onSuccess1: " + statusCode + ":" + response);
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK", "onSuccess2: " + statusCode + ":" + response.toString());

                // If the response is JSONObject instead of expected JSONArray
            }

            // On use when we use php_encode_json FONCTION
            @Override // Return the User Id
            public void onSuccess(int statusCode, Header[] headers, JSONArray jarray) {
                super.onSuccess(statusCode, headers, jarray);
                Log.i("NETWORK", "onSuccess3");
                // here we go
                // Pull out the first event on the public timeline
                JSONObject firstEvent = null;
                try {
                    firstEvent = (JSONObject) jarray.get(0);
                    uid = firstEvent.getString("id");
                    welcome_activity.onCommunicationSuccess(uid);
                    // Do something with the response
                    Log.i("NETWORK", "Output:" + uid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NETWORK", "failure: " + responseString);
                Log.e("NETWORK", "failurecode: " + statusCode);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
        return uid; // error
    }
    public String getActivityRecordDate(RequestParams params) throws JSONException {
//TODO: ADD ON FAILURE

        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.GET_LAST_ACTIVITY_SCRIPT, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_m", "onSuccess1: " + statusCode + ":" + response);
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_M", "onSuccess2: " + statusCode + ":" + response.toString());

                // If the response is JSONObject instead of expected JSONArray
            }

            // On use when we use php_encode_json FONCTION
            @Override // Return the User Id
            public void onSuccess(int statusCode, Header[] headers, JSONArray jarray) {
                super.onSuccess(statusCode, headers, jarray);
                Log.i("NETWORK_M", "onSuccess3");
                // here we go
                // Pull out the first event on the public timeline
                JSONObject firstEvent = null;
                try {
                    firstEvent = (JSONObject) jarray.get(0);
                    lastActivityTimeStamp = firstEvent.getString("LastActivityRecord");
                    main_activity.onCommunicationSuccess(lastActivityTimeStamp);
                    //TODO: METTRE UNE condition

                    // Do something with the response
                    Log.i("NETWORK_M", "Output:" + lastActivityTimeStamp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NETWORK", "failure: " + responseString);
                Log.e("NETWORK", "failure_code: " + statusCode);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
        return lastActivityTimeStamp; // error
    }
    public void getEvents(RequestParams params) throws JSONException {
        // get new events for the application

        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.GET_EVENTS, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NET_getEvents", "onSuccess1: " + statusCode + ":" + response);
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NET_getEvents", "onSuccess2: " + statusCode + ":" + response.toString());

                // If the response is JSONObject instead of expected JSONArray
            }

            // On use when we use php_encode_json FONCTION
            @Override // Return the User Id
            public void onSuccess(int statusCode, Header[] headers, JSONArray jarray) {
                super.onSuccess(statusCode, headers, jarray);
                Log.i("NET_getEvents", "onSuccess3 size answer="+jarray.length());

                CircadianLocalBDD localBDD = new CircadianLocalBDD(eventSync_service);
                localBDD.open();
                JSONObject Event = null;
                for(int i=0;i<jarray.length();i++){
                    try {
                        Log.i("NET_getEvents", "onSuccess3 fill local db");
                        Event = (JSONObject) jarray.get(i);
                        localBDD.insertEvent(new TEvent(
                                Event.getLong("id"), // le même sur le serveur et en local
                            Event.getString("type"),
                            Event.getString("name"),
                            Event.getString("description"),
                            Event.getString("value"),
                            Event.getString("content"),
                            0, //image TODO: Trouver un moyen de verifier si les images sont libres ou non
                            false // done
                            ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(jarray.length() > 0) { // on mets à jour la préférence
                    sendNotification();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(eventSync_service);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("lastEventGet", getDate());
                    editor.commit();
                }
                localBDD.close();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NET_getEvents", "failure: " + responseString);
                Log.e("NET_getEvents", "failure_code: " + statusCode);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
        //return lastActivityTimeStamp; // error
    }
    public void getGlobalActivity() throws JSONException {
        // get global activity by hour for the population

        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.GET_GLOBALACTIVITY, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NET_getActivityAll", "onSuccess1: " + statusCode + ":" + response);
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NET_getActivityAll", "onSuccess2: " + statusCode + ":" + response.toString());

                // If the response is JSONObject instead of expected JSONArray
            }

            // On use when we use php_encode_json FONCTION
            @Override // Return the User Id
            public void onSuccess(int statusCode, Header[] headers, JSONArray jarray) {
                super.onSuccess(statusCode, headers, jarray);
                Log.i("NET_getActivityAll", "onSuccess3 size answer="+jarray.length());
                CircadianLocalBDD localBDD = new CircadianLocalBDD(main_activity);
                localBDD.open();
                JSONObject GlobalActivity = null;
                if(jarray.length()>0) {
                    for (int i = 0; i < jarray.length(); i++) {
                        try {
                            Log.i("NET_getActivityAll_" + i, "onSuccess3 fill local db");
                            GlobalActivity = (JSONObject) jarray.get(i);
                            Log.i("NET_getActivityAll", GlobalActivity.getString("type"));
                            Log.i("NET_getActivityAll", GlobalActivity.getString("json"));
                            Log.i("NET_getActivityAll", GlobalActivity.getString("last_update"));
                            Log.i("NET_getActivityAll", "" + Long.parseLong(GlobalActivity.getString("avg_steps")));
                            Log.i("NET_getActivityAll", "" + Long.parseLong(GlobalActivity.getString("avg_kcals")));
                            localBDD.insertGlobalActivity(new TGlobalActivity(
                                    GlobalActivity.getString("type"),
                                    GlobalActivity.getString("json"),
                                    GlobalActivity.getString("last_update"),
                                    Long.parseLong(GlobalActivity.getString("avg_steps")),
                                    Long.parseLong(GlobalActivity.getString("avg_kcals"))
                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                localBDD.close();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NET_getEvents", "failure: " + responseString);
                Log.e("NET_getEvents", "failure_code: " + statusCode);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
        //return lastActivityTimeStamp; // error
    }
    public void postActivity(RequestParams params){
//TODO: ADD ON FAILURE

        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.RECORD_ACTIVITY_SCRIPT, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_m2", "onSuccess1: " + statusCode + ":" + response);
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_M2", "onSuccess2: " + statusCode + ":" + response.toString());

                // If the response is JSONObject instead of expected JSONArray
            }

            // On use when we use php_encode_json FONCTION
            @Override // Return the User Id
            public void onSuccess(int statusCode, Header[] headers, JSONArray jarray) {
                super.onSuccess(statusCode, headers, jarray);
                Log.i("NETWORK_M2", "onSuccess3");
                // here we go
                // Pull out the first event on the public timeline
                JSONObject firstEvent = null;
                try {
                    //firstEvent = (JSONObject) jarray.get(0);

                    //lastActivityTimeStamp = firstEvent.getString("LastActivityRecord");
                    //main_activity.onCommunicationSuccess(lastActivityTimeStamp);
                    // Do something with the response
                    Log.i("NETWORK_M2", "Output:" + jarray.get(0).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NETWORK_M2", "failure: " + responseString);
                Log.e("NETWORK_M2", "failure_code: " + statusCode);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
    public void postEventsAnswers(RequestParams params){
//Envoi les réponses données aux events au serveur

        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.RECORD_EVENTS_SCRIPT, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_POST_EVENT", "onSuccess1: " + statusCode + ":" + response);
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_POST_EVENT", "onSuccess2: " + statusCode + ":" + response.toString());

                // If the response is JSONObject instead of expected JSONArray
            }

            // On use when we use php_encode_json FONCTION
            @Override // Return the User Id
            public void onSuccess(int statusCode, Header[] headers, JSONArray jarray) {
                super.onSuccess(statusCode, headers, jarray);
                Log.i("NETWORK_POST_EVENT", "onSuccess3");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NETWORK_POST_EVENT", "failure: " + responseString);
                Log.e("NETWORK_POST_EVENT", "failure_code: " + statusCode);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
    public boolean isConnectedToServer(String url, int timeout) {
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }
    public String verificationEmail(RequestParams params2) throws JSONException{
//TODO: ADD ON FAILURE
        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.IS_EMAIL_SET, params2, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_email","onSuccess1: "+statusCode+":"+response);
                // If the response is JSONObject instead of expected JSONArray
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_email","onSuccess2: "+statusCode+":"+response.toString());

                // If the response is JSONObject instead of expected JSONArray
            }
            // On use when we use php_encode_json FONCTION
            @Override // Return the User Id
            public void onSuccess(int statusCode, Header[] headers, JSONArray jarray)  {
                super.onSuccess(statusCode, headers, jarray);
                Log.i("NETWORK_M_email", "onSuccess3_EMAIL");
                // here we go
                // Pull out the first event on the public timeline
                JSONObject firstEvent = null;
                try {
                    firstEvent = (JSONObject) jarray.get(0);
                    email = firstEvent.getString("email");

                    // update shared preference
                    if(!email.equals("") && !email.isEmpty() && !email.equals("null")){
                        Boolean isVerified = intToBool(firstEvent.getInt("emailVerified"));
                        SharedPreferences sharedPref = data_activity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", email);
                        editor.putBoolean("emailVerified", isVerified);
                        editor.commit();
                        Log.i("NETWORK_M_email", "email mis à jour dans les shared preferences " + email + " bool:" + isVerified.toString());
                        if(isVerified) // mail ok vérifié on passe au dernier écran
                            data_activity.onCommunicationSuccess(email);
                    }else
                        Log.i("NETWORK_M_email","email NULL pas mis à jour  dans les shared preferences");
                    //welcome_activity.onCommunicationSuccess(email);
                    // Do something with the response
                    Log.i("NETWORK_M_email", "Output:" + email);
                   // data_activity.onCommunicationSuccess(email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NETWORK_mail1", "failure: " + responseString);
                Log.e("NETWORK_mail1", "failurecode: " + statusCode);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
        return email; // error
    }
    public String submitEmail(RequestParams params2) throws JSONException{
        // TO RECORD E-MAIL ON SERVER :
        // PUT EMAIL AND PASS ON DATABASE AND PUT emailVerified == false
        // last step = activation
//TODO: ADD ON FAILURE
        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.RECORD_EMAIL, params2, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_email_SUBMITTED","onSuccess1: "+statusCode+":"+response);
                // If the response is JSONObject instead of expected JSONArray
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("NETWORK_email_SUBMITTED","onSuccess2: "+statusCode+":"+response.toString());

                // If the response is JSONObject instead of expected JSONArray
            }
            // On use when we use php_encode_json FONCTION
            @Override // Return the User Id
            public void onSuccess(int statusCode, Header[] headers, JSONArray jarray)  {
                super.onSuccess(statusCode, headers, jarray);
                Log.i("NETWORK_email_SUBMITTED", "onSuccess3_EMAIL");
                // here we go
                // Pull out the first event on the public timeline
                JSONObject firstEvent = null;
                try {
                    firstEvent = (JSONObject) jarray.get(0);
                    Boolean isMailSent = intToBool(firstEvent.getInt("IsMailSent"));
                    if(isMailSent){ // si le mail de vérification à été correctement envoyé
                        SharedPreferences sharedPref = data_activity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", email);
                        editor.commit();
                    }
// verif supplémentaire doublons ?
                    Log.i("NETWORK_M_SubmitEmail", "Output:" + isMailSent.toString());
                    //data_activity.onCommunicationSuccess(email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NETWORK_mail2", "failure: " + responseString);
                Log.e("NETWORK_mail2", "failurecode: " + statusCode);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
        return email; // error
    }

    public void downloadDatas(RequestParams params){
//TODO: ADD ON FAILURE

        // ATTENTION HANDLER ASSYNCHRONES PAS INSTANTANES
        HttpStaticClient.post(ConfigurationParameters.DDL_DATAS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("email", "csv_downloaded:" + statusCode);
                //response.createNewFile()
                writeToFileExternal(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("NETWORK_M2", "failure: " + responseBody.toString());
                Log.e("NETWORK_M2", "failure_code: " + statusCode);
            }
        });
    }
    public Boolean intToBool(Integer toconvert ){
        return (toconvert == 1);
    }
    public void writeToFileExternal(byte[] textToWrite){
        Log.i("WriteFile", "writeToFileExternal ");
        // write to Ordinateur\Galaxy A3 (2016)\Phone\circadian_google_ar
        File root = android.os.Environment.getExternalStorageDirectory(); // only works with version < marshmallow
        Log.v("WriteFile", "External file system root: " + root);

        String filepath = root.getAbsolutePath() + "/circadian_life_app";
        if (isStoragePermissionGranted()) {
            File dir = new File(filepath);
            dir.mkdirs();
            File file = new File(dir, "circadian_datas_export.csv");
            //}
            try {
                FileOutputStream f = new FileOutputStream(file, false);// true = append mode ; if true, then bytes will be written to the end of the file rather than the beginning
                f.write(textToWrite);
                f.close();
                Toast.makeText(context, "Download to : "+filepath, Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i("WriteFile", "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("WriteFile", "File written to " + file);
        }
    }
    public  boolean isStoragePermissionGranted() { // demande de permission supplémentaire depuis marshmallow
        if (Build.VERSION.SDK_INT >= 23) {
            if (data_activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("permission", "Permission is granted");
                return true;
            } else {

                Log.v("permission", "Permission is revoked");
                ActivityCompat.requestPermissions(data_activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("permission", "Permission is granted");
            return true;
        }
    }
    private String getDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(cal.getTime());
        //		return "["+cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+
        //                " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"]";
    }
    public void sendNotification() {
/*TODO: a ajouter : kill au clic + METTRE DANS le résultat de la requête*/
        String notification_content = context.getResources().getString(R.string.notification);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icone)
                        .setContentTitle("Circadian Life Events")
                        .setContentText(notification_content)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);
// Gets an instance of the NotificationManager service//
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(777, mBuilder.build());

    }
// downloadDatas
}
