package com.example.aliano.clientcircadian_va;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Aliano on 20/05/2017.
 */
public class FragmentCollection_tab2 extends Fragment { // support app v4 ...
    private static Context contextOfApplication;

    private ListView mListView; // list view for events

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_collection_library, container, false);
        super.onCreate(savedInstanceState);
        contextOfApplication = getActivity();//.getApplicationContext();

        mListView = (ListView) view.findViewById(R.id.ListEvent); // get listView event

        // event part
        final List<View_Event> eventList = generateEvents();
        final EventListAdaptater adapter = new EventListAdaptater(contextOfApplication, eventList);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View_Event current_event = eventList.get(position);
                showEvent(current_event);
            }
        });
        mListView.setAdapter(adapter); // put the customized adaptater

        //contextOfApplication = getActivity();//.getApplicationContext();
        return view;
    }

    public static FragmentCollection_tab2 newInstance(String text) {

        FragmentCollection_tab2 f = new FragmentCollection_tab2();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    private List<View_Event> generateEvents() {
        List<View_Event> events = new ArrayList<View_Event>();
        // TO DO : Remettre une fois que le serveur sera en place
        //String qGetEvents =  "SELECT s.id, e.id, e.name FROM "+SportBDD.TABLE_SPORTS +" AS s INNER JOIN " +SportBDD.TABLE_EVENTS +" AS e ON s.ID = e.SID";
        //String whereClause = " WHERE s.name = \"" +sportSelected +"\" ORDER BY e.name;";
        CircadianLocalBDD localBDD = new CircadianLocalBDD(contextOfApplication);
        localBDD.open();
        Cursor e = localBDD.getEventsDoneByType("INFO"); // get all the event not done
        Log.i("collection_library","event trouves :"+e.getCount()); // events pas encore fait
        if(e.getCount()>0){

            while (e.moveToNext()) {
                String type = e.getString(1).toString();
                TEvent event = localBDD.cursorToEvent(e);
                Log.i("collection_library","pos :" +e.getPosition() +", id="+event.getId()+", type ="+type);
                events.add(new View_Event(event.getId(), event.getType(), event.getName(), event.getDescription(), event.getValue(), R.drawable.chronotype));
            }
        }
        //On ferme le cursor
        e.close();
        return events;
    }
    public void showEvent(final View_Event current_event) {
        //create the alertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(contextOfApplication);
        LayoutInflater factory = LayoutInflater.from(contextOfApplication);
        builder.setCancelable(false);
        final View viewE;
        TextView name, value, content, answer;
        ImageView res1, res2;
        final int xp_provide;
//TODO: Ajouter dans la db : Nb info consulté , Nb form rempli, Nb qcm validés, Nb qcm bonne réponse
            viewE = factory.inflate(R.layout.event_info_view, null);
            name = (TextView) viewE.findViewById(R.id.name); // get the view selected
            value = (TextView) viewE.findViewById(R.id.value); // get the view selected
            content = (TextView) viewE.findViewById(R.id.content); // get the view selected
            res1 = (ImageView) viewE.findViewById(R.id.img1);
            name.setText(current_event.getName());
            value.setText(current_event.getValue());
            content.setText(current_event.getContent());
            res1.setImageResource(current_event.getResImg1());
            // set positive button
            builder.setView(viewE);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    return;
                }
            });
        builder.setCancelable(false); // cannot go back without the cancel button
        builder.show();


    }

    public void animationTimer(View aniView) {
//        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(aniView, "alpha", 0f);
        //      fadeOut.setDuration(2000);
        ObjectAnimator mover = ObjectAnimator.ofFloat(aniView, "translationX", -500f, 0f);
        mover.setDuration(1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(aniView, "alpha", 0f, 1f);
        fadeIn.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(mover).with(fadeIn);//.after(fadeOut);
        animatorSet.start();
    }
}
