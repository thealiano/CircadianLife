package com.example.aliano.clientcircadian_va;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aliano.clientcircadian_va.Network.ConnexionHandler;
import com.example.aliano.clientcircadian_va.Network.OnCommunicationListener;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TUserInfo;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnCommunicationListener{//}, NavigationView.OnNavigationItemSelectedListener {
//public class MainActivity extends DrawerMenu implements OnCommunicationListener{//}, NavigationView.OnNavigationItemSelectedListener {
    String email;
    private TextView tlevel, tname, tlevelup;
    private ProgressBar pXp;
    public int pictureRes, profileNumber;
    public android.location.Location true_location;
    public long userid;
    TUserInfo userInfo;

    //shared preference to update location
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    ConnexionHandler conn;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
   // private GoogleApiClient client;
// for navigation
    private ViewPager viewPager;
    private DrawerLayout drawer;
    private TabLayout tabLayout;

     @Override
    protected void onResume() {
        super.onResume();
        //LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.STRING_ACTION));
    }

    @Override
    protected void onPause() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }
//TODO : TESTER SI L'ACTIVITY RECOGNITION EST DéMARRé SI CE N'EST PAS LE CAS LE DéMARRé manuellementb
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Location Provider
        FusedLocationProviderClient mFusedLocationClient;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(android.location.Location location) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    true_location = location;

                    editor.putFloat("locationlat", (float)location.getLatitude());
                    editor.putFloat("locationlong", (float)location.getLongitude());
                    editor.commit();
                    Log.i("location_eph","edited by mainActivity: "+location.getLatitude()+","+location.getLongitude());

                }else
                    Log.e("Mainlocation","not_found");
            }
        });

        //private FusedLocationProviderClient mFusedLocationClient;
       // com.example.aliano.clientcircadian_va.SunriseAndSet.dto.Location default_location = new com.example.aliano.clientcircadian_va.SunriseAndSet.dto.Location("46.5333", "6.6668"); // lausanne


        //get the viewPager and drawer
        viewPager = (ViewPager) findViewById(R.id.view_pager); // VIEWPAGER
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        // create default navigation toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        // set viewpager (onglet) adapter
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(),this));

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
// load db
        userInfo = loadUserInformations(); // load user informations from local bdd
        recordActivity(); // RECORD ACTIVITY in server: NEED USERINF

        ImageView mProfilePicture = (ImageView) findViewById(R.id.left_pic); // get listView event
        // get profile number and convert it to a valid res (in case of picture changed)
        profileNumber = userInfo.getProfile();
        userid = userInfo.getId();
        pictureRes = profileToRes(profileNumber);
        mProfilePicture.setImageResource(pictureRes);
        mProfilePicture.setOnClickListener(new View.OnClickListener() { // listener image
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });

        tname = (TextView) findViewById(R.id.text_main_name);
        tlevel = (TextView) findViewById(R.id.t_blevel);
        tlevelup = (TextView) findViewById(R.id.t_lvlup);
        pXp = (ProgressBar) findViewById(R.id.progressbarXP);
      // get all the components of the main screen and load them
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
// set info for banner
        tname.setText(userInfo.getName());
        tlevel.setText("" + userInfo.getLevel());
        pXp.setMax(1000);
        pXp.setProgress((int) (userInfo.getXp() % 1000)); // xp earned

        viewPager.setCurrentItem(0); // selectionne le 1er onglet par défaut
        // LAUNCH STEP COUNTER SERVICE
        Intent i = new Intent(this, StepCounterService.class);
        startService(i);
        Intent s = new Intent(this, EventSyncService.class);
        startService(s);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
          return;  //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // menu main
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent anIntent;
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            anIntent = new Intent(getApplicationContext(), MainActivity.class);
            //anIntent.putExtra("theprofile",profileNumber);
            startActivity(anIntent);
            // viewPager.setCurrentItem(0); // test si activité créées
        } else if (id == R.id.nav_consomation){

        }
        else if (id == R.id.nav_collection) {
            anIntent = new Intent(getApplicationContext(), CollectionActivity.class);
            //anIntent.putExtra("theprofile",profileNumber);
            startActivity(anIntent);
            //viewPager.setCurrentItem(1);
        } else if (id == R.id.nav_datas){
            anIntent = new Intent(getApplicationContext(), DataAccessActivity.class);
            anIntent.putExtra("theprofile",profileNumber);
            startActivity(anIntent);

        } else if (id == R.id.nav_aboutus){
            anIntent = new Intent(getApplicationContext(), AboutUsActivity.class);
            startActivity(anIntent);
        }
        else{
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private TUserInfo loadUserInformations() {
        CircadianLocalBDD localBdd = new CircadianLocalBDD(this);
        localBdd.open(); //On ouvre la base de données pour écrire dedans
//to do : change the id , had to come from server for account identification        //Get basic informations
        //TUserInfo userInfos = localBdd.getUserInfoWithId(0);
        TUserInfo userInfos = localBdd.getUserInfos();
        if (userInfos != null)
            Log.v("usrInf_String2", userInfos.toString());
        else
            Log.e("LOCALBDD", "ERROR RETRIVING USERINFORMATIONS");
        localBdd.close(); //On ferme la base de données

        return userInfos;
    }


    private Date ConvertToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
            Log.i("DATE_CONVERTED", "" + dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.e("DATE_CONVERTED", "FAILURE");
            e.printStackTrace();
        }
        return convertedDate;
    }

