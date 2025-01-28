package com.example.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.garage.functions.timeUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class user_posts_fragment extends Fragment {

     LinearLayout postsContainer;
     TextView loadingText;

    public user_posts_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_posts_fragment, container, false);

        postsContainer = view.findViewById(R.id.posts_container);
        loadingText = view.findViewById(R.id.loading_text);

        loadPosts();

        return view;
    }

    private void loadPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadingText.setVisibility(View.GONE);
                        if (task.getResult().isEmpty()) {
                            loadingText.setVisibility(View.VISIBLE);
                            loadingText.setText("No posts");
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String authorEmail = document.getString("authorEmail");
                                if (!authorEmail.equals(auth.getCurrentUser().getEmail())) {
                                    return;
                                }
                                String title = document.getString("title");
                                Timestamp firestoreTimestamp = document.getTimestamp("timestamp");
                                String timeAgo = "";
                                if (firestoreTimestamp != null) {
                                    timeAgo = timeUtil.getTimeAgo(firestoreTimestamp.toDate());
                                }

                                View postView = LayoutInflater.from(getContext()).inflate(R.layout.user_post_item, postsContainer, false);

                                TextView postTitle = postView.findViewById(R.id.postTitle);
                                TextView postTimestamp = postView.findViewById(R.id.postTimestamp);

                                postTitle.setText(title);
                                postTimestamp.setText(timeAgo);

                                postsContainer.addView(postView);
                            }
                        }
                    } else {
                        loadingText.setText("Error loading posts.");
                    }
                });
    }

}
