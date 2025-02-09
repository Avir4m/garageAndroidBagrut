package com.example.garage.functions;

import static com.example.garage.functions.formatUtils.formatCount;

import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class postInteractions {

    public static void toggleLikePost(String postId, ImageButton likeBtn, TextView likeCountText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DocumentReference postRef = db.collection("posts").document(postId);

        postRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> likes = (List<String>) documentSnapshot.get("likes");
                int likeCount = documentSnapshot.getLong("likeCount").intValue();
                String currentUserId = auth.getCurrentUser().getUid();
                if (likes == null) {
                    likes = new ArrayList<>();
                }
                if (likes.contains(currentUserId)) {
                    likes.remove(currentUserId);
                    postRef.update("likes", likes);
                    postRef.update("likeCount", --likeCount);
                    likeBtn.setImageResource(R.drawable.heart);
                } else {
                    likes.add(currentUserId);
                    postRef.update("likes", likes);
                    postRef.update("likeCount", ++likeCount);
                    likeBtn.setImageResource(R.drawable.heart_filled);
                }
                likeCountText.setText(formatCount(likeCount));
            }
        }).addOnFailureListener(e -> {

        });
    }

    public static void toggleSavePost(String postId, ImageButton saveBtn) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> saves = (List<String>) documentSnapshot.get("savedPosts");
                if (saves == null) {
                    saves = new ArrayList<>();
                }
                if (saves.contains(postId)) {
                    saves.remove(postId);
                    userRef.update("savedPosts", saves);
                    saveBtn.setImageResource(R.drawable.bookmark);
                } else {
                    saves.add(postId);
                    userRef.update("savedPosts", saves);
                    saveBtn.setImageResource(R.drawable.bookmark_filled);
                }
            }
        }).addOnFailureListener(e -> {

        });
    }
}
