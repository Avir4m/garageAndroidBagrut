package com.example.garage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.garage.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AboutThisAccountDialog extends BottomSheetDialogFragment {

    private String userId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AboutThisAccountDialog() {
    }

    public static AboutThisAccountDialog newInstance(String userId) {
        AboutThisAccountDialog dialog = new AboutThisAccountDialog();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_this_account_dialog, container, false);

        TextView accountUsername = view.findViewById(R.id.username);
        TextView accountJoinedAt = view.findViewById(R.id.joinedAt);
        TextView accountVehicleCount = view.findViewById(R.id.vehicles);

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());


        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                accountUsername.setText(documentSnapshot.getString("username"));
                accountJoinedAt.setText(formatter.format(documentSnapshot.getDate("joined")));
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show());

        db.collection("vehicles")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        accountVehicleCount.setText(Integer.toString(queryDocumentSnapshots.size()));
                    }
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load vehicles", Toast.LENGTH_SHORT).show());

        return view;
    }
}
