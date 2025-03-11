package com.example.garage.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.garage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddEventDialog extends DialogFragment {


    private Calendar calendar;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button createButton, cancelButton;
    EditText eventTitle, eventLocation, eventDateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_event_dialog, container, false);

        calendar = Calendar.getInstance();

        createButton = view.findViewById(R.id.createButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        eventTitle = view.findViewById(R.id.eventTitle);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDateTime = view.findViewById(R.id.eventDateTime);

        cancelButton.setOnClickListener(v -> dismiss());
        createButton.setOnClickListener(v -> createEvent());
        eventDateTime.setOnClickListener(v -> showDateTimePicker());


        return view;
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    showTimePicker();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    eventDateTime.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    public void createEvent() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = timeFormat.format(calendar.getTime());

        List<String> participants = new ArrayList<>();
        participants.add(auth.getCurrentUser().getUid());

        HashMap<String, Object> event = new HashMap<>();
        event.put("hostId", auth.getCurrentUser().getUid());
        event.put("host", auth.getCurrentUser().getDisplayName());
        event.put("title", eventTitle.getText().toString());
        event.put("location", eventLocation.getText().toString());
        event.put("date", formattedDate);
        event.put("time", formattedTime);
        event.put("participantsCount", 1);
        event.put("participants", participants);


        db.collection("events").add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Event created!", Toast.LENGTH_SHORT).show();
                    eventTitle.setText("");
                    eventLocation.setText("");
                    eventDateTime.setText("");
                    dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
