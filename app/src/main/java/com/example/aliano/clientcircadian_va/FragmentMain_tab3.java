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
public class FragmentMain_tab3 extends Fragment { // support app v4 ...
    private static Context contextOfApplication;

    private ListView mListView; // list view for events
    int xp_bonus = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main_event, container, false);
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
                View_Event toRemove = adapter.getItem(position); // remove the event when done
                adapter.remove(toRemove);
            }
        });
        mListView.setAdapter(adapter); // put the customized adaptater

        //contextOfApplication = getActivity();//.getApplicationContext();
        return view;
    }

    public static FragmentMain_tab3 newInstance(String text) {

        FragmentMain_tab3 f = new FragmentMain_tab3();
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
        Cursor e = localBDD.getEventsDoneOrNot(false); // get all the event not done
        Log.i("event_frag","event trouves :"+e.getCount()); // events pas encore fait
        if(e.getCount()>0){

            while (e.moveToNext()) {
                String type = e.getString(1).toString();
                TEvent event = localBDD.cursorToEvent(e);
                Log.i("event_frag","pos :" +e.getPosition() +", id="+event.getId()+", type ="+type);
                switch(type){
                    case "INFO":
                        events.add(new View_Event(event.getId(), event.getType(), event.getName(), event.getDescription(), event.getValue(), R.drawable.chronotype));
                        break;
                    case "FORM": // createForm
                        events.add(new View_Event(event.getId(),event.getType(), event.getName(), event.getDescription(),event.getValue(),createForm(event.getContenu())));
                        break;
                    case "QCM":
                        String description = (event.getDescription().length()<=4) ? getString(R.string.qcm_default_description) : event.getDescription();
                        // 1 == true answer
                        events.add(new View_Event(event.getId(), event.getType(), event.getName(), description, event.getValue(), createQCM(event.getContenu())));
                        break;
                }
            }
        }
        //On ferme le cursor
        e.close();

        //int e = 5;
        // all the informations will be given by the server ( PUSH ALL THE RELATED EVENTS)
        //if (e!=0){ //if(e !=null) {
        //e.moveToFirst();
        //for (int i = 0; i < e; i++) { // number of results
        //for (int i = 0; i < e.getCount(); i++) { // number of results
       // events.add(new View_Event(1, "INFO", "Chronotype", getString(R.string.descriptionInfo_exemple), getString(R.string.contentinfo_exemple), R.drawable.chronotype));
       // events.add(new View_Event(2,"FORM","Profil", "Du matin ou du soir ? ","Etes vous plutôt nocturne ou matinal ?",testForm()));
       // events.add(new View_Event(3, "QCM", "Question du jour", "Tenez vous prêt pour la question du jour", "Qu'est ce que le chronotype ?", testQCM()));
       // events.add(new View_Event(4, "INFO", "Cortisol", "Le cortisol est une hormone stéroïde secrétée par le cortex de la glande surrénale à partir du cholestérol(...)", "Quand vous êtes face à une situation risquée, plusieurs hormones boostent votre organisme pour vous aider à surmonter le danger. La plus connue d'entre elles est l'adrénaline. Surnommée \"l'hormone guerrière\", elle mobilise toute votre énergie disponible et aiguise instantanément vos sens. Quant au cortisol, il est produit en masse quelques minutes après la poussée d'adrénaline. Ses effets sont moins perceptibles mais extrêmement importants. Il participe activement à la production d'énergie en transformant les réserves de graisse en sucres. Il dirige également cette énergie au bon endroit, comme dans les muscles de vos jambes si vous devez prendre la fuite ! Le cortisol contribue réellement à vous sauver la vie. \n" +
       //         "De plus, en dehors des pics de stress, il contribue également à maintenir l'équilibre énergétique de votre organisme, tout au long de la journée."));
        /// /events.add(new View_Event(-1*e.getInt(1), e.getString(2),""));
        //e.moveToNext();
        // }
        // }
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
        switch (current_event.getType()) {
            // une fois vue l'info va dans l'historique (bibliothèque)
            case "INFO":
                xp_provide = 100; // xp given
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
                        ((MainActivity)getActivity()).winEXPerience(xp_provide);
                        CircadianLocalBDD localBDD = new CircadianLocalBDD(contextOfApplication);
                        localBDD.open();
                        localBDD.setEventResult(current_event.getEid(), "");
                        localBDD.close();
                        return;
                    }
                });

                break;
            case "QCM": // after setting the view launching timer
                final int true_answer;
                final String true_answer_text;
                xp_provide = 200;
                xp_bonus = 0;
                viewE = factory.inflate(R.layout.event_qcm_view, null);
                final TextView timer = (TextView) viewE.findViewById(R.id.timer); // get timer
                final RadioGroup rg_qcm = (RadioGroup) viewE.findViewById(R.id.radioQCM);
                //Button validate = (Button) view.findViewById(R.id.QCMvalidate);
                //validate.setVisibility(View.VISIBLE);
                name = (TextView) viewE.findViewById(R.id.name); // name of the event
                value = (TextView) viewE.findViewById(R.id.value); // short resume if needed
                content = (TextView) viewE.findViewById(R.id.content); // content of the question
                res1 = (ImageView) viewE.findViewById(R.id.img1); // image if needeed

                name.setText(current_event.getName());
                value.setText(current_event.getValue());
                content.setText(current_event.getContent());
                res1.setImageResource(current_event.getResImg1());
                animationTimer(timer); // animer// le timer
                new CountDownTimer(45000, 1000) { // 45 s -> 0s

                    public void onTick(long millisUntilFinished) {
                        timer.setText("" + millisUntilFinished / 1000);
                        xp_bonus = (int) (millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        timer.setBackgroundColor(0xFFFF0000);
                        timer.setText("out of time");
                    }
                }.start();

                JSONArray answers = current_event.getAnswers();
                int temp_find_answer =-1;
                String temp_text_answer="";
                for (int i = 0; i < answers.length(); i++) { // par position
                //for (int i = 1; i <= answers.length(); i++) { // par position
                    JSONObject qcm_answer_i = null;
                    try {
                        qcm_answer_i = answers.getJSONObject(i);
                        //qcm_answer_i = answers.getJSONObject(i).getString();
                        //to register later in db -> qcm_answer_i.getString("id");
                        if (qcm_answer_i.getBoolean("is_true_answer")) {
                            temp_find_answer = i;
                            temp_text_answer = qcm_answer_i.getString("possible_answer");
                        } // identify the true answer
                        RadioButton rdbtn = new RadioButton(contextOfApplication);
                        rdbtn.setId(i);
                        rdbtn.setText(qcm_answer_i.getString("possible_answer")); // answer text
                        rg_qcm.addView(rdbtn);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                true_answer = temp_find_answer; // the true answer is final need to be assigned only once
                true_answer_text = temp_text_answer;
                builder.setView(viewE);

                builder.setPositiveButton(getResources().getString(R.string.validate), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//TODO: DB Question répondues +1
                        String result;
                        int xp_earned;
                        if (rg_qcm.getCheckedRadioButtonId() == true_answer) {
//TODO: DB Bonne réponse + 1
                            result ="true";
                            xp_earned = xp_bonus + xp_provide;
                            Toast.makeText(contextOfApplication, getResources().getString(R.string.goodAnswer) + " +" + xp_earned + " xp", Toast.LENGTH_SHORT).show();
                            //rg_qcm.setBackgroundColor(Color.GREEN);

                            // screen bonne réponse ! + gains
                        } else {
                            result ="false";
                            xp_earned = 10; // lot de consolation (need faire un badge)
                            Toast.makeText(contextOfApplication, getResources().getString(R.string.badAnswer) +" +" +xp_earned +" xp", Toast.LENGTH_SHORT).show();
                            // screen lot de consolation et la bonne réponse était : true_answer_text
                        }
                        CircadianLocalBDD localBDD = new CircadianLocalBDD(contextOfApplication);
                        localBDD.open();
                        localBDD.setEventResult(current_event.getEid(),result);
                        localBDD.close();
                        // screen gain et bonne réponse
                        ((MainActivity)getActivity()).winEXPerience(xp_earned);

                        return;
                    }
                });

                break;
            case "FORM":
                RadioButton rdbtn;
                xp_provide = 300;
                viewE = factory.inflate(R.layout.event_form_view, null);

                final RadioGroup rg_form = (RadioGroup) viewE.findViewById(R.id.radioQCM);
                //Button validate = (Button) view.findViewById(R.id.QCMvalidate);
                //validate.setVisibility(View.VISIBLE);
                name = (TextView) viewE.findViewById(R.id.name); // name of the event
                value = (TextView) viewE.findViewById(R.id.value); // short resume if needed
                content = (TextView) viewE.findViewById(R.id.content); // content of the question
                res1 = (ImageView) viewE.findViewById(R.id.img1); // image if needeed

                name.setText(current_event.getName());
                value.setText(current_event.getValue());
                content.setText(current_event.getContent());
                res1.setImageResource(current_event.getResImg1());


                JSONArray answers_form = current_event.getAnswers();

                for (int i = 0; i < answers_form.length(); i++) {
                    JSONObject qcm_answer_i = null;
                    try {
                        qcm_answer_i = answers_form.getJSONObject(i);
                        rdbtn = new RadioButton(contextOfApplication);
                        rdbtn.setId(i);
                        rdbtn.setText(qcm_answer_i.getString("possible_answer")); // answer text
                        rg_form.addView(rdbtn);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                builder.setView(viewE);

                builder.setPositiveButton(getResources().getString(R.string.validate), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (rg_form.getCheckedRadioButtonId() != -1) {
                            int id_form = rg_form.getCheckedRadioButtonId();
                            View radioButton = rg_form.findViewById(id_form);
                            int radioId = rg_form.indexOfChild(radioButton);
                            RadioButton btn = (RadioButton) rg_form.getChildAt(radioId);
                            String selection = (String) btn.getText();
                            Log.i("form_selection", "selected: " + selection);
                            ((MainActivity) getActivity()).winEXPerience(xp_provide);
                            // update local db
                            CircadianLocalBDD localBDD = new CircadianLocalBDD(contextOfApplication);
                            localBDD.open();
                            localBDD.setEventResult(current_event.getEid(), selection);
                            Log.i("currentEvent", "idForm" + id_form + "radioId:" + radioId);
                            if ((current_event.getEid() == 70) || (current_event.getEid() == 71)) { // version en ou fr
                                Intent gotoObtainBadge;
                                Log.i("currentEvent", "eid> " + current_event.getEid());
                                if (radioId < 2) {
                                    String badge_unlock = "badge_" + (radioId +6);
                                    Log.i("badge_unlock",badge_unlock);
                                    if (!localBDD.isBadgeUnlock(badge_unlock, contextOfApplication)) {
                                        gotoObtainBadge = new Intent(contextOfApplication, BadgeObtainActivity.class);
                                        gotoObtainBadge.putExtra("badge_string_name", badge_unlock); // circadian badge
                                        startActivity(gotoObtainBadge); // principe d'une pile : activités en ordre inversé
                                    }
                                }
                            }
                            localBDD.close();
                            return;
                        } else {
                            Toast.makeText(contextOfApplication, getResources().getString(R.string.please_select_str), Toast.LENGTH_SHORT).show();
                        }
                        //rg_form.getCheckedRadioButtonId()

                    }
                });

                break;
        }

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
    public JSONArray createQCM(String json){
        JSONArray result_json  = new JSONArray();
        JSONObject qcm;

        Random r = new Random();
        int true_answer_new_position = r.nextInt(4) + 1; ; // random int (1,2,3,4)
        Log.i("event_true_answer", "" + true_answer_new_position);

        try {
            JSONObject jsnobject = new JSONObject(json);
            String answerToXchange = jsnobject.getString(""+true_answer_new_position);
            //int positionToXchange = jsnobject.
            // la 1ere réponse envoyée est la bonne on la mélange du coups
            Log.i("json_length",""+jsnobject.length());
            // mélangeur de réponse

            qcm = new JSONObject();
            qcm.put("id", "1");
            if((true_answer_new_position !=1)) {
                qcm.put("possible_answer", answerToXchange);
                qcm.put("is_true_answer", "false");
            } else{
                qcm.put("possible_answer", jsnobject.getString("1"));
                qcm.put("is_true_answer", "true");
            }
            result_json.put(qcm); // 1st
            for(int i = 2; i<=jsnobject.length();i++){
                if(i==true_answer_new_position){
                    qcm = new JSONObject();
                    qcm.put("id", true_answer_new_position);
                    qcm.put("possible_answer", jsnobject.getString("1"));
                    qcm.put("is_true_answer", "true");
                    result_json.put(qcm);
                }else {
                    qcm = new JSONObject();
                    qcm.put("id", i);
                    qcm.put("possible_answer", jsnobject.getString("" + i));
                    qcm.put("is_true_answer", "false");
                    result_json.put(qcm);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return result_json;
    }
    public JSONArray testQCM() {// simule le json array recu du server
        JSONObject qcm_1 = new JSONObject();
        JSONObject qcm_2 = new JSONObject();
        JSONObject qcm_3 = new JSONObject();
        JSONObject qcm_4 = new JSONObject();
        try {
            qcm_1.put("id", "1");
            qcm_1.put("possible_answer", "Mesure du temps de sommeil moyen");
            qcm_1.put("is_true_answer", "false");

            qcm_2.put("id", "2");
            qcm_2.put("possible_answer", "Mesure du milieu du temps de sommeil");
            qcm_2.put("is_true_answer", "true");

            qcm_3.put("id", "3");
            qcm_3.put("possible_answer", "Mesure de la moyenne du rythme circadien individuel");
            qcm_3.put("is_true_answer", "false");

            qcm_4.put("id", "4");
            qcm_4.put("possible_answer", "Mesure du chronomètre");
            qcm_4.put("is_true_answer", "false");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(qcm_1);
        jsonArray.put(qcm_2);
        jsonArray.put(qcm_3);
        jsonArray.put(qcm_4);
        return jsonArray;
    }
    public JSONArray testForm() {// simule le json array recu du server
        JSONObject qcm_1 = new JSONObject();
        JSONObject qcm_2 = new JSONObject();
        try {
            qcm_1.put("id", "1");
            qcm_1.put("possible_answer", "Nocturne");

            qcm_2.put("id", "2");
            qcm_2.put("possible_answer", "Matinal");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(qcm_1);
        jsonArray.put(qcm_2);
        return jsonArray;
    }
    public JSONArray createForm(String json){
        JSONArray result_json  = new JSONArray();
        JSONObject qcm;

        try {
            JSONObject jsnobject = new JSONObject(json);

            Log.i("json_length", "" + jsnobject.length());

            for(int i = 1; i<=jsnobject.length();i++){
                    qcm = new JSONObject();
                    qcm.put("id", i);
                    qcm.put("possible_answer", jsnobject.getString(""+i));
                    result_json.put(qcm);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return result_json;
    }
}
