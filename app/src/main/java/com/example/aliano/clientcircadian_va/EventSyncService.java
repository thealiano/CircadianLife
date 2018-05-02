package com.example.aliano.clientcircadian_va;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;

import com.example.aliano.clientcircadian_va.Network.ConnexionHandler;
import com.example.aliano.clientcircadian_va.Network.OnCommunicationListener;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TUserInfo;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Aliano on 02/07/2017.
 */
public class EventSyncService extends Service implements OnCommunicationListener {
    Context ctx;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {
        Log.i("START_SERVICE", "EventSync");
        // doit être fait une fois par jour
        ctx = getApplicationContext();

        CircadianLocalBDD localBdd = new CircadianLocalBDD(this);
        localBdd.open();
        TUserInfo userInfos = localBdd.getUserInfos();
        int countEvent = localBdd.getCountEventToSent(); // nombre d'event realisé ET PAS envoyés dans la base locale
        localBdd.close(); //On ferme la base de données

        if (userInfos != null) {
            Log.v("EventSync", userInfos.toString());

            Log.i("EventSync", "Nb_event done not sent:" + countEvent);
            if (countEvent > 0) { // envoi des events réalisé au serveur
                localBdd.open();
                //postEventsAnswers
                // get all the events done
                JSONArray eventsRecorded = localBdd.getEventsDoneToSent();
                // récupère = eventId, event type et résultat
                // envoi les réponses des formulaires et les events deja fait au serveur
                //TODO: enlever les infos
                if (eventsRecorded != null) {
                    int eventsToRecord = eventsRecorded.length();
                    Log.i("EventSync", "3.1_EVENT_TO_RECORD_JSON_READY_TO_SEND_size:" + eventsToRecord);
                    Log.i("EventSync", "3.1_EVENT_TO_RECORD_JSON_READY_TO_SEND_content:" + eventsRecorded.toString());
                    if (eventsToRecord > 0) { // send it to server
                        RequestParams params = new RequestParams();
                        params.put("json", eventsRecorded.toString());
                        params.put("id", userInfos.getId()); // user id to sign activity

                        ConnexionHandler conn = new ConnexionHandler(this);
                        conn.postEventsAnswers(params); //launch the asynchronous Task
                        if(conn.haveInternetConnection()) {
                            for (int j = 0; j < eventsRecorded.length(); j++) {
                                try {
                                    JSONObject event = eventsRecorded.getJSONObject(j);
                                    Log.i("Event_sync", "id_get" + event.getLong("ID"));
                                    localBdd.setEventSent(event.getLong("ID")); // a mettre en connexion success
                                } catch (JSONException e) {
                                    Log.i("EventSync", "json_error_sent");
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Log.i("EventSync", "EVENT_TO_RECORD_error1");
                    }
                } else {
                    Log.i("EventSync", "EVENT_TO_RECORD_error2");
                }
                localBdd.close(); //On ferme la base de données

            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
            String last_event_get = sharedPref.getString("lastEventGet", "");
            Log.i("EventSync","led="+last_event_get);
            if(!getDate().equals(last_event_get)){ // il y'a eu un enregistrement avant
                    // pour éviter de récupérer tout les events le même jour
                    // on va en récupérer 2 (chiffre fixé dans le fichier php ) par jour
                    // get new events
                    ConnexionHandler conn = new ConnexionHandler(this);
                    if(conn.haveInternetConnection()) {
                        String language = Locale.getDefault().getLanguage().toUpperCase(); // FR ou EN
                        RequestParams params = new RequestParams();
                        params.put("id", userInfos.getId()); // user id
                        params.put("language", language); // language
                        //Log.i("EventSync", "language sent" + language);
                        try { // remplis la base locale d'events
                            conn.getEvents(params); //launch the asynchronous Task who will fill the local db
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }else
            Log.e("EventSync", "ERROR RETRIVING USERINFORMATIONS");

    }
    @Override
    public void onCommunicationSuccess(String output) {

    }

    @Override
    public void onCommunicationFailure() {

    }
    private String getDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(cal.getTime());
        //		return "["+cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+
        //                " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"]";
    }


}
