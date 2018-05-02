package com.example.aliano.clientcircadian_va.Questionnary.DateSelectors;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.aliano.clientcircadian_va.Questionnary.Question;


/**
 * Created by John on 14.07.2015.
 */
public class TimeSelectorPicker {
    private static SelectedDate selectedDate;
    private static Question question;
    private static Activity currentActivity;
    private DialogFragment newFragment;
    private static int viewID;

    public TimeSelectorPicker(Activity act, Question question, int numberOfCall){
        this.question = question;
        currentActivity = act;
        Bundle args = new Bundle();
        args.putInt("numberOfCall",numberOfCall);
        newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(act.getFragmentManager(), "timePicker");
    }

    public TimeSelectorPicker(SelectedDate selectedDate, Activity act, int viewID){
        this.selectedDate = selectedDate;
        currentActivity = act;
        this.viewID = viewID;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(act.getFragmentManager(), "timePicker");
    }

    public TimeSelectorPicker(Activity act, int viewID){
        currentActivity = act;
        this.viewID = viewID;
        this.selectedDate = new SelectedDate();
        newFragment = new TimePickerFragment();
        newFragment.show(act.getFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private int numberOfCall = 1;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            Bundle b = getArguments();
            if(b != null){
                numberOfCall = b.getInt("numberOfCall");
            }

            int hour = 0;
            int minute = 0;

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            selectedDate = new SelectedDate(hourOfDay, minute);
            TextView selectedDateView = (TextView) currentActivity.findViewById(viewID);
            if(selectedDateView != null){
                selectedDateView.setText(selectedDate.getHours()+":"+selectedDate.getMinutes());

            }
            if(question != null){
                if(numberOfCall == 1){
                    question.setSelectedDate(selectedDate.getHours()+":"+selectedDate.getMinutes());
                    question = null; // Just in case user come back to a previous question, that do not use this specific case, once we have finiish getting the hour set question to null
                } else if(numberOfCall > 1){
                    question.callSelectedDatetwoTimes(selectedDate.getHours()+":"+selectedDate.getMinutes());
                }
            }
        }
    }

    public DialogFragment getPickerDialogFragment(){
        return newFragment;
    }
    public static SelectedDate getSelectedDate() {
        return selectedDate;
    }
}
