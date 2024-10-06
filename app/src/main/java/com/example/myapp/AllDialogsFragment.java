package com.example.myapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AllDialogsFragment extends Fragment implements View.OnClickListener {

    Button alertBtn;
    Button customBtn;
    Button dateBtn;
    Button timeBtn;

    public AllDialogsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_dialogs, container, false);

        alertBtn = view.findViewById(R.id.alertBtn);
        customBtn = view.findViewById(R.id.customBtn);
        dateBtn = view.findViewById(R.id.dateBtn);
        timeBtn = view.findViewById(R.id.timeBtn);

        alertBtn.setOnClickListener(this);
        customBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);


        return inflater.inflate(R.layout.fragment_all_dialogs, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view == alertBtn) {
            createAlertBtn();
        }
        if (view == customBtn) {
            createCustomBtn();
        }
        if (view == dateBtn) {
            createDateBtn();
        }
        if (view == timeBtn) {
            createTimeBtn();
        }
    }

    private void createTimeBtn() {
        Calendar systemCalendar = Calendar.getInstance();
        int hour = systemCalendar.get(Calendar.HOUR);
        int minute = systemCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new SetTime(), hour, minute, true);
        timePickerDialog.show();
    }

    private void createDateBtn() {
        Calendar systemCalendar = Calendar.getInstance();
        int year = systemCalendar.get(Calendar.YEAR);
        int month = systemCalendar.get(Calendar.MONTH);
        int day = systemCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new SetDate(), year, month, day);
        datePickerDialog.show();

    }

    private void createCustomBtn() {
        Dialog dialog = new Dialog(getContext());
        dialog.setTitle("WEEEEEE!!");
        dialog.setContentView(R.layout.customdialog);
        dialog.show();
    }

    private void createAlertBtn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("select name");
        builder.setMessage("this will end the activity");
        builder.setCancelable(true);
        builder.setPositiveButton("I agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "dawdawd", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class SetTime implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            String str = "Time is: " + hourOfDay + ":" + minute;
        }
    }
}