package com.example.aliano.clientcircadian_va.Questionnary;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.aliano.clientcircadian_va.WelcomeActivity;
import com.example.aliano.clientcircadian_va.R;
//import com.google.gson.Gson;

public class FormQuestionnary  extends Fragment {
    private Activity callerActivity;
    private int numberOfQuestions;
    private int duration;
    private int currentQuestionIndex;  // from 0 to numberOfQuestions-1 determined by getFragmentManager().getBackStackEntryCount()
    private int previousQuestionIndex; // from 0 to numberOfQuestions-1
    private Button nextBtn;
    private Button prevBtn;
    private ImageButton[] dotSelector; // a dot image to visually indicate to the user on which question he currently is
    private Form form;
    private boolean isPrevButtonPressed;
    private boolean isNextButtonPressed;
    private int numberOfJumpNxt;
    private int numberOfJumpPrev;
    private int adjustCurrentQuestionIndex; // parameter used for adjusting the currentQuestion indes in case of a jump

    public FormQuestionnary(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w("FragmentFormManager", "FragmentFormManager in onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_form_manager, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        this.numberOfQuestions = bundle.getInt("numberOfQuestions");
        this.duration = bundle.getInt("duration");
        Log.w("FragmentFormManager", "numberOfQuestions = " + numberOfQuestions);
        Log.w("FragmentFormManager", "duration = " + duration);
        this.dotSelector = new ImageButton[numberOfQuestions];
        this.currentQuestionIndex = 0;
        this.previousQuestionIndex = 0;
        this.adjustCurrentQuestionIndex = 0;

        this.numberOfJumpNxt = 0;
        this.isPrevButtonPressed = false;
        this.isNextButtonPressed = false;

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callerActivity = getActivity();

        //form = new FormHorneOstbergLike(); // test

        form = ((WelcomeActivity)callerActivity).getForm();
        setIntroForm();

        callerActivity.getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(isNextButtonPressed && numberOfJumpNxt > 0){
                    // There is a jump next over 1 or more question
                    isNextButtonPressed = false;
                    previousQuestionIndex = currentQuestionIndex+numberOfJumpNxt;
                    currentQuestionIndex = callerActivity.getFragmentManager().getBackStackEntryCount()-1 + numberOfJumpNxt + adjustCurrentQuestionIndex;
                    adjustCurrentQuestionIndex += numberOfJumpNxt;
                    clearAllDotSelector();
                } else if(isPrevButtonPressed && numberOfJumpPrev > 0) {
                    adjustCurrentQuestionIndex -= numberOfJumpPrev;

                    // There is a jump Previous over 1 or more question
                    isPrevButtonPressed = false;
                    previousQuestionIndex = currentQuestionIndex;
                    currentQuestionIndex = callerActivity.getFragmentManager().getBackStackEntryCount()-1 + adjustCurrentQuestionIndex;
                    clearAllDotSelector();
                } else {
                    // No jump, just go next or previous question
                    previousQuestionIndex = currentQuestionIndex;
                    currentQuestionIndex = callerActivity.getFragmentManager().getBackStackEntryCount()-1 + adjustCurrentQuestionIndex;
                }

                // add the current question to the form if the fragment is a question, add a question
                // if fragment is the intro form, set current question to null
                if(currentQuestionIndex-1 >= 0 && currentQuestionIndex-1 < numberOfQuestions){
                    if(form.getListOfQuestions()[currentQuestionIndex-1] instanceof Question){
                        form.setCurrentQuestion((Question)form.getListOfQuestions()[currentQuestionIndex-1]);
                    }
                } else {
                    // intro form, reset everything
                    form.setCurrentQuestion(null);
                    numberOfJumpNxt = 0;
                    numberOfJumpPrev = 0;
                    currentQuestionIndex = 0;
                    previousQuestionIndex = 0;
                    adjustCurrentQuestionIndex = 0;
                }

                // Update the jump index (next+previous) update the dotSelector (graphical indicator of the current question) and update the button name (end/start of form)
                updateQuestionJumpIndex();
                updateDotSelector();
                updateButtons();
            }
        });

        TableRow navDot = (TableRow) callerActivity.findViewById(R.id.dot_question_indicator_tab);
        ImageButton but;

        TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        for(int i = 0; i < numberOfQuestions; i++){
            but = new ImageButton(callerActivity);
            but.setBackgroundResource(R.drawable.dot_nav);
            but.setLayoutParams(param);
            if(i == 0){
                but.setSelected(false);
            } else {
                but.setSelected(false);
            }
            navDot.addView(but);
            dotSelector[i] = but;
        }

        prevBtn = (Button) callerActivity.findViewById(R.id.action_previous);
        prevBtn.setText(R.string.quit_string);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeButtonPress(prevBtn.getId());
            }
        });
        nextBtn = (Button) callerActivity.findViewById(R.id.action_next);
        nextBtn.setText(R.string.start_string);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeButtonPress(nextBtn.getId());
            }
        });
    }


    private void setIntroForm() {
        Fragment fragment = new FragmentIntro();
        fragment.setArguments(getArguments());
        Bundle args = new Bundle();
        args.putInt("numberOfQuestion", numberOfQuestions);
        args.putInt("duration", duration);
        FragmentManager fragmentManager = callerActivity.getFragmentManager();
        FragmentTransaction fragmentTrans = fragmentManager.beginTransaction();

        fragmentTrans.replace(R.id.content_question_form, fragment);
        fragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTrans.commit();
        form.setCurrentQuestion(null);
    }

    private synchronized void goNextQuestion() {

        if(callerActivity.getFragmentManager().getBackStackEntryCount()-1 < numberOfQuestions){

            Fragment fragment = form.getListOfQuestions()[currentQuestionIndex];

//            form.setCurrentQuestion((Question)fragment);
            if(fragment.isDetached()){//ToDo find other way to avoid java.lang.IllegalStateException: Fragment already active by the setArgument function
                fragment.setArguments(getArguments());
            }

            FragmentManager fragmentManager = callerActivity.getFragmentManager();
            FragmentTransaction fragmentTrans = fragmentManager.beginTransaction();

            fragmentTrans.replace(R.id.content_question_form, fragment);
            fragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTrans.addToBackStack("" + currentQuestionIndex);
            fragmentTrans.commit();

            callerActivity.setTitle(getString(R.string.question_string) + " " + (currentQuestionIndex + 1));

        } else {
            // End of form thx msg and quit
            Log.w("FragmentThoroughProfile", "callerActivity.getFragmentManager().getBackStackEntryCount() = "+callerActivity.getFragmentManager().getBackStackEntryCount());
        }
    }

    private synchronized void executeButtonPress(int id){

        if(id == prevBtn.getId()) {
            executeOnBackPressed();
        } else if (id == nextBtn.getId()) {
            isNextButtonPressed = true;
            isPrevButtonPressed = false;
            if (nextBtn.getText().equals(getString(R.string.finish_string))) {
                // Send the result of the form and quit the form
                String formResult = form.getResult();
                int score = form.getScore(); // get the form score
                // connexion to send data to the server
                quitForm();
                if (callerActivity instanceof WelcomeActivity) {
                    // test intent ici
                    WelcomeActivity welc = (WelcomeActivity) callerActivity;
                    welc.setScore(score);
                    welc.showUserInfForm();
                    //welc.showProfile();
                    welc.setAnswers(formResult);
                }

            } else {
                Question currQuestion = form.getCurrentQuestion();
                if (currQuestion != null) {
                    updateQuestionJumpIndex();
                    if (numberOfJumpNxt > 0) {
                        jumpToQuestion();
                    } else {
                        goNextQuestion();
                    }
                } else {
                    goNextQuestion();
                }
            }
        }
    }


    // case when backbutton is pressed, execute exactly the same code as when the previous button is clicked !
    public void executeOnBackPressed(){
        isPrevButtonPressed = true;
        isNextButtonPressed = false;
        if(currentQuestionIndex-1 >= 0){
            Fragment fragment = form.getListOfQuestions()[currentQuestionIndex-1];
            form.setCurrentQuestion((Question) fragment);
            callerActivity.setTitle(getString(R.string.question_string) + " " + (currentQuestionIndex - 1));
            updateQuestionJumpIndex();

        }
        callerActivity.getFragmentManager().popBackStack();
    }


    // This function is used to jump over the next "numberOfJumpNxt" question
    private void jumpToQuestion() {
        // jump only if numberOfJump > 0
        if(numberOfJumpNxt > 0){
            Log.w("FragmentFormManager", "jumpToQuestion: ");
            Fragment fragQuestion = form.getListOfQuestions()[currentQuestionIndex +numberOfJumpNxt];
            if(fragQuestion instanceof Question){
                Question quesToJumpTo = (Question) fragQuestion;
                quesToJumpTo.setNumberOfJumpPreviousQuestion(numberOfJumpNxt);

                form.setCurrentQuestion(quesToJumpTo);
                if(fragQuestion.isDetached()){//ToDo find other way to avoid java.lang.IllegalStateException: Fragment already active by the setArgument function
                    fragQuestion.setArguments(getArguments());
                }

                FragmentManager fragmentManager = callerActivity.getFragmentManager();
                FragmentTransaction fragmentTrans = fragmentManager.beginTransaction();

                fragmentTrans.replace(R.id.content_question_form, fragQuestion);
                fragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTrans.addToBackStack("" + currentQuestionIndex + numberOfJumpNxt + 1);
                fragmentTrans.commit();

                callerActivity.setTitle(getString(R.string.question_string) + " " + (currentQuestionIndex + numberOfJumpNxt + 1));
            }
        }
    }

    private void quitForm() {

        FragmentManager fragMng = callerActivity.getFragmentManager();
        int limit = fragMng.getBackStackEntryCount();
        for(int i = 0; i < limit; i++){
            fragMng.popBackStack();
        }


    }

    private void updateDotSelector() {
        if(previousQuestionIndex <= numberOfQuestions && previousQuestionIndex > 0){
            dotSelector[previousQuestionIndex -1].setSelected(false);
        }
        if (currentQuestionIndex <= numberOfQuestions && currentQuestionIndex > 0) {
            dotSelector[currentQuestionIndex -1].setSelected(true);
        }
    }

    private void clearAllDotSelector(){
        for(int i  = 0; i < dotSelector.length; i++){
            dotSelector[i].setSelected(false);
        }
    }

    private void updateButtons() {
        if(currentQuestionIndex == numberOfQuestions) {
            nextBtn.setText(R.string.finish_string);
        } else if(currentQuestionIndex == 0){
            nextBtn.setText(R.string.start_string);
            prevBtn.setText(R.string.quit_string);
        } else {
            nextBtn.setText(R.string.next_string);
            prevBtn.setText(R.string.previous_string);
        }
    }

    private void updateQuestionJumpIndex() {
        Question currQuestion = form.getCurrentQuestion();
        if(currQuestion != null){
            numberOfJumpPrev = currQuestion.getNumberOfJumpPreviousQuestion();
            numberOfJumpNxt = currQuestion.getNumberOfJumpNextQuestion();
        }
    }


    /**FRAGMENT CLASS TO SET UP THE INTRO OF A FORM: The number of questions and the duration are indicated and the user is invited to do the form or quit**/
    public static class FragmentIntro extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_form_intro, container, false);
            setHasOptionsMenu(true);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle args = this.getArguments();
            int duration = args.getInt("duration");
            int numberOfQuestions = args.getInt("numberOfQuestions");


            Activity test = getActivity();
            TextView txt = null;
            if(test != null){
                txt = (TextView) test.findViewById(R.id.frag_form_intro);
            }
            if(txt != null){
                txt.setText(String.format(getString(R.string.form_intro_txt), numberOfQuestions, duration));
            }
        }
    }

}
