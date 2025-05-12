package com.example.garage;

import static com.example.garage.functions.formatUtils.formatCount;
import static com.example.garage.functions.formatUtils.getTimeAgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class post {

    private String postId;

    TextView postTitle, likeCount, postAuthor, postTimestamp;
    ImageView postImage;
    ImageButton saveBtn, likeBtn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.post_item, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference postRef = db.collection("posts").document(postId);
        postRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            postTitle.setText(document.getString("title"));
            postAuthor.setText(document.getString("author"));
            likeCount.setText(String.valueOf(formatCount(document.getLong("likeCount"))));
            postTimestamp.setText(getTimeAgo(document.getDate("timestamp")));

        });

        return view;
    }
}
