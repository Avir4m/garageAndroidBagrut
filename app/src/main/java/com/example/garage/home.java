package com.example.garage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.garage.functions.timeUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class home extends Fragment implements View.OnClickListener {

    TextView screenTitle, loadingText;
    ImageButton chatBtn, backBtn, settingsBtn;
    BottomNavigationView navbar;
    LinearLayout postsContainer;

    public home() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Home");

        chatBtn = getActivity().findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(this);
        chatBtn.setVisibility(View.VISIBLE);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

        loadingText = view.findViewById(R.id.loading_text);

        postsContainer = view.findViewById(R.id.posts_container);

        loadPosts();

        return view;
    }

    private void loadPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingText.setVisibility(View.GONE);
                if (task.getResult().isEmpty()) {
                    loadingText.setVisibility(View.VISIBLE);
                    loadingText.setText("No posts");
                } else {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String title = document.getString("title");
                        String author = document.getString("author");
                        Timestamp firestoreTimestamp = document.getTimestamp("timestamp");

                        String timeAgo = "";
                        if (firestoreTimestamp != null) {
                            timeAgo = timeUtil.getTimeAgo(firestoreTimestamp.toDate());
                        }

                        View postView = LayoutInflater.from(getContext()).inflate(R.layout.home_post_item, postsContainer, false);

                        TextView titleView = postView.findViewById(R.id.postTitle);
                        TextView authorView = postView.findViewById(R.id.postAuthor);
                        TextView timestampView = postView.findViewById(R.id.postTimestamp);

                        titleView.setText(title);
                        authorView.setText(author);
                        timestampView.setText(timeAgo);

                        postsContainer.addView(postView);
                    }
                }
            } else {
                loadingText.setText("Error loading posts.");
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == chatBtn) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, new chat());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}