package com.example.aliano.clientcircadian_va;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.aliano.clientcircadian_va.Questionnary.DateSelectors.DateSelectorPicker;
import com.example.aliano.clientcircadian_va.SensorProviders.SoftwarePodometer.StepDetector;
import com.example.aliano.clientcircadian_va.SensorProviders.SoftwarePodometer.StepListener;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TUserInfo;
import com.example.aliano.clientcircadian_va.SunriseAndSet.SunriseSunsetCalculator;
import com.example.aliano.clientcircadian_va.SunriseAndSet.dto.Location;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Aliano on 20/05/2017.
 */
public class FragmentMain_tab1 extends Fragment implements View.OnClickListener { // support app v4 ...
    private TextView tPod, tKcal;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "";
    private int numSteps;
    private static Context contextOfApplication;
    public static GoogleApiClient mApiClient; // mapApi object static for recognition

    private Button buttonValidateTS;
    private WebView chartWeeklyActivity, wActogram;//, chartWDvNWD;
    private CheckBox cToday, cYesterday;
    public int pictureRes, profileNumber;
    TUserInfo userInfo;

    long startClickTime; // for avoid fragment launch when drag and drop
    // chart size for main
    final String chartSize = "155x155";

    String sweeklyActivity, sWdNwd;
    private static final int MAX_CLICK_DURATION = 100;
    View.OnTouchListener onClickGraph;
    // date Pickers
    DateSelectorPicker datePicker1, datePicker2;
    String s_sunrise, s_sunset;
    String urlActogramBase = "http://upnaesrv2.epfl.ch:3838/sample-apps/circadianLife/?id=";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.content_main, container, false);
        super.onCreate(savedInstanceState);

        contextOfApplication = getActivity();//.getApplicationContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);

        userInfo = loadUserInformations(); // load user informations from local bdd
        Log.i("actogram",urlActogramBase+userInfo.getId() +" load");

        // chart and actogram
        chartWeeklyActivity = (WebView) view.findViewById(R.id.chartWA); //get Chart object
        wActogram = (WebView) view.findViewById(R.id.actogram); // actogram
        wActogram.getSettings().setJavaScriptEnabled(true); // enable javascript
        wActogram.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("actogram","webview Error");
            }
        });

        wActogram.loadUrl(urlActogramBase +userInfo.getId());// set actogram

        //String sweeklyActivity = "http://chart.apis.google.com/chart?cht=rs&chm=B,FF990080,0,1.0,5.0|B,00006680,1,1.0,5.0|V,00FF0080,0,4.0,5.0&chls=2.0,4.0,0.0|2.0,4.0,0.0&chs=" +chartSize +"&chco=FF9900,000066&chd=t:10,20,30,40,50,60,70,80,90,100,0,10,20|100,90,80,70,60,50,40,30,20,10,0,10,20&chxt=x&chxl=0:|12|1|2|3|4|5|6|7|8|9|10|11&chf=bg,s,edf5ff";
         // sWdNwd = "http://chart.apis.google.com/chart?cht=rs&chm=B,FF000080,0,1.0,5.0|B,00006680,1,1.0,5.0&chls=2.0,4.0,0.0|2.0,4.0,0.0&chs="+chartSize+"&chco=FF0000,000066&chd=t:10,20,30,40,50,60,70,80,90,100,0,10,20,10,20,30,40,50,60,70,80,90,100,0|20,30,40,50,60,70,80,90,100,0,10,20,100,90,80,70,60,50,40,30,20,10,10,20&chxt=x&chxr=0,0,23,1&chf=bg,s,edf5ff";

        cToday = (CheckBox) view.findViewById(R.id.checkBoxT);
        cYesterday = (CheckBox) view.findViewById(R.id.checkBoxY);

        // mettre param par défaut
        View.OnClickListener checkBoxListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Float[] userActivity, userActivity_ACC;
                //String userActivityStr, userActivityStrYD; // ACTIVITY RECOGNITION
                String userActivityStr_ACC, userActivityStrYD_ACC; // ACCELEROMETER RECOGNITION
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                String dayAndNight = getEphemerideStringForCharts(48, 2);
                Log.v("dayAndNight", dayAndNight);

                if (cToday.isChecked()) {
                    // Activity recognition mode = 0
                    //userActivity = getUserActivityByHour(24, 2, new Date(), 0);
                    //userActivityStr = getUserActivityString(userActivity, 0);
                    // acc recognition mode (last parameter) = 1
                    userActivity_ACC = getUserActivityByHour(24, 2, new Date(), 2);
                    userActivityStr_ACC = getUserActivityString(userActivity_ACC, 2);
                    //sWdNwd = "http://chart.googleapis.com/chart?cht=r&chm=B,FF000070,0,1.0,5.0|B,0000FF30,1,1.0,5.0|B,FFFF0060,2,1.0,5.0&chls=1|0|0&chco=FF0000,000000,000000&chs=" + chartSize + "&chd=t:" + userActivityStr + dayAndNight + "&chxt=x&chxl=0:|0||1||2||3||4||5||6||7||8||9||10||11||12||13||14||15||16||17||18||19||20||21||22||23|&chf=bg,s,edf5ff";
                    sweeklyActivity  = "http://chart.googleapis.com/chart?cht=r&chm=B,FF000070,0,1.0,5.0|B,0000FF30,1,1.0,5.0|B,FFFF0060,2,1.0,5.0&chls=1|0|0&chco=FF0000,000000,000000&chs=" + chartSize + "&chd=t:" + userActivityStr_ACC + dayAndNight + "&chxt=x&chxl=0:|0||1||2||3||4||5||6||7||8||9||10||11||12||13||14||15||16||17||18||19||20||21||22||23|&chf=bg,s,edf5ff";

                    if (cYesterday.isChecked()) { // TODAY + YESTERDAY
                       // userActivityStrYD = getUserActivityString(getUserActivityByHour(24, 2, cal.getTime(), 0), 0);
                        //sWdNwd = "http://chart.googleapis.com/chart?cht=r&chm=B,02FE0055,0,1.0,5.0|B,FF000055,1,1.0,5.0|B,0000FF30,2,1.0,5.0|B,FFFF0060,3,1.0,5.0&chls=1|1|0|0&chco=02FE00,FF0000,000000,000000&chs=" + chartSize + "&chd=t:" + userActivityStrYD + userActivityStr + dayAndNight + "&chxt=x&chxl=0:|0||1||2||3||4||5||6||7||8||9||10||11||12||13||14||15||16||17||18||19||20||21||22||23|&chf=bg,s,edf5ff";
                        userActivityStrYD_ACC = getUserActivityString(getUserActivityByHour(24, 2, cal.getTime(), 2), 2);
                        sweeklyActivity = "http://chart.googleapis.com/chart?cht=r&chm=B,02FE0055,0,1.0,5.0|B,FF000055,1,1.0,5.0|B,0000FF30,2,1.0,5.0|B,FFFF0060,3,1.0,5.0&chls=1|1|0|0&chco=02FE00,FF0000,000000,000000&chs=" + chartSize + "&chd=t:" + userActivityStrYD_ACC + userActivityStr_ACC + dayAndNight + "&chxt=x&chxl=0:|0||1||2||3||4||5||6||7||8||9||10||11||12||13||14||15||16||17||18||19||20||21||22||23|&chf=bg,s,edf5ff";

                    }
                } else if (cYesterday.isChecked()) {
                    //userActivityStrYD = getUserActivityString(getUserActivityByHour(24, 2, cal.getTime(), 0), 0);
                    //sWdNwd = "http://chart.googleapis.com/chart?cht=r&chm=B,02FE0055,0,1.0,5.0|B,0000FF30,1,1.0,5.0|B,FFFF0060,2,1.0,5.0&chls=1|0|0&chco=02FE00,000000,000000&chs=" + chartSize + "&chd=t:" + userActivityStrYD + dayAndNight + "&chxt=x&chxl=0:|0||1||2||3||4||5||6||7||8||9||10||11||12||13||14||15||16||17||18||19||20||21||22||23|&chf=bg,s,edf5ff";
                    // mode acc
                    userActivityStrYD_ACC = getUserActivityString(getUserActivityByHour(24, 2, cal.getTime(), 2), 2);
                    sweeklyActivity = "http://chart.googleapis.com/chart?cht=r&chm=B,02FE0055,0,1.0,5.0|B,0000FF30,1,1.0,5.0|B,FFFF0060,2,1.0,5.0&chls=1|0|0&chco=02FE00,000000,000000&chs=" + chartSize + "&chd=t:" + userActivityStrYD_ACC + dayAndNight + "&chxt=x&chxl=0:|0||1||2||3||4||5||6||7||8||9||10||11||12||13||14||15||16||17||18||19||20||21||22||23|&chf=bg,s,edf5ff";

                }
                // set chart object
                chartWeeklyActivity.loadUrl(sweeklyActivity); //
                chartWeeklyActivity.setOnTouchListener(onClickGraph);


                // 2ND GRAPH
                //chartWDvNWD.loadUrl(sWdNwd);

                //chartWDvNWD.setOnTouchListener(onClickGraph);
            }
        };
        cToday.setOnClickListener(checkBoxListener);
        cYesterday.setOnClickListener(checkBoxListener);

        onClickGraph = new View.OnTouchListener() {
            String urlTemp;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // we cannot use onclic event for WebView so we simulate it with an onTouch event and a timer
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: { // when the user click on graphics
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            //Log.v("click duration", "" +clickDuration);
                            if (clickDuration > MAX_CLICK_DURATION) {
                                switch (v.getId()) {
                                    /*case R.id.chartWDNWD:
                                        urlTemp = sWdNwd;
                                        urlTemp = urlTemp.replace("chs=" + chartSize, "chs=300x300");
                                        showGraphic(R.string.chartWdNwd_name, urlTemp, getString(R.string.chartWdNwd));
                                        break;*/
                                    case R.id.chartWA: /** Chart Weekly Activity */
                                        urlTemp = sweeklyActivity;
                                        urlTemp = urlTemp.replace("chs=" + chartSize, "chs=300x300");
                                        showGraphic(R.string.chartWA_name, urlTemp, getString(R.string.chartWA));
                                        break;
                                }
                            }
                        }
                }return false;
            }
        };
        // graph Board zone
      //  final TextView date1 = (TextView) view.findViewById(R.id.tdate1);
      //  final TextView date2 = (TextView) view.findViewById(R.id.tdate2);
        TextView hsunrise = (TextView) view.findViewById(R.id.hSunRise);
        TextView hsunset = (TextView) view.findViewById(R.id.hSunSet);

        //date1.setText(getString(R.string.please_select_str));
        //date2.setText(getString(R.string.please_select_str));

       /* date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(datePicker1, date1);
            }
        });
        date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(datePicker2, date2);
            }
        });*/
