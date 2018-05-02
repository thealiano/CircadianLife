package com.example.aliano.clientcircadian_va;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TGlobalActivity;
import com.google.android.gms.vision.text.Text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Aliano on 20/05/2017.
 */
public class FragmentMain_tab2 extends Fragment { // support app v4 ...
    private static Context contextOfApplication;

    WebView chartFriendChronotype;
    final String chartSocialSize = "300x128";
    String social ="0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
    String global ="0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
    long startClickTime;
    private static final int MAX_CLICK_DURATION = 100;
    String sSocial, hourSocial;
    private TextView t_step, t_kcal,t_dailypeak,t_step_global,t_kcal_global,t_dailypeak_global;
    private Spinner spin_period, spin_kind;

    int numberOfDays = 6; // nombre de jours


    int step_count= 0;
    int kcal_count = 0;
    int activityPeak =0;
    int activityPeakGlobal = 0;
    int maxpercent = 15;
    long nb_record_global = 0;

//TODO: getGlobalActivityWithType
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main_social, container, false);
        super.onCreate(savedInstanceState);
        contextOfApplication = getActivity();//.getApplicationContext();

        t_step = (TextView) view.findViewById(R.id.text_step);
        t_kcal = (TextView) view.findViewById(R.id.text_kcal);
        t_dailypeak = (TextView) view.findViewById(R.id.text_dailypeak);
        //GLOBAL ACTIVITY
        t_step_global = (TextView) view.findViewById(R.id.text_step_avg);
        t_kcal_global = (TextView) view.findViewById(R.id.text_kcal_avg);
        t_dailypeak_global = (TextView) view.findViewById(R.id.text_dailypeak_avg);

        spin_period = (Spinner) view.findViewById(R.id.spinner_period);
        spin_kind = (Spinner) view.findViewById(R.id.spinner_kind);
        //spinner_kind
        chartFriendChronotype = (WebView) view.findViewById(R.id.chartSocial); // set up the charts


        AdapterView.OnItemSelectedListener spinner_adapter = new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //open local bdd
                CircadianLocalBDD localBdd = new CircadianLocalBDD(contextOfApplication);
                localBdd.open(); //On ouvre la base de données pour lire dedans
                if(spin_period.getSelectedItemPosition() == 0) {
                    numberOfDays = 7;
                }
                if(spin_period.getSelectedItemPosition() == 1) {
                    numberOfDays = 30;
                }
                if(spin_period.getSelectedItemPosition() == 2) {
                    numberOfDays = 90;
                }

                long[] htab = new long[24];
                long[] htab_global = new long[24];

                int nb_record = 0;
                //mode => global=0, workingday=1, weekend=2;
                int mode = spin_kind.getSelectedItemPosition();

                step_count = localBdd.getUserStepCountSince(numberOfDays,mode);
                kcal_count = localBdd.getUserKcalCountSince(numberOfDays,mode);
                htab = localBdd.getPickOfActivity(numberOfDays,mode);
                Log.i("Social","mode"+mode+"ndays"+numberOfDays);

                long max = 0;
                social="";
                for (int i=0; i < htab.length; i++){ // permet de récupérer chaque valeur individuellement
                    if(htab[i] > max){
                        max = htab[i];
                        activityPeak = i;
                    }
                    nb_record += htab[i];
                }

                // GLOBAL PART
                String glo_type="";
                switch(mode){
                    case 0:
                        glo_type = "GLO";
                        break;
                    case 1:
                        glo_type ="WDA";
                        break;
                    case 2:
                        glo_type="WEA";
                        break;
                }
                glo_type = glo_type +numberOfDays;
                Log.i("getActivityAll",glo_type);
                TGlobalActivity GlobalActivity = localBdd.getGlobalActivityWithType(glo_type);
                Log.v("getActivityAll",GlobalActivity.toString());
                localBdd.close();
                // set global tab
                htab_global =getGlobalRepartitionTab(GlobalActivity.getJson());


                if(nb_record > 0) {
                    social = getHoursValuesString(htab, nb_record);
                    global = getHoursValuesString(htab_global,nb_record_global);
                    Log.i("FRAG_STRING_social",social);
                    Log.i("FRAG_STRING_global",global);
                    // playground -> https://developers.google.com/chart/image/docs/chart_playground
                    //https://developers.google.com/chart/image/docs/data_formats
                    // cha = autoscale !!
                    //sSocial = "http://chart.apis.google.com/chart?cht=bvg&chco=000066&chs=" + chartSocialSize + "&chds=a&chxt=x,x,y,y&chxl=1:|Repartition Activity(local%20time)|3:|%&chxp=1,50|3,50&chbh=a,5,15&chxr=0,0,23,2|2,0,"+maxpercent+"&chd=t:"+social +"&chf=bg,s,edf5ff";
                    sSocial = "http://chart.apis.google.com/chart?cht=lc" + //line chart
                            "&chco=FF000080,00006680" +
                            "&chs=" + chartSocialSize +
                            "&chds=a" + // autoscale
                            "&chm=B,FF000080,0,1.0,5.0|B,00006680,1,1.0,5.0"+
                            "&chxt=x,x,y,y" +
                            "&chxl=1:|Repartition Activity(local%20time)|3:|%" + // titre des axes
                            "&chdl="+getString(R.string.me)+"|"+getString(R.string.avg_users)+
                            "&chxp=1,50|3,50" +
                            "&chbh=a,5,15" +
                            "&chxr=0,0,23,2|2,0,"+maxpercent +
                            "&chd=t:"+social +"|"+global + //données
                            "&chf=bg,s,edf5ff"+
                            "&chdls=000000,10"+ // taille et couleur de la légende
                            "&chdlp=t"; // légende au dessus (b -> bottom , r -> right , l -> left, bv,tv

                    chartFriendChronotype.loadUrl(sSocial);
                }

                Log.i("Social","activity :"+social);
                t_step.setText(""+step_count);
                t_kcal.setText(""+kcal_count);
                t_dailypeak.setText(activityPeak+" h"); // gérer l'anglais
                // population

                t_step_global.setText(""+GlobalActivity.getAvgSteps());
                t_kcal_global.setText(""+GlobalActivity.getAvgKcals());
                t_dailypeak_global.setText(activityPeakGlobal+" h"); // gérer l'anglais

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        };
        spin_period.setOnItemSelectedListener(spinner_adapter);
        spin_kind.setOnItemSelectedListener(spinner_adapter);
        chartFriendChronotype.loadUrl(sSocial);
        //chartFriendChronotype.setBackgroundColor(Color.TRANSPARENT);
        //chartFriendChronotype.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        View.OnTouchListener onClickGraph = new View.OnTouchListener() {
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
                                case R.id.chartSocial: /** chart social */
                                    urlTemp = sSocial;
                                    urlTemp = urlTemp.replace("chs=" + chartSocialSize, "chs=280x200");
                                    showGraphic(R.string.chartSocial_name, urlTemp, getString(R.string.chartSocial));
                                    break;
                            }
                        }
                    }
                }return false;
            }
        };
        //spin_kind.setSelection(0); // force le type à global au départ
        spin_period.setSelection(1); // force the first choice
        chartFriendChronotype.setOnTouchListener(onClickGraph);
        return view;
    }

    public static FragmentMain_tab2 newInstance(String text) {

        FragmentMain_tab2 f = new FragmentMain_tab2();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
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
    public String getHoursValuesString(long[] htab, long nb_record){
        double normalize = 100f / (double)nb_record;
        hourSocial ="";
        maxpercent = 0;
        for (int i=0; i < htab.length; i++){ // permet de récupérer chaque valeur individuellement
            int percent = (int)((double)htab[i]*normalize);
            if(percent>maxpercent)
                maxpercent = percent + 1 ; // valeur maximum du graphique (+1 pour un petit espace)
             hourSocial += ""+percent+",";
        }
        return hourSocial.substring(0,hourSocial.length()-1); // enleve la derniere virgule
    }
    public long[] getGlobalRepartitionTab(String json){
        long[] htab = new long[24];
        long nb_record_total = 0;
        long max = 0;
        try {
            JSONArray jarray = new JSONArray(json);
            JSONObject GlobalActivity = null;
            //24 h -> il peut manquer des enregistrements (quand le serveur n'a encore rien recu)
            for (int i = 0; i < jarray.length(); i++) {
                GlobalActivity = (JSONObject) jarray.get(i); // hour et count
               // Log.i("FRA2_getActivityAll","i="+i+",h:" +GlobalActivity.getString("hour"));
               // Log.i("FRA2_getActivityAll", GlobalActivity.getString("count"));
                int hour =  Integer.parseInt(GlobalActivity.getString("hour"));
                htab[hour] =  Long.parseLong(GlobalActivity.getString("count"));

                if(htab[hour] > max){
                    max = htab[hour];
                    activityPeakGlobal = hour; // heure de pic global
                }
                nb_record_total += htab[hour];

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // set global variables
        nb_record_global = nb_record_total;

        return htab;
    }
}
