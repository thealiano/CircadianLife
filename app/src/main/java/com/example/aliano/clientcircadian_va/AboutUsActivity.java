package com.example.aliano.clientcircadian_va;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TUserInfo;

/**
 * Created by Aliano on 06/07/2017.
 */
public class AboutUsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        long uid = getUserId();

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_aboutus);
        TextView tversion = (TextView)findViewById(R.id.version) ;
        String textversion = tversion.getText()+" id : "+uid;
        tversion.setText(textversion);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    // to go home on item <- selected on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
// to go home on back button
    @Override
    public void onBackPressed() {
        finish();
    }
    private long getUserId() {
        CircadianLocalBDD localBdd = new CircadianLocalBDD(this);
        localBdd.open(); //On ouvre la base de données pour écrire dedans
//to do : change the id , had to come from server for account identification        //Get basic informations
        //TUserInfo userInfos = localBdd.getUserInfoWithId(0);
        TUserInfo userInfos = localBdd.getUserInfos();
        localBdd.close(); //On ferme la base de données

        if (userInfos != null){
            Log.v("usrInf_String2", userInfos.toString());
            return userInfos.getId();}
        else {
            Log.e("LOCALBDD", "ERROR RETRIVING USERINFORMATIONS");
            return -1;
        }
    }
}

