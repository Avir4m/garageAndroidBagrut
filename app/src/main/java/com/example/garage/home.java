package com.example.garage;

import static com.example.garage.functions.formatUtils.formatCount;
import static com.example.garage.functions.formatUtils.getTimeAgo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class home extends Fragment implements View.OnClickListener {

    private DocumentSnapshot lastVisible = null;
    private boolean isLoading = false;

    TextView screenTitle, loadingText;
    ImageButton chatBtn, backBtn, settingsBtn, likeBtn;
    BottomNavigationView navbar;
    LinearLayout postsContainer;
    ScrollView scrollView;

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

        scrollView = view.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener((scrollView, x, y, oldX, oldY) -> {
            int scrollViewHeight = scrollView.getHeight();
            int scrollY = scrollView.getScrollY();
            int contentHeight = postsContainer.getHeight();

            if (scrollY + scrollViewHeight == contentHeight) {
                loadPosts();
            }
        });

        loadPosts();

        return view;
    }

    private void loadPosts() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    loadingText.setText("No posts available");
                    return;
                }
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String postId = document.getId();
                    String title = document.getString("title");
                    String author = document.getString("author");
                    Timestamp firestoreTimestamp = document.getTimestamp("timestamp");
                    List<String> likes = (List<String>) document.get("likes");

                    String timeAgo = "";
                    if (firestoreTimestamp != null) {
                        timeAgo = getTimeAgo(firestoreTimestamp.toDate());
                    }

                    View postView = LayoutInflater.from(getContext()).inflate(R.layout.home_post_item, postsContainer, false);

                    TextView titleView = postView.findViewById(R.id.postTitle);
                    TextView authorView = postView.findViewById(R.id.postAuthor);
                    TextView timestampView = postView.findViewById(R.id.postTimestamp);
                    TextView likeCount = postView.findViewById(R.id.likeCount);
                    ImageButton likeButton = postView.findViewById(R.id.likeBtn);

                    titleView.setText(title);
                    authorView.setText(author);
                    timestampView.setText(timeAgo);
                    likeCount.setText(formatCount(document.getLong("likeCount").longValue()));

                    likeButton.setImageResource(likes != null && likes.contains(auth.getCurrentUser().getUid()) ? R.drawable.heart_filled : R.drawable.heart);
                    likeButton.setOnClickListener(v -> toggleLikePost(postId, likeButton, likeCount));

                    postsContainer.addView(postView);
                }
                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                loadingText.setVisibility(View.GONE);
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

    private void toggleLikePost(String postId, ImageButton likeBtn, TextView likeCountText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth  = FirebaseAuth.getInstance();
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
}