package com.example.aliano.clientcircadian_va;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TBadge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Aliano on 20/05/2017.
 */
public class FragmentCollection_tab1 extends Fragment { // support app v4 ...
    private static Context contextOfApplication;
    public SQLiteDatabase mybdd; // pour faire les requetes
    TBadge tempBadge;
    WebView chartFriendChronotype;
    final String chartSocialSize = "300x128";
    long startClickTime;
    private static final int MAX_CLICK_DURATION = 100;
    String sSocial;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_collection_grid, container, false);
        super.onCreate(savedInstanceState);
        contextOfApplication = getActivity();//.getApplicationContext();
// database objects
        CircadianLocalBDD localBdd = new CircadianLocalBDD(contextOfApplication);
        localBdd.open(); // open local bdd
        mybdd = localBdd.getBDD(); // get tables to make requests

        String myQueryBase = "SELECT b.ID, b.Name, b.Description, b.IsUnlock, b.Date, b.ResNumber FROM "+CircadianLocalBDD.TABLE_BADGES +" AS b";  // PREMIERE JOINTURE
        String whereClause = " WHERE b.IsUnlock = 1;"; // AND e.name =\""+eventSelected+"\";";
        Cursor e = mybdd.rawQuery(myQueryBase+whereClause,null);
        Log.i("Query",e.getCount() +"= Number of results =");
        /*
        int numberOfBadges = localBdd.getNumberOfBadges();
        Log.v("badge_number",""+numberOfBadges);
        */
        List<Integer> mThumbIds = new ArrayList<Integer>();
        if(e.getCount() >0) {
            e.moveToFirst();
            for (int i = 0; i < e.getCount(); i++) { // number of results
                //tempBadge = localBdd.cursorToBadge(e);// new View_Match(e.getInt(2),e.getString(3),e.getString(7);
                //ImageView iv = new ImageView(this);
                //iv.setImageResource(e.getInt(5));
                mThumbIds.add(e.getInt(5));
                //iv.setImageResource(getImageResourceByName("icone"));
                /*RelativeLayout rl = (RelativeLayout) findViewById(R.id.medalsContainer);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, R.id.t_collection);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                rl.addView(iv, lp);*/
                e.moveToNext();
            }
        }
        e.close();
        // complete gridview
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(contextOfApplication,mThumbIds ));

        //for(int i=0;i<numberOfBadges;i++){
        //TempBadge = new TBadge();
        //}
        // create image , need to change that to create the obtained badges

        return view;
    }

    public static FragmentCollection_tab1 newInstance(String text) {

        FragmentCollection_tab1 f = new FragmentCollection_tab1();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

}
