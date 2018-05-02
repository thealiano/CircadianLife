package com.example.aliano.clientcircadian_va.ActivityRecognition;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

/**
 * Created by Alexis on 15.12.2016.
 */
public class AR_BootLauncher extends IntentService {
    public AR_BootLauncher() {
        this(AR_BootLauncher.class.getName());
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AR_BootLauncher(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Intent service par défaut en start Sticky
        Log.i("AR_BootLauncher","onHandleIntent_start");
        // toast dans un br JAMAIS  ! IL FAIT TOUT FOIRé !
        // requestActivityUpdates = 1 data for each update, requestActivityChanges = 1 data for each activity change
        // you have to change the receiver class accordingly v requestActivityUpdates or requestActivityChanges
        PathsenseLocationProviderApi.getInstance(this).requestActivityUpdates(PathsenseActivityUpdateBroadcastReceiver.class);
        String serviceName = "com.pathsense.activitydemo.app.ActivityRecognitionService";
        Intent serviceintent = new Intent(this, ActivityRecognitionService.class);
        serviceintent.setAction(serviceName);
        startService(serviceintent); // start le service

    }

}
