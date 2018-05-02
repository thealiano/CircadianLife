package com.example.aliano.clientcircadian_va.Questionnary.DateSelectors;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.TextView;

import com.example.aliano.clientcircadian_va.Questionnary.Question;

import java.util.Calendar;


/**
 * Created by John on 14.07.2015.
 */
public class DateSelectorPicker {
    private static SelectedDate selectedDate;
    private static Activity currentActivity;
    private static Question question;
    private int numberOfCall;
    private static int viewID;


    public DateSelectorPicker(Activity act, Question question, int numberOfCall){
        this.currentActivity = act;
        this.question = question;
        this.numberOfCall = numberOfCall;
        Bundle args = new Bundle();
        args.putInt("numberOfCall",numberOfCall);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(currentActivity.getFragmentManager(), "datePicker");
    }
    public DateSelectorPicker(SelectedDate selectedDate, Activity act, int viewID){
        this.selectedDate = selectedDate;
        currentActivity = act;
        this.viewID = viewID;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(currentActivity.getFragmentManager(), "datePicker");
    }

    public DateSelectorPicker(Activity act, int viewID){
        currentActivity = act;
        this.viewID = viewID;
        this.selectedDate = new SelectedDate();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(currentActivity.getFragmentManager(), "datePicker");
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        private int numberOfCall = 1;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            Bundle b = getArguments();
            if(b != null){
                numberOfCall = b.getInt("numberOfCall");
            }

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) { // month from 0 to 11
            selectedDate = new SelectedDate(year, monthOfYear+1, dayOfMonth);
            TextView selectedDateView = (TextView) currentActivity.findViewById(viewID);
            if(selectedDateView != null){
                selectedDateView.setText(selectedDate.getYear()+"-"+selectedDate.getMonth()+"-"+selectedDate.getDay());
            }
            if(question != null){
                if(numberOfCall == 1){
                    question.setSelectedDate(selectedDate.getYear()+"-"+selectedDate.getMonth()+"-"+selectedDate.getDay());
                    question = null; // Just in case user come back to a previous question, that do not use this specific case, once we have finiish getting the hour set question to null
                } else if(numberOfCall > 1){
                    question.callSelectedDatetwoTimes(selectedDate.getYear()+"-"+selectedDate.getMonth()+"-"+selectedDate.getDay());
                }
            }
        }
    }

    public static SelectedDate getSelectedDate() {
        return selectedDate;
    }

}
