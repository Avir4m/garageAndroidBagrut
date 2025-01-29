package com.example.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.garage.functions.timeUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class user_posts_fragment extends Fragment {

    private DocumentSnapshot lastVisible = null;

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

        Query query = db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) return;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String postEmail = document.getString("authorEmail");
                    if (!postEmail.equals(auth.getCurrentUser().getEmail().toString())) {
                        continue;
                    }
                    String title = document.getString("title");
                    Timestamp firestoreTimestamp = document.getTimestamp("timestamp");

                    String timeAgo = "";
                    if (firestoreTimestamp != null) {
                        timeAgo = timeUtils.getTimeAgo(firestoreTimestamp.toDate());
                    }
                    try {
                        View postView = LayoutInflater.from(getContext()).inflate(R.layout.user_post_item, postsContainer, false);

                        TextView titleView = postView.findViewById(R.id.postTitle);
                        TextView timestampView = postView.findViewById(R.id.postTimestamp);

                        titleView.setText(title);
                        timestampView.setText(timeAgo);

                        postsContainer.addView(postView);
                    } catch (Error error) {return;}
                }
                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                loadingText.setVisibility(View.GONE);
            }
        });
    }

}
