package com.example.aliano.clientcircadian_va;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aliano.clientcircadian_va.Network.ConnexionHandler;
import com.example.aliano.clientcircadian_va.Network.OnCommunicationListener;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TUserInfo;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;

/**
 * Created by Aliano on 23/06/2017.
 */


public class DataAccessActivity  extends AppCompatActivity  implements OnCommunicationListener { // to get the same navigation drawer
    /** TO DO :
     * récupérer l'id utilisateur via l'intent
     */
    ConnexionHandler conn;
    EditText e_email, e_password, e_confirmPassword;
    TextView t_error;
    Button b_validate;
    String email;
    Boolean isVerified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// get shared preference
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        email = sharedPref.getString("email", "");
        isVerified = sharedPref.getBoolean("emailVerified", false);
        Log.i("email_shared", ">" + email + " l: " + email.length() +"verified?="+isVerified.toString());
        if(email =="" || email.equals("null") || email.length()==0){ // cas e-mail == null donc jamais inséré
            Log.i("email_getter",">launched");
            isEmailHasBeenVerified();   //
            setContentView(R.layout.activity_accessdatas);
            e_email = (EditText) findViewById(R.id.e_email);
            e_password = (EditText) findViewById(R.id.e_password);
            e_confirmPassword = (EditText) findViewById(R.id.e_password2);
            t_error = (TextView) findViewById(R.id.t_error);
            b_validate = (Button) findViewById(R.id.b_validate);
        }else if(!isVerified){// e-mail dans la base distante en attente de vérification
            setContentView(R.layout.activity_accessdatas_2_verification);
            TextView t_email = (TextView)findViewById(R.id.t_send);
            t_email.setText(t_email.getText() +" " +email);
        }else{ // meilleur cas = e-mail dans la base et déjà vérifié
            setContentView(R.layout.activity_accessdatas_final);
            TextView t_email = (TextView)findViewById(R.id.t_email);
            t_email.setText(t_email.getText() +" " +email);
        }


        // test si il y'a déjà une adresse vérifiée dans la base de donnée distante
        // si oui l'enregistré dans les préférences partagées
        // -> si oui écran avec les infos

        /// -> si non formulaire

        // -> si existe mais pas encore vérifiée formulaire pré-rempli avec marqué en attente de vérification



        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within the main activity
        // getLayoutInflater().inflate(R.layout.content_collection_grid, contentFrameLayout);

    }
    public void verifyFields(View v)
    { // ONCLICk 1ST SCREEN
        String s_error="";
        e_email.setTextColor(Color.BLACK);
        e_password.setTextColor(Color.BLACK);
        e_confirmPassword.setTextColor(Color.BLACK);
        String s_email = e_email.getText().toString() ,
                s_pass = e_password.getText().toString(),
                s_pass_confirm = e_confirmPassword.getText().toString();

        if((s_email.length() == 0 || s_pass.length() == 0 || s_pass_confirm.length() == 0)){
            s_error = getString(R.string.fillallfield);
            Toast.makeText(this, s_error, Toast.LENGTH_LONG).show();
            //fillallfield
        }else if(!isValidEmail(s_email)){
            //e_email.setOnFocusChangeListener();
            s_error = getString(R.string.error_wrong_input_for_email);
            e_email.setTextColor(Color.RED);
            Toast.makeText(this, s_error, Toast.LENGTH_LONG).show();
            t_error.setText(s_error);
        }else if(e_password.getText().toString().length() < 7) {
            s_error = getString(R.string.error_wrong_input_for_password);
            e_password.setTextColor(Color.RED);
            e_confirmPassword.setTextColor(Color.RED);
            Toast.makeText(this, s_error, Toast.LENGTH_LONG).show();
            t_error.setText(s_error);
        }else if(!s_pass.equals(s_pass_confirm)) {
            s_error = getString(R.string.error_wrong_input_new_user_for_password);
            e_password.setTextColor(Color.RED);
            e_confirmPassword.setTextColor(Color.RED);
            Toast.makeText(this, s_error, Toast.LENGTH_LONG).show();
            t_error.setText(s_error);
        }else{ // all good then submit PASS + EMAIL
            submitEmail(s_email, s_pass);
            //s_error ="email send ! ";
            setContentView(R.layout.activity_accessdatas_2_verification);
            TextView t_email = (TextView)findViewById(R.id.t_send);
            t_email.setText(t_email.getText() +" " +s_email);
            // lancer un thread qui vérifie périodiquement ?
        }
        //t_error.setText(s_error);

    }
    public void isEmailActive(View v) { // ONCLICk 2nd SCREEN BUTTON
        isEmailHasBeenVerified();
    }
    //isEmailActive
    public void downloadDatas(View v){
        Log.i("EMAIL","trying to retrieve user datas");
        // fonction pour télécharger ces données
        RequestParams params = new RequestParams();
        //envoi des parametres
        //  Log.i("NETWORK", "1_id_GET:" + userInfo.getId());
        params.put("id", getUID());

        conn = new ConnexionHandler(this);
        conn.downloadDatas(params);//launch the asynchronous Task

    }
    public final static boolean isValidEmail(CharSequence target) {
        Log.i("email", "valid");
        // like a regEx to verify e-mail with patterns
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    // communication with server for email
    public void isEmailHasBeenVerified(){
        Log.i("EMAIL","Check Activation");
        // Verifie si l'email est activé dans le serveur
        RequestParams params = new RequestParams();
        //envoi des parametres
      //  Log.i("NETWORK", "1_id_GET:" + userInfo.getId());
        params.put("id", getUID());

        conn = new ConnexionHandler(this);
        try {
            conn.verificationEmail(params);//launch the asynchronous Task
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void submitEmail(String email, String password){
        //submitEmail
        Log.i("EMAIL_submitting","trying to submit");
        // prend le contenu de la base sqlite si il n'est pas vide et l'enregistre sur le serveur
        RequestParams params = new RequestParams();
        //envoi des parametres
        //  Log.i("NETWORK", "1_id_GET:" + userInfo.getId());
        params.put("id", getUID());
        params.put("email", email);
        params.put("password", password);
        conn = new ConnexionHandler(this);
        try {
            conn.submitEmail(params);//launch the asynchronous Task
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onCommunicationSuccess(String output) {
        // end of the verification process
        Log.i("EMAIL","EMAIL HAS BEEn VERIFIED" +output);
        setContentView(R.layout.activity_accessdatas_final);
        TextView t_email = (TextView)findViewById(R.id.t_email);
        t_email.setText(t_email.getText() + " " + output);
        // set return toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onCommunicationFailure() {

    }
    private long getUID() {
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
        return userInfos.getId();
    }
}