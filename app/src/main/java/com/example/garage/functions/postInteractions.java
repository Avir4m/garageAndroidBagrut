package com.example.garage.functions;

import static com.example.garage.functions.formatUtils.formatCount;

import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class postInteractions {

    public static void toggleLikePost(String postId, ImageButton likeBtn, TextView likeCountText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DocumentReference postRef = db.collection("posts").document(postId);
        String currentUserId = auth.getCurrentUser().getUid();

        postRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> likes = (List<String>) documentSnapshot.get("likes");
                int likeCount = documentSnapshot.getLong("likeCount").intValue();
                if (likes == null) likes = new ArrayList<>();

                boolean liked = likes.contains(currentUserId);
                WriteBatch batch = db.batch();

                if (liked) {
                    likes.remove(currentUserId);
                    likeCount--;
                    likeBtn.setImageResource(R.drawable.heart);
                } else {
                    likes.add(currentUserId);
                    likeCount++;
                    likeBtn.setImageResource(R.drawable.heart_filled);
                }

                batch.update(postRef, "likes", likes);
                batch.update(postRef, "likeCount", likeCount);
                batch.commit();

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
                if (saves == null) saves = new ArrayList<>();

                boolean saved = saves.contains(postId);
                WriteBatch batch = db.batch();

                if (saved) {
                    saves.remove(postId);
                    saveBtn.setImageResource(R.drawable.bookmark);
                } else {
                    saves.add(postId);
                    saveBtn.setImageResource(R.drawable.bookmark_filled);
                }

                batch.update(userRef, "savedPosts", saves);
                batch.commit();
            }
        }).addOnFailureListener(e -> {
        });
    }
}
