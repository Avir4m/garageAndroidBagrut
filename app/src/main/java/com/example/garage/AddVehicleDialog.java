package com.example.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddVehicleDialog extends DialogFragment {

    FirebaseAuth auth;

    public AddVehicleDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_vehicle_dialog, container, false);

        auth = FirebaseAuth.getInstance();

        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference garageCollection = db.collection("garage");
            Map<String, Object> vehicleData = new HashMap<>();
            vehicleData.put("make", "Mazda");
            vehicleData.put("model", "Mazda 3");
            vehicleData.put("year", 2024);
            vehicleData.put("userId", auth.getCurrentUser().getUid());
            garageCollection.add(vehicleData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(v.getContext(), "Vehicle added successfully!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return view;
    }

}