package com.example.garage.functions;


import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class eventInteractions {

    public static void toggleJoinEvent(String eventId, Button joinBtn) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> joinedUsers = (List<String>) documentSnapshot.get("participants");

                if (joinedUsers != null && joinedUsers.contains(currentUserId)) {
                    eventRef.update("participantsCount", documentSnapshot.getLong("participantsCount") - 1);
                    eventRef.update("participants", FieldValue.arrayRemove(currentUserId)).addOnSuccessListener(aVoid -> joinBtn.setText("Join Event"));
                } else {
                    eventRef.update("participantsCount", documentSnapshot.getLong("participantsCount") + 1);
                    eventRef.update("participants", FieldValue.arrayUnion(currentUserId)).addOnSuccessListener(aVoid -> joinBtn.setText("Participating"));

                }
            }
        });
    }
}
