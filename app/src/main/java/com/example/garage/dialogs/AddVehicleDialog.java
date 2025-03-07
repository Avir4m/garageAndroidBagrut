package com.example.garage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.garage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddVehicleDialog extends DialogFragment {

    FirebaseAuth auth;
    EditText makeInput, modelInput, yearInput;
    String[] vehicleTypes = {"Coupe", "SUV", "Convertible", "Sport", "Hatchback", "Sedan", "Motorcycle", "Other"};
    private Spinner vehicleTypeSpinner;


    public AddVehicleDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_vehicle_dialog, container, false);

        auth = FirebaseAuth.getInstance();

        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        makeInput = view.findViewById(R.id.vehicleMake);
        modelInput = view.findViewById(R.id.vehicleModel);
        yearInput = view.findViewById(R.id.vehicleYear);
        vehicleTypeSpinner = view.findViewById(R.id.vehicleTypeSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, vehicleTypes);
        vehicleTypeSpinner.setAdapter(adapter);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference garageCollection = db.collection("vehicles");
            Map<String, Object> vehicleData = new HashMap<>();
            vehicleData.put("make", makeInput.getText().toString());
            vehicleData.put("model", modelInput.getText().toString());
            vehicleData.put("year", yearInput.getText().toString());
            vehicleData.put("type", vehicleTypeSpinner.getSelectedItem().toString());
            vehicleData.put("ownerId", auth.getCurrentUser().getUid());
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