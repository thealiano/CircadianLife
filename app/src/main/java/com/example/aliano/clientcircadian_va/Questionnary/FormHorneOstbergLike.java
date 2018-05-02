package com.example.aliano.clientcircadian_va.Questionnary;

/**
 *  by Aliano on 25/05/2016.
 */

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.aliano.clientcircadian_va.Config.ConfigurationParameters;
import com.example.aliano.clientcircadian_va.Questionnary.DateSelectors.TimeSelectorPicker;
import com.example.aliano.clientcircadian_va.R;

/**
 * Created by John on 04.05.2015.
 */
public class FormHorneOstbergLike extends Form{

    private static final int FORM_ID = ConfigurationParameters.MCTQ_LIKE_ID;
    private static final int NUMBER_OF_QUESTIONS = 8;
    private static final int DURATION = ConfigurationParameters.MCTQ_LIKE_FORM_DURATION;
    public FormHorneOstbergLike() {
        super(NUMBER_OF_QUESTIONS, DURATION);

        createForm();
    }

    @Override
    public void createForm() {
        Fragment[] questionList = new Fragment[NUMBER_OF_QUESTIONS];
        questionList[0] = newInstance(R.string.question_HO1,R.array.question_HO1_answer);
        questionList[1] = newInstance(R.string.question_HO2,R.array.question_HO2_answer);
        questionList[2] = newInstance(R.string.question_HO3,R.array.question_HO3_answer);
        questionList[3] = newInstance(R.string.question_HO4,R.array.question_HO4_answer);
        questionList[4] = newInstance(R.string.question_HO5,R.array.question_HO5_answer);
        questionList[5] = newInstance(R.string.question_HO6,R.array.question_HO6_answer);
        questionList[6] = newInstance(R.string.question_HO7,R.array.question_HO7_answer);
        questionList[7] = newInstance(R.string.question_HO8,R.array.question_HO8_answer);

        // ajouter fragment résultat
        setListOfQuestions(questionList);
    }


    @Override
    public int getFormID() {
        return FORM_ID;
    }

    public static FragmentQuestionHO newInstance(int question, int answer) {
        FragmentQuestionHO fragment = new FragmentQuestionHO();
        Bundle args = new Bundle();
        args.putInt("question", question);
        args.putInt("answer", answer);
        fragment.setArguments(args);
        return fragment;
    }
    public static class FragmentQuestionHO extends Question {
        private FormQuestionTool tool;
        Spinner spinner;
        Bundle args;
        public FragmentQuestionHO() { // constructeur
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.question_spinner, container, false);
            setHasOptionsMenu(true);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Activity act = getActivity();
            Bundle args = getArguments(); // retrieve the bundle

            int question = args.getInt("question");
            int answer = args.getInt("answer");;

            List<String> answers = Arrays.asList(getResources().getStringArray(answer));
            tool = new FormQuestionTool(act);
            tool.setSpinnerQuestionAndAnswer(getString(question),answers);
            spinner = tool.getSpinner(); // get the spinner (list)
//TODO: A TESTER
            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>(getActivity().getApplication().getApplicationContext(), R.layout.spinner_custom,answers);
            spinner.setAdapter(adapter); // pour changer la couleur de la liste déroulante
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected (AdapterView<?> arg0, View v, int pos, long row) {
                    setScore(pos);
                }

                @Override
                public void onNothingSelected (AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }

        @Override
        public String getAnswer() {
/*
            SelectedDate date = datePicker.getSelectedDate();
            Log.w("FormHorneOstbergLike", "############ responce = " + this.birthday);
            if(tool != null && birthday != null){
                return tool.getJsonString("birthday", birthday.getYear()+"-"+birthday.getMonth()+"-"+birthday.getDay());
            } else {
                return "\"birthday\": \"\"";
            }*/
            return tool.getJsonString("Score", ""+this.getScore());
        }

    }


}
