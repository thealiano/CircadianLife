package com.example.aliano.clientcircadian_va.Questionnary;

import android.app.Fragment;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by John on 30.04.2015.
 */
public abstract class Form {

    private int numberOfQuestion;
    private Fragment[] questionList;
    private int duration;
    private int numberOfJumpNext;
    private int numberOfJumpPrevious;
    private Question currentQuestion;
    int score;
    public Form(int numberOfQuestions, int duration){
        this.numberOfQuestion = numberOfQuestions;
        this.duration = duration;
    }

    public int getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public int getDuration() {
        return duration;
    }

    public Fragment[] getListOfQuestions(){
        return questionList;
    }

    public void setListOfQuestions(Fragment[] list){
        questionList = list;
    }

    public abstract void createForm();
//    public abstract String getResult();

    public String getResult() {

        String res = "{\"command\":\"sendForm\",";
        for(Fragment f : questionList){
            if(f instanceof Question){
                Question q = (Question) f;
                res += q.getAnswer()+",";
            }
        }

        // Add formId
        res += "\"form_id\":\""+this.getFormID()+"\",";

// Add the timeStamp to the json string
        Calendar cal = Calendar.getInstance();

//Get the timezone format: Continental/country GMT+01:00
        TimeZone tz = TimeZone.getDefault();
        String timezone = tz.getID()+" "+TimeZone.getTimeZone(tz.getID()).getDisplayName(false, TimeZone.SHORT);

        res += "\"timestamp\": \""+cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+
                " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"\", \"timezone\": \""+timezone+"\"";
        return res+"}";
    }
    public int getScore(){
        int score = 0;
        for(Fragment f : questionList){
            if(f instanceof Question){
                Question q = (Question) f;
                score += q.getScore();
            }
        }
        return score;
    }

    public abstract int getFormID();
    public int getNumberJumpOverNextQuestion(){
        return numberOfJumpNext;
    }
    public int getNumberJumpOverPreviousQuestion(){
        return numberOfJumpPrevious;
    }

    public void setNumberOfJumpNext(int numberOfJumpNext){
        this.numberOfJumpNext = numberOfJumpNext;
    }
    public void setNumberOfJumpPrevious(int numberOfJumpPrevious){
        this.numberOfJumpPrevious = numberOfJumpPrevious;
    }

    public void setCurrentQuestion(Question question){
        currentQuestion = question;
    }

    public Question getCurrentQuestion(){
        return currentQuestion;
    }
}
