package com.example.garage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.garage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddVehicleDialog extends DialogFragment {

    private FirebaseAuth auth;
    private EditText yearInput;
    private AutoCompleteTextView vehicleMakeAutoComplete, vehicleModelAutoComplete;
    private Spinner vehicleTypeSpinner;

    String[] vehicleTypes = {"Coupe", "SUV", "Convertible", "Sport", "Hatchback", "Sedan", "Motorcycle", "Other"};

    private Map<String, List<String>> vehicleMakesAndModels = new HashMap<>();
    private List<String> vehicleMakes = new ArrayList<>();

    private void loadVehicleMakesFromJson() {
        try {
            if (getActivity() == null) {
                return;
            }

            InputStream is = getActivity().getAssets().open("vehicles.json");
            if (is == null) {
                return;
            }

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray carsArray = new JSONArray(json);

            for (int i = 0; i < carsArray.length(); i++) {
                JSONObject carObject = carsArray.getJSONObject(i);
                String brand = carObject.getString("brand");
                vehicleMakes.add(brand);

                JSONArray modelsArray = carObject.getJSONArray("models");
                List<String> models = new ArrayList<>();
                for (int j = 0; j < modelsArray.length(); j++) {
                    models.add(modelsArray.getString(j));
                }

                vehicleMakesAndModels.put(brand, models);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateModelAutoComplete(String selectedMake) {
        if (vehicleMakesAndModels.containsKey(selectedMake)) {
            List<String> models = vehicleMakesAndModels.get(selectedMake);

            ArrayAdapter<String> vehicleModelAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, models);
            vehicleModelAutoComplete.setAdapter(vehicleModelAdapter);
            vehicleModelAutoComplete.setThreshold(1);
        }
    }

    public AddVehicleDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_vehicle_dialog, container, false);

        auth = FirebaseAuth.getInstance();

        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.addButton);

        yearInput = view.findViewById(R.id.vehicleYear);

        vehicleTypeSpinner = view.findViewById(R.id.vehicleTypeSpinner);
        ArrayAdapter<String> vehicleTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, vehicleTypes);
        vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);

        vehicleMakeAutoComplete = view.findViewById(R.id.vehicleMake);
        vehicleModelAutoComplete = view.findViewById(R.id.vehicleModel);

        loadVehicleMakesFromJson();

        ArrayAdapter<String> vehicleMakeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, vehicleMakes);
        vehicleMakeAutoComplete.setAdapter(vehicleMakeAdapter);
        vehicleMakeAutoComplete.setThreshold(1);
        vehicleMakeAutoComplete.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedMake = (String) parent.getItemAtPosition(position);
            vehicleMakeAutoComplete.setText(selectedMake);
            updateModelAutoComplete(selectedMake);
        });



        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference garageCollection = db.collection("vehicles");
            Map<String, Object> vehicleData = new HashMap<>();
            vehicleData.put("make", vehicleMakeAutoComplete.getText().toString());
            vehicleData.put("model", vehicleModelAutoComplete.getText().toString());
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

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return view;
    }
}
