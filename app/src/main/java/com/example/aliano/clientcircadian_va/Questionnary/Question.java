package com.example.aliano.clientcircadian_va.Questionnary;
import android.app.Fragment;

/**
 ** Created by alexis inspired by John on 30.06.2015.
 */
public class Question extends Fragment {

    private int numberOfJumpNext = 0;
    private int numberOfJumpPrev = 0;
    private int score = 0;
    public Question(){

    }

    public String getAnswer(){
        return "";
    }

    // The number of jump represent the next nb question that will be skipped
    public void setNumberOfJumpNextQuestion(int nb){
        this.numberOfJumpNext = nb;
    }
    public int getNumberOfJumpNextQuestion(){
        return numberOfJumpNext;
    }

    public void setNumberOfJumpPreviousQuestion(int nb){
        this.numberOfJumpPrev = nb;
    }

    public void setSelectedDate(String selectedDate) {
        //ToDo
    }
    public void setSelectedDate(String selectedDate1, String selectedDate2) {
        //ToDo
    }
    public void callSelectedDatetwoTimes(String selectedDate){
        //ToDo
    }
    public void setScore(int score){
        this.score = score;
    }
    public int getScore(){
        return this.score;
    }
    public int getNumberOfJumpPreviousQuestion(){
        return numberOfJumpPrev;
    }
}