// TODO : get true location
        Float shared_locationLat = sharedPref.getFloat("locationlat", 46.5333f);
        Float shared_locationLong = sharedPref.getFloat("locationlong", 6.6668f);
        Log.i("location_eph","location used for ephemerides: "+shared_locationLat +","+shared_locationLong);
        //private FusedLocationProviderClient mFusedLocationClient;
        Location true_location = new Location(shared_locationLat, shared_locationLong); // lausanne lat ,long
       /* if(true_location != null){
            Log.i("location","get "+true_location.getLatitude()+","+true_location.getLongitude());

        }*/
        String Ephemerides[] = getEphemerides(true_location);
        s_sunrise = Ephemerides[0];
        hsunrise.setText(s_sunrise);
        s_sunset = Ephemerides[1];
        hsunset.setText(s_sunset);

        chartWeeklyActivity.setBackgroundColor(Color.WHITE); // to fix the white borders
        chartWeeklyActivity.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        // Podometer and kcal part text_pod
        tPod = (TextView) view.findViewById(R.id.text_pod);
        tKcal = (TextView) view.findViewById(R.id.text_kcal);

        String shared_steps = sharedPref.getString("numSteps", "0");
        Float shared_kcal = sharedPref.getFloat("numKcal",0f);

        tPod.setText(shared_steps);
        tKcal.setText(""+Math.round(shared_kcal));

        checkBadgesFootKcal(Integer.parseInt(shared_steps),shared_kcal);

        return view;
    } // à débloqué avec les pas d'hier, rajouté issaw dans les badge
    public void checkBadgesFootKcal(int footsteps, float kcal){
        int min = 500;
        if (footsteps> min) {
            CircadianLocalBDD localBdd = new CircadianLocalBDD(contextOfApplication);
            localBdd.open();
            Intent gotoObtainBadge;
            if(!localBdd.isBadgeUnlock("badge_8",contextOfApplication)){
                gotoObtainBadge = new Intent(contextOfApplication, BadgeObtainActivity.class);
                gotoObtainBadge.putExtra("badge_string_name", "badge_8"); // circadian badge
                startActivity(gotoObtainBadge); // principe d'une pile : activités en ordre inversé
            }
            if(footsteps>1000){
                if(!localBdd.isBadgeUnlock("badge_9",contextOfApplication)){
                    gotoObtainBadge = new Intent(contextOfApplication, BadgeObtainActivity.class);
                    gotoObtainBadge.putExtra("badge_string_name", "badge_9"); // circadian badge
                    startActivity(gotoObtainBadge);
                }
                if(footsteps>3000) {
                    if (!localBdd.isBadgeUnlock("badge_10",contextOfApplication)) {
                        gotoObtainBadge = new Intent(contextOfApplication, BadgeObtainActivity.class);
                        gotoObtainBadge.putExtra("badge_string_name", "badge_10"); // circadian badge
                        startActivity(gotoObtainBadge);
                    }
                    if(footsteps>10000) {
                        if (!localBdd.isBadgeUnlock("badge_11",contextOfApplication)) {
                            gotoObtainBadge = new Intent(contextOfApplication, BadgeObtainActivity.class);
                            gotoObtainBadge.putExtra("badge_string_name", "badge_11"); // circadian badge
                            startActivity(gotoObtainBadge);
                        }
                    }
                }
            }
            localBdd.close();
        }
    }
    public void onClick(final View v) { //check for what button is pressed

    }
    public static FragmentMain_tab1 newInstance(String text) {
        FragmentMain_tab1 f = new FragmentMain_tab1();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }
    public void rowClick(View view) {
        switch (view.getId()) {
            /*case R.id.tbadges:
                if (findViewById(R.id.tbadges_content).getVisibility() == View.VISIBLE)
                    findViewById(R.id.tbadges_content).setVisibility(View.GONE);
                else
                    findViewById(R.id.tbadges_content).setVisibility(View.VISIBLE);*/
            case R.id.tevenement:
                if (view.findViewById(R.id.tevenement_content).getVisibility() == View.VISIBLE)
                    view.findViewById(R.id.tevenement_content).setVisibility(View.GONE);
                else
                    view.findViewById(R.id.tevenement_content).setVisibility(View.VISIBLE);
                break;
            case R.id.tsocial:
                if (view.findViewById(R.id.tsocial_content).getVisibility() == View.VISIBLE)
                    view.findViewById(R.id.tsocial_content).setVisibility(View.GONE);
                else
                    view.findViewById(R.id.tsocial_content).setVisibility(View.VISIBLE);
                break;
           /* case R.id.tstats:
                if (findViewById(R.id.tstats_content).getVisibility() == View.VISIBLE)
                    findViewById(R.id.tstats_content).setVisibility(View.GONE);
                else
                    findViewById(R.id.tstats_content).setVisibility(View.VISIBLE);
                break;*/
        }
    }

    private void showGraphic(int restitle, String url, String Msg) { // on click on one graphic
        // A LA PLACE mêttre le DFRAG
        AlertDialog.Builder builder = new AlertDialog.Builder(contextOfApplication);
        LayoutInflater factory = LayoutInflater.from(contextOfApplication);
        final View view = factory.inflate(R.layout.graphic_view, null);
        TextView txtitle = (TextView) view.findViewById(R.id.tChartTitle); // get the view selected
        TextView txtcontent = (TextView) view.findViewById(R.id.tChart); // get the view selected
        WebView wv = (WebView) view.findViewById(R.id.iChart); // get the view selected
        txtitle.setText(getString(restitle));
        txtcontent.setText(Msg);
        // set chart object
        wv.loadUrl(url); //
        builder.setView(view);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        builder.show();
    }
    private TUserInfo loadUserInformations() {
        CircadianLocalBDD localBdd = new CircadianLocalBDD(contextOfApplication);
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

    public String getUserActivityString(Float[] userActivity, int mode) {
        // 0 : mode activity recognition
        // 1 : mode accelerometer only
        // 2 : mode confidence
        // l'activité correspond aux periodes ou l'on a pas de STILL
        //
// TODO : Calibration pour être plus précis on normalise ensuite sur le max et le min
        String strValues = "";
        int normalisation; // mise a l'échelle
        // normalisation par rapport au max (environ 3) doit dépendre de la calibration
        for (Float i : userActivity) {
            //normalisation = (int)Math.abs(i*30);// *30 pour un max de 3
            if (mode == 0 || mode == 2)
                strValues += Math.round(i) + ",";
            else if (mode == 1) {
                normalisation = (int) Math.abs(i * 30);// *30 pour un max de 3
                strValues += normalisation + ","; // normalisation
            }
        }
        strValues = new StringBuilder(strValues).deleteCharAt(strValues.length() - 1).toString();
        strValues += "|";
        Log.v("stringValues", strValues);
        return strValues;
    }

    public String getEphemerideStringForCharts(int hoursTabSize, int decoupageH) {
        // tab size = taille du tableau , decoupage = découpage des heures 2= 30min , 4= 15min ,....
        String[] hoursSR = s_sunrise.split(":"); // 12:22 -> 12 & 22
        int hourSR = Integer.parseInt(hoursSR[0]);
        int minuteSR = Integer.parseInt(hoursSR[1]);
        String[] hoursSS = s_sunset.split(":");
        int hourSS = Integer.parseInt(hoursSS[0]);
        int minuteSS = Integer.parseInt(hoursSS[1]);
        String day = "";
        String night = "";
        int iSR = hourSR * decoupageH;
        int iSS = hourSS * decoupageH;
        if (minuteSR >= 60 / decoupageH / 2)
            iSR += 1;
        if (minuteSS >= 60 / decoupageH / 2)
            iSS += 1;
        Log.v("iSR & iSS", iSR + " & " + iSS);
        for (int i = 0; i <= hoursTabSize; i++) {
            if (i == iSR || i == iSS) {
                day += "100,"; // MOMENT OU LA NUIT ET LE JOUR SE REJOIGNENT
                night += "100,";
            } else if (i > iSR && i < iSS) {
                day += "100,"; // graphique day apparait et nuit disparait
                night += "0,";
            } else {
                day += "0,"; // inverse
                night += "100,";
            }
        }
        day = new StringBuilder(day).deleteCharAt(day.length() - 1).toString();
        night = new StringBuilder(night).deleteCharAt(night.length() - 1).toString();

        return night + "|" + day;

    }


    public Float[] getUserActivityByHour(int nbHours, int hoursCut, Date day, int mode) {
        // nb hours = nombres d'heures considérés sur le graphique
        // exemple : 24 h , 12 h , ...
        // hoursCur = découpage
        // exemple : 2 = en demi heures, 4 en quart d'heures ...
        // mode : 0 = activity recognition values
        // 1 : accelerometer: normalized datas
        // 2 : activity confidence

        Float[] avgActivityValues = new Float[nbHours * hoursCut]; // tableau de valeurs moyenne
        // taille : découpage * nombre d'heures

// TODO : works with every date
        // Get the current date (day) and the previous day
        Calendar cal = Calendar.getInstance();
        cal.setTime(day); // day selected
        cal.add(Calendar.DATE, -1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String yearDayMonthDay = dateFormat.format(day);
        final String yearDayMonthPreviousDay = dateFormat.format(cal.getTime());
        Log.i("DATE_CURRENT", yearDayMonthDay);
        Log.i("DATE_PREVIOUS_DAY", yearDayMonthPreviousDay);

        String hour1, hour2;
        int tempH1, tempH2;
        final int heuremin = 60;

        //open local bdd
        CircadianLocalBDD localBdd = new CircadianLocalBDD(contextOfApplication);
        localBdd.open(); //On ouvre la base de données pour lire dedans
        for (int i = 0; i < nbHours * hoursCut; i++) { // pour 24 h en 1/2h on a: 0 -> 47 cases (intervales)
            hour1 = " ";
            hour2 = " ";
            // receive 0 if not set
            if (i % 2 == 0) { // pair
                // first part
                if (i == 0) // 1st interval = 23:30 -> 00:00 (example sur h :24 , découpage : 2)
                    tempH1 = nbHours - 1;
                else
                    tempH1 = ((i / hoursCut) - 1);
                if (tempH1 < 10) // on mets un 0 devant
                    hour1 += "0" + tempH1 + ":" + heuremin / hoursCut + ":00";
                else
                    hour1 += tempH1 + ":" + heuremin / hoursCut + ":00";
                // 2nd part
                tempH2 = (i / 2);
                if (tempH2 < 10) // on mets un 0 devant
                    hour2 += "0" + tempH2 + ":00:00";
                else
                    hour2 += tempH2 + ":00:00";
            } else if (i % 2 == 1) {// impaire
                // first part
                tempH1 = (i - 1) / 2;
                if (tempH1 < 10) {
                    hour1 += "0" + tempH1 + ":00:00";
                    hour2 += "0" + tempH1 + ":" + heuremin / hoursCut + ":00";
                } else {
                    hour1 += tempH1 + ":00:00";
                    hour2 += tempH1 + ":" + heuremin / hoursCut + ":00";
                }
            }

            hour2 = yearDayMonthDay + hour2;
            if (i == 0) {
                hour1 = yearDayMonthPreviousDay + hour1;
            } else {
                hour1 = yearDayMonthDay + hour1; // string to do the request
            }
            if (mode == 0) // activity recognition
                avgActivityValues[i] = localBdd.getUserActivityBetween2Dates(hour1, hour2);
            else if (mode == 1) { // accelerometer recognition
                avgActivityValues[i] = localBdd.getUserActivityBetween2DatesACC(hour1, hour2);
            }
            else if (mode ==2) { // activity recognition with confidence and inverted confidence (for still)
                avgActivityValues[i] = localBdd.getUserActivityBetween2Dates_withConfidence(hour1, hour2);
            }
            Log.v("string_hours", "index:" + i + " s1:" + hour1 + "," + "s2:" + hour2);
            //float usrAvg = localBdd.getUserActivityBetween2Dates("2016-10-19 23:30:00", "2016-10-20 00:00:00");
        }
        localBdd.close(); //On ferme la base de données
        return avgActivityValues;
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

    private void showTimePicker(DateSelectorPicker datePicker, TextView tdate) {
        datePicker = new DateSelectorPicker((Activity)contextOfApplication, tdate.getId());
        //tdate.setText(datePicker.getSelectedDate().getDay()+"/"+datePicker.getSelectedDate().getMonth());
    }

    private String[] getEphemerides(Location currentLocation) {
        // Pass the time zone display here in the second parameter.
        //TimeZone.getDefault().getDisplayName(); // get the time zone
        String stimeZone = TimeZone.getDefault().getID().toString(); // get Timezone id

        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(currentLocation, stimeZone);
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        String[] Ephemerides = new String[2];
        Ephemerides[0] = officialSunrise;
        Ephemerides[1] = officialSunset;
        Log.i("CurrentTimeZone", "" + TimeZone.getDefault().getID().toString());
        Log.i("Ephemerides", ">SUNRISE: " + officialSunrise + ",>SUNSET:" + officialSunset + ",TZ=" + stimeZone);
        return Ephemerides;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(StepCounterService.STEP_RESULT) // résultat d'un pas
        );
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onStop();
    }
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String steps = intent.getStringExtra(StepCounterService.STEP_MESSAGE);
            String kcals = intent.getStringExtra(StepCounterService.KCAL_MESSAGE);
            tPod.setText(steps);
            tKcal.setText(kcals);
            tPod.setTextColor(Color.parseColor("#FF154978"));
            tKcal.setTextColor(Color.parseColor("#FFD22E11")); // FFD22E11
            // do something here.
        }
    };

}
