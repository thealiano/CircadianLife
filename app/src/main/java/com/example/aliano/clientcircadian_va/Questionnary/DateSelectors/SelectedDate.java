package com.example.aliano.clientcircadian_va.Questionnary.DateSelectors;

/**
 * Created by John on 13.07.2015.
 */
public class SelectedDate {
    private int hours;
    private int minutes;
    private int day;
    private int month;
    private int year;
    // 2015-07-13 10:46

    public SelectedDate(int year, int month, int day, int hours, int minutes){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
    }
    public SelectedDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = -1;
        this.minutes = -1;
    }
    public SelectedDate(int hours, int minutes){
        this.year = -1;
        this.month = -1;
        this.day = -1;
        this.hours = hours;
        this.minutes = minutes;
    }
    public SelectedDate(){
        this.year = -1;
        this.month = -1;
        this.day = -1;
        this.hours = -1;
        this.minutes = -1;
    }


    public String getSelectedDate(){
        if(year != -1 && month != -1 && day != -1){
            if(hours != -1 && minutes != -1){
                return year+"-"+month+"-"+day+" "+hours+":"+minutes+":00";
            } else {
                return year+"-"+month+"-"+day;
            }
        } else {
            if(hours != -1 && minutes != -1){
                return hours+":"+minutes+":00";
            } else {
                return "";
            }
        }
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString(){
        return "Date{year="+year+",month="+month+",day="+day+",hours="+hours+",minutes="+minutes+"}";
    }

}
