package com.example.aliano.clientcircadian_va;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.aliano.clientcircadian_va.ActivityRecognition.AR_BootLauncher;

/**
 * Created by Alexis on 13.10.2016.
 * gradle 19n instead of 15
 */
public class BootBroadcastReceiver extends BroadcastReceiver{
        //implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public Context appContext;
    //public GoogleApiClient mApiClient;
    @Override
    public void onReceive(Context context, Intent intent) {
        appContext = context;
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
          // Au démarrage on lance les services
            Intent i = new Intent(appContext, AR_BootLauncher.class); // reconnaissance d'activité
            Intent s = new Intent(appContext, StepCounterService.class); // compteur de pas
            Intent t = new Intent(appContext, EventSyncService.class);
            /*i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// to retrieve the context
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // test to close automaticaliy activity
            */

            context.startService(i); // launch activity recognition service at start
            context.startService(s); // launch step counter service
            context.startService(t); // launch eventSync but suicide if no needed
        }
    }
}