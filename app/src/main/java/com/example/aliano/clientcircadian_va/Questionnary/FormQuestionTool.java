package com.example.aliano.clientcircadian_va.Questionnary;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aliano.clientcircadian_va.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;




/**
 * Created by John on 04.05.2015.
 */
public class FormQuestionTool {

    private TextView question1;
    private Spinner spinner;
    private RadioGroup radioGrp;
    private RadioButton radioYes;
    private RadioButton radioNo;
    private Activity currActivity;
    private ArrayList<String> countryList;


    public FormQuestionTool(Activity currActivity){
        this.currActivity = currActivity;
        countryList = setupCountryList();
    }

    public void setSpinnerQuestionAndAnswer(String question, List<String> listToDisplay){ // lui passer mext quest ?
        question1 = (TextView) currActivity.findViewById(R.id.question_spinner_question); // R.id.question_spinner_question
        question1.setText(question);
        spinner = (Spinner) currActivity.findViewById(R.id.question_spinner_answer);

       // ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(currActivity,
        //        answerId, android.R.layout.simple_spinner_item);
        ArrayAdapter adapter = new ArrayAdapter(
                currActivity,//this
                android.R.layout.simple_spinner_item,
                listToDisplay
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public void setYesNoQuestion(String question){
        radioGrp = (RadioGroup) currActivity.findViewById(R.id.radio_yes_no_answer);
        radioYes = (RadioButton) currActivity.findViewById(R.id.radioYes);
        radioNo = (RadioButton) currActivity.findViewById(R.id.radioNo);

        question1 = (TextView) currActivity.findViewById(R.id.textview_question_yes_no);
        question1.setText(question);
    }

    // set a simple question with a list of answer in a spinner
    public void setQuestionAndCountryAnswer(String question, ArrayList<String> answers){
        question1 = (TextView) currActivity.findViewById(R.id.question_spinner_question);
        question1.setText(question);
        spinner = (Spinner) currActivity.findViewById(R.id.question_spinner_answer);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(currActivity, android.R.layout.simple_spinner_dropdown_item, answers);

        spinner.setAdapter(categoriesAdapter);
    }
    public Spinner getSpinner() {
        return spinner;
    }

    public String getYesNoAnswer(){
        int id = radioGrp.getCheckedRadioButtonId();

        if(id == R.id.radioYes){
            return "1";
        } else if(id == R.id.radioNo){
           return "0";
        } else {
            return "";
        }
    }

    // Create the country list from the system
    private ArrayList<String> setupCountryList() {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        countries.add(currActivity.getString(R.string.please_select_str));
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        return countries;
    }

    public RadioGroup getRadioGrp(){ return radioGrp; }

    // return the key value into the string: "key": "value"
    public String getJsonString(String key, String value){
        return "\""+key+"\": \""+value+"\"";
    }

    public ArrayList<String> getCountryList() {
        return countryList;
    }

}