//TODO: Déplacer dans le bootLoader
    // Communication with server part
    public void recordActivity() {

        // prend le contenu de la base sqlite si il n'est pas vide et l'enregistre sur le serveur
        RequestParams params = new RequestParams();
        //envoi des parametres
        Log.i("NETWORK", "1_id_GET:" + userInfo.getId() + " last activity");
        params.put("id", userInfo.getId());

        conn = new ConnexionHandler(this);
        if(conn.haveInternetConnection()) {
            try {
                conn.getActivityRecordDate(params);//launch the asynchronous Task
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCommunicationSuccess(String output) {
        CircadianLocalBDD localBdd = new CircadianLocalBDD(this);
        localBdd.open(); //open local database to get activity

        String lastActivityDate = "";
        lastActivityDate = output;
        Log.i("NETWORK_M", "2_ACTIVITY_GET:" + lastActivityDate);
        //ConvertToDate(lastActivity);
        JSONArray activityRecorded = localBdd.getUserActivitySince(lastActivityDate);
// envoi toute la nouvelle activité au serveur
        localBdd.close(); // close local database
        if (activityRecorded != null) {
            int activitySize = activityRecorded.length();
            Log.i("NETWORK_M", "3.1_ACTIVITY_TO_RECORD_JSON_READY_TO_SEND_size:" + activitySize);
            Log.i("NETWORK_M", "3.1_ACTIVITY_TO_RECORD_JSON_READY_TO_SEND_content:" + activityRecorded.toString());
            if (activitySize > 0) { // send it to server
                RequestParams params = new RequestParams();
                params.put("json", activityRecorded.toString()); //todo : test with json only
                params.put("id", userInfo.getId()); // user id to sign activity
                conn.postActivity(params); // send activity to server
                // need 2nd part of interface to launch asynchronous activity
            } else
                Log.i("NETWORK_M", "3.2_ACTIVITY_UP_TO_DATE");

        } else
            Log.i("NETWORK_M", "3.2_ERR");
        try {
            conn.getGlobalActivity(); // récupère l'activité globale
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommunicationFailure() {

    }
/*
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.aliano.clientcircadian_va/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.aliano.clientcircadian_va/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
*/
    public int profileToRes(int profile) {
        // CONVERT A PROFIL ID TO THE CORRESPONDING RES IMAGE
        int res = -1;
        switch (profile) {
            case 0:
                res = R.drawable.bat;
                break;
            case 1:
                res = R.drawable.chouette;
                break;
            case 2:
                res = R.drawable.hummingbird;
                break;
            case 3:
                res = R.drawable.mesange;
                break;
            case 4:
                res = R.drawable.alouette2;
                break;
        }
        return res;
    }


    private void showProfile() {
        // A LA PLACE mettre le DFRAG
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.profile_view, null);
        TextView txt = (TextView) view.findViewById(R.id.tProfile); // get the view selected
        ImageView img = (ImageView) view.findViewById(R.id.iProfile); // get the view selected

        CharSequence Msg = "";
        switch (profileNumber) {
            case 0:
                Msg = getText(R.string.profile0); // getText instead of getString to keep html format
                break;
            case 1:
                Msg = getText(R.string.profile1);
                break;
            case 2:
                Msg = getText(R.string.profile2);
                break;
            case 3:
                Msg = getText(R.string.profile3);
                break;
            case 4:
                Msg = getText(R.string.profile4);
                break;
        }
        txt.setText(Msg); // to keep the <b> balises and html formats
        img.setImageResource(pictureRes);

        builder.setView(view);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        builder.show();
    }

    public void winEXPerience(int xpEarned){
        int current_level = Integer.parseInt(tlevel.getText().toString());
        int new_level = current_level;
        int total_xp;
        int new_progress;
        int level_earned;

//TODO: Animation barre d'xp
        total_xp = pXp.getProgress() +xpEarned;
        level_earned = total_xp / pXp.getMax();
        Log.i("XP","current_level="+current_level+" new_level="+new_level+" total xp"+total_xp+" level earned= "+level_earned);
        if( level_earned >0){ // one or more level earned
            new_level += level_earned;
            this.tlevel.setText("" +new_level);
            textAnimation(tlevelup);
            // launch animation
        }

        new_progress =  (total_xp) % pXp.getMax();
        //Save in Local DB updateUserXp
        CircadianLocalBDD localBdd = new CircadianLocalBDD(this);
        localBdd.open(); //On ouvre la base de données pour écrire dedans
        localBdd.updateUserXp(userInfo.getId(), new_level, new_progress);
        localBdd.close(); //On ferme la base de données locale
        pXp.setProgress(new_progress);
    }

    public void textAnimation(TextView aniView){
        Animation myBlinkInAnimation = AnimationUtils.loadAnimation(this, R.anim.blinkanim); // load configured animation
        aniView.setVisibility(View.VISIBLE);
        aniView.startAnimation(myBlinkInAnimation);
        aniView.setVisibility(View.INVISIBLE);
    }
}
