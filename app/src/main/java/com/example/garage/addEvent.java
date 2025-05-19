package com.example.garage;

import static android.app.Activity.RESULT_OK;
import static com.example.garage.functions.ImageUtils.uploadImageToFirestore;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class addEvent extends Fragment {

    boolean imageSelected = false;

    private Calendar calendar;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button createButton, pickImageBtn;
    ImageButton addBtn, backBtn;
    EditText eventTitle, eventLocation;
    TextView eventDateTime, screenTitle;
    ImageView imagePreview;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    imagePreview.setImageURI(selectedImageUri);
                    imagePreview.setVisibility(View.VISIBLE);
                    imageSelected = true;
                }
            });

    public addEvent() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Add Event");

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        calendar = Calendar.getInstance();

        createButton = view.findViewById(R.id.createButton);
        eventTitle = view.findViewById(R.id.eventTitle);
        imagePreview = view.findViewById(R.id.imagePreview);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDateTime = view.findViewById(R.id.eventDateTime);
        pickImageBtn = view.findViewById(R.id.pickImage);

        createButton.setOnClickListener(v -> createEvent());
        eventDateTime.setOnClickListener(v -> showDateTimePicker());
        pickImageBtn.setOnClickListener(v -> openSystemImagePicker());

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

    private void openSystemImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            Intent chooser = Intent.createChooser(galleryIntent, "Select or take a new picture");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
            imagePickerLauncher.launch(chooser);
        } else {
            imagePickerLauncher.launch(galleryIntent);
        }
    }

    public void createEvent() {
        CollectionReference eventsCollection = db.collection("events");

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
        event.put("timestamp", Timestamp.now());

        if (imageSelected) {
            uploadImageToFirestore(((BitmapDrawable) imagePreview.getDrawable()).getBitmap()).addOnSuccessListener(docId -> {
                event.put("imageId", docId);

                eventsCollection.add(event).addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Event has been uploaded successfully.", Toast.LENGTH_SHORT).show();
                    eventTitle.setText("");
                    eventLocation.setText("");
                    eventDateTime.setText("");
                    imageSelected = false;
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error uploading event.", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error uploading image.", Toast.LENGTH_SHORT).show();
            });
        } else {
            eventsCollection.add(event).addOnSuccessListener(documentReference -> {
                Toast.makeText(getContext(), "Event has been uploaded successfully.", Toast.LENGTH_SHORT).show();
                eventTitle.setText("");
                eventLocation.setText("");
                eventDateTime.setText("");
                imageSelected = false;
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error uploading event.", Toast.LENGTH_SHORT).show();
            });
        }
    }
}