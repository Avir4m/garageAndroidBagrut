package com.example.myapp;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

public class SetDate implements DatePickerDialog.OnDateSetListener {
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear +1;
        String str = "You selected : " +dayOfMonth + "/" + monthOfYear + "/" + year;
    }
}
