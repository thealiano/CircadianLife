package com.example.aliano.clientcircadian_va;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aliano.clientcircadian_va.ActivityRecognition.ActivityRecognitionService;
import com.example.aliano.clientcircadian_va.ActivityRecognition.PathsenseActivityUpdateBroadcastReceiver;
import com.example.aliano.clientcircadian_va.Network.ConnexionHandler;
import com.example.aliano.clientcircadian_va.Network.OnCommunicationListener;
import com.example.aliano.clientcircadian_va.Questionnary.Form;
import com.example.aliano.clientcircadian_va.Questionnary.FormHorneOstbergLike;
import com.example.aliano.clientcircadian_va.Questionnary.FormQuestionnary;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TBadge;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TUserInfo;
import com.loopj.android.http.RequestParams;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WelcomeActivity extends Activity implements OnCommunicationListener {
       private SharedPreferences fillQuestionnary;
    private SharedPreferences.Editor fillQuestionnaryEditor;
    Form form;
    int score,profile;
    String username, birthdate, sexe;
    int weight, size, xp, level,user_id = -1;
    final int maxSize = 230;
    final int minSize = 80;
    final int maxWeight = 230;
    final int minWeight = 20;
    String badgeUnlock;
    boolean alreadyAddAProfile;
    Intent gotoProfile;
    Intent gotoObtainBadge;
    ConnexionHandler con;
    CircadianLocalBDD localBdd = new CircadianLocalBDD(this);
    TextView tloading;
    Calendar calendar = Calendar.getInstance();
    final int current_year = calendar.get(Calendar.YEAR);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        con = new ConnexionHandler(this); // NETWORK HANDLER: To register the userG
        fillQuestionnary = getSharedPreferences("questionnary_pref", MODE_PRIVATE); // pref file
        fillQuestionnaryEditor = fillQuestionnary.edit(); // open in edition mode
        alreadyAddAProfile = fillQuestionnary.getBoolean("addProfile", false);
        gotoProfile = new Intent(WelcomeActivity.this, MainActivity.class); // create the intent
        gotoObtainBadge = new Intent(WelcomeActivity.this, BadgeObtainActivity.class);


        if (alreadyAddAProfile){ // already had filled the questionnarY
            // test si la reconnaissance d'activité est dé
            if(!isMyServiceRunning(ActivityRecognitionService.class)){ // probleme the service self destroy after execution !
                Log.e("ACTIVITYSERVICE","NOT_RUNNING_launching....");
                startActivityRecognition1stTime();
            }else{Log.e("ACTIVITYSERVICE","_RUNNING");}
           if(con.haveInternetConnection()) { // ET QUE L'ID ENREGISTRER DANS LA BASE = -1

               // RECORD ACTIVITY TO SERVER
           // récupérer changer dans la base locale l'id utilisateur par l'id recu du serveur
           }
            profile = fillQuestionnary.getInt("profile", -1);
            gotoProfile.putExtra("theprofile", profile);

            //    Toast.makeText(this.getBaseContext(),"Service already Launched", Toast.LENGTH_SHORT).show();
            startActivity(gotoProfile);
        }
        else{
// todo:  loading screen
// todo:  prévoir maj des badges utlérieure
            showDisclaimer(); // to be sure the user is agree
            // lancement activity recognition pour la premiere fois
            startActivityRecognition1stTime();
            Log.i("WelcomActivity","start Activity Recognition 1st Time");
            TextView tloading = (TextView) findViewById(R.id.tloading);
            generateAllBadges(); // générer tout les badges DE DéPART (1x au premier lancement)
            tloading.setText("");// fin du chargement
            welcomeTest(); // A lancer dès que l'utilisateur à cliqué sur commencer
        }
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        } else{
            super.onBackPressed();
        }
    }
    public boolean welcomeTest(){
        Bundle args;
        final FragmentManager fragmentManager;
        boolean isFormLoaded;
        FragmentTransaction fragmentTrans;
        fragmentManager = getFragmentManager();

        FormQuestionnary questionnaryContainer;
        form = new FormHorneOstbergLike();

        questionnaryContainer = new FormQuestionnary(); // charge layout avec questionSpinner

        args = new Bundle();
        args.putInt("numberOfQuestions", form.getNumberOfQuestion());
        args.putInt("duration", form.getDuration());

        questionnaryContainer.setArguments(args);

        Log.w("FragmentSwitcher", "form.getNumberOfQuestion() = " + form.getNumberOfQuestion());
        Log.w("FragmentSwitcher", "form.getNumberOfQuestion() = "+form.getNumberOfQuestion());

        fragmentTrans = fragmentManager.beginTransaction();
        fragmentTrans.replace(R.id.content_activity_login, questionnaryContainer);
        fragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTrans.addToBackStack(null);
        fragmentTrans.commit();
        isFormLoaded = true;

        return isFormLoaded;
    }
    public Form getForm(){
        return this.form;
    }
    public void showUserInfForm(){
// to do: améliorer ergonomie avec passage automatique d'un champ a l'autre
        final EditText e_username;
        final Spinner s_sexe, s_weight, s_size, s_birthdate;
        Button b_gotoprofile;
        ArrayAdapter<String> spinnerArrayAdapter;// for choice
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.activity_firstinfos, null);
        final String viewprofil = this.getResources().getString(R.string.viewprofil);
        final String fillAllFields = this.getResources().getString(R.string.fillallfield);

        e_username = (EditText) view.findViewById(R.id.e_username);
        s_sexe = (Spinner) view.findViewById(R.id.s_sexe);
        String c_sexe[] = {"","M","F"};// Array of choices (to replace with symbols
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, c_sexe); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_sexe.setAdapter(spinnerArrayAdapter);

        s_birthdate = (Spinner) view.findViewById(R.id.s_birthdate);
        List lc_bd = new ArrayList();
        for(int bd = current_year -105; bd<= current_year; bd++){
            lc_bd.add(""+ bd);
        }
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lc_bd); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_birthdate.setAdapter(spinnerArrayAdapter);

        s_size = (Spinner) view.findViewById(R.id.s_size);
        List lc_size = new ArrayList();
        for(int size = minSize; size<=maxSize;size++){
            lc_size.add(""+size);
        }
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lc_size); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_size.setAdapter(spinnerArrayAdapter);

        s_weight = (Spinner) view.findViewById(R.id.s_weight);
        //String c_weight[] = {"","50","75","80"};// Array of choices (to replace with symbols
        List lc_weight = new ArrayList();
        for(int weight = minWeight; weight<=maxWeight;weight++){
            lc_weight.add(""+weight);
        }

        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lc_weight); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_weight.setAdapter(spinnerArrayAdapter);
        // set builder for dialog
        builder.setView(view);
        builder.setPositiveButton(viewprofil,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // to avoid closure on screen click
        dialog.setCancelable(false);  // to avoid closure on back key
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!e_username.getText().toString().equals("") &&
                        !s_weight.getSelectedItem().toString().equals("") &&
                        !s_sexe.getSelectedItem().toString().equals("") &&
                        !s_size.getSelectedItem().toString().equals("") &&
                        !s_birthdate.getSelectedItem().toString().equals("")){
                    username = e_username.getText().toString();
                    birthdate = s_birthdate.getSelectedItem().toString();
                    sexe = s_sexe.getSelectedItem().toString();
                    weight = Integer.parseInt(s_weight.getSelectedItem().toString());
                    size = Integer.parseInt(s_size.getSelectedItem().toString());

                    // go to next activity and create the table
                    dialog.dismiss(); // close dialog
                    //   dialog.cancel();
                    showProfile();
                }else{
                    Toast.makeText(WelcomeActivity.this,fillAllFields, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void showProfile(){ //called by fragment manager
        // A LA PLACE mêttre le DFRAG
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.profile_view, null);
        TextView txt = (TextView) view.findViewById(R.id.tProfile); // get the view selected
        ImageView img = (ImageView) view.findViewById(R.id.iProfile); // get the view selected

        String Msg ="";
        badgeUnlock ="badge_";
        int res;
        if (score <6) {
            Msg = getString(R.string.profile0);
            res = R.drawable.bat;
            badgeUnlock = badgeUnlock + "1";
            profile = 0;
        }
        else if(score <11) {
            Msg = getString(R.string.profile1);
            res = R.drawable.chouette;
            badgeUnlock = badgeUnlock + "2";
            profile = 1;

            // badge_string_name = badges
        }
        else if(score <17) {
            Msg = getString(R.string.profile2);
            res = R.drawable.hummingbird;
            badgeUnlock = badgeUnlock + "3";
            profile = 2;
        }
        else if(score <22) {
            Msg = getString(R.string.profile3);
            res = R.drawable.mesange;
            badgeUnlock = badgeUnlock + "4";
            profile = 3;
        }
        else {
            Msg = getString(R.string.profile4);
            res = R.drawable.alouette2;
            badgeUnlock = badgeUnlock + "5";
            profile = 4;
        }

        //profile = res;
        fillQuestionnaryEditor.putBoolean("addProfile", true); // add preferences
        fillQuestionnaryEditor.putInt("profile", profile); // add preferences
        fillQuestionnaryEditor.commit();
        txt.setText(Msg);
        img.setImageResource(res);

        builder.setView(view);
        builder.setCancelable(false); // to avoid closure on back key
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
// TODO: REMPLACER PROFILE DANS LE FICHIER PHP ET L'ENVOYER EN REQUEST PARAM
                gotoProfile.putExtra("theprofile",profile); // NO NEED
                if (con.haveInternetConnection()) { // if the user have internet
                    Log.i("NETWORK", "INTERNET_DETECTED");
                    signUpWithServer(); // get a unique id from server , if the id isn't received the value recorded is -1
                }else{
                    Toast.makeText(WelcomeActivity.this,"Enable your Internet connexion before continuing ", Toast.LENGTH_LONG).show();
                }
                //setUserInfos(); // record localy in SQLite Database
                // Stack Activities
/*                startActivity(gotoProfile);
                gotoObtainBadge.putExtra("badge_string_name",badgeUnlock); // test badge
                startActivity(gotoObtainBadge);
                gotoObtainBadge.putExtra("badge_string_name","badge_0"); // circadian badge
                startActivity(gotoObtainBadge); // principe d'une pile : activités en ordre inversé
*/           }
        });
        builder.show();
    }
    private boolean signUpWithServer(){
        // get id from server
        RequestParams params = new RequestParams();
        //envoi des parametres
        Log.i("NETWORK", "SEND : W:" + weight + "s" + size + "bd:" + birthdate + "s:" + sexe);
        params.put("weight", weight);
        params.put("size", size);
        params.put("birthdate", birthdate);
        params.put("sexe", sexe);
        params.put("profile", profile);
            try {
                con.registerUserAndGetId(params);
                Log.i("NETWORK","Lancement tache asynchrone");
                //WelcomeActivity.this.user_id = Integer.parseInt(output);
                //Log.i("NETWORK","integer"+user_id);
                return true;
            } catch (JSONException e) {
                Log.i("NETWORK","JSON_FAILURE");
                e.printStackTrace();
                return false;
            }
        // sinon on laisse tomber et on le mettra a jour plus tard (pour ne pas bloquer l'utilisateur)
        //Log.i("NETWORK","INTERNET_FAILURE");
        //return false;

    }
    private void setUserInfos(){
        //open local bdd
        CircadianLocalBDD localBdd = new CircadianLocalBDD(this);
        localBdd.open(); //On ouvre la base de données pour écrire dedans
        // get unique id from server and record user basic informations
// TODO: PENSER A ENLEVER LA TABLE
        // create informations for Database
        // add WelcomeActivity.this. ?
        // EMAIL vide sera renseigné plus tard
        TUserInfo userInfos = new TUserInfo(WelcomeActivity.this.user_id, WelcomeActivity.this.username,WelcomeActivity.this.birthdate,WelcomeActivity.this.sexe, WelcomeActivity.this.weight, WelcomeActivity.this.size, WelcomeActivity.this.profile, 0, 1);

        Log.v("usrInf_String_all", userInfos.toString()); // to record later in database
        localBdd.insertUserInfo(userInfos); // Set User basic Informations
        localBdd.close(); //On ferme la base de données

    }
    // methode call by others
    public void setAnswers(String answers){
        Log.d("reponses", answers);
    }
    public void setScore(int score){
        this.score = score;
    }

    // convert a String Ressource Name to a res identifier
    private int getImageResourceByName(String resName) {
        int picId = getResources().getIdentifier(resName, "drawable", getApplicationContext().getPackageName());
        return picId;
    }
    public int getStringArrayIdentifierByName(String name) {
        // return the ressource id of the string array
        int arrayId = getResources().getIdentifier(name, "array", getPackageName());
        return arrayId;
    }

    private void generateAllBadges(){
        // to do : Tester si les badges sont à jour, sinon on les charges mais 1 seule fois
// TODO: CONSTANTE GLOBALE POUR LES BADGES CHECKER A CHAQUE NOUVELLE VERSION
        int nbBadges = 12; // trouver comment en faire une constante globale
//TODO: verifier si ca marche en Constante parameter
        localBdd.open(); //On ouvre la base de données pour écrire dedans
        for(int idB = 0; idB <nbBadges; idB++) { // idB deviendra l'id unique du badge dans la bddSqlite
            String s_badges =  "badge_" +Integer.toString(idB);
            createNewBadge(idB,getStringArrayIdentifierByName(s_badges));
        }
        localBdd.close(); // on ferme la base une fois tout les badges insérés
    }

    private void createNewBadge(int idB, int resId){
        // récupération des infos depuis le fichier strings ( un tableau correspond a un badge)
        String[] tab = getResources().getStringArray(resId);
        //Création et insertion d'un badge que l'on vient dans la bdd locale
        // UNLOCK = 0 / le badge n'est pas débloqué par défaut, date = date car par débloqué
        localBdd.insertBadge(new TBadge(idB, tab[0], tab[1], 0, "date", getImageResourceByName(tab[2])));
    }

    @Override
    public void onCommunicationSuccess(String output) {
        // success getting the id WE CAN LAUNCH MAIN ACTIVITY
        // GETTING UID = SIGN UP
        // USER HAVE TO HAVE UNIQUE ID TO SIGN EACH ACTIVITY THEY RECORD INDIVIDUALY LATER
        Log.i("NETWORK","COMMUNICATION SUCCESS");
        user_id = Integer.parseInt(output);
        Toast.makeText(this,"Id received = "+user_id, Toast.LENGTH_LONG).show();
        setUserInfos(); // enregistre les informations utilisateurs dont son id recu du serveur
        // Stack Activities
        startActivity(gotoProfile);
        gotoObtainBadge.putExtra("badge_string_name",badgeUnlock); // test badge
        startActivity(gotoObtainBadge);
        gotoObtainBadge.putExtra("badge_string_name","badge_0"); // circadian badge
        startActivity(gotoObtainBadge); // principe d'une pile : activités en ordre inversé

        // start main activity
    }

    @Override
    public void onCommunicationFailure() {
        Toast.makeText(this,"Communication failure = "+user_id, Toast.LENGTH_LONG).show();
        // to do in case of failure
    }
    public void startActivityRecognition1stTime(){
// 1st time
        PathsenseLocationProviderApi.getInstance(this).requestActivityUpdates(PathsenseActivityUpdateBroadcastReceiver.class);
        String serviceName = "com.pathsense.activitydemo.app.ActivityRecognitionService";
        Intent serviceintent = new Intent(this, ActivityRecognitionService.class);
        serviceintent.setAction(serviceName);
        startService(serviceintent); // start le service


    }

    public void showDisclaimer(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Disclaimer");
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.disclaimer_message))
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        dialog.cancel();
                     }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothin
                        WelcomeActivity.this.finish();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    public boolean activityRecognitionServiceIsLaunched(){
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        //boolean isActivityFound = false;
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        //List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < serviceInfos.size(); i++) {
            if (serviceInfos.get(i).service.getClassName().toString().equals("ActivityRecognitionService")) { //ici le nom du package recherché
                //isActivityFound = true;
                Log.i("ActivityService_Info","Service already launched");
                return true;
            }else {
                Log.i("ActivityService_Info", serviceInfos.get(i).service.getClassName().toString());
            }
        }return false;
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }
}
