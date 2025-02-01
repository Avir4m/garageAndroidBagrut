package com.example.garage;


import static com.example.garage.functions.ImageUtils.getImageFromFirestore;
import static com.example.garage.functions.formatUtils.formatCount;
import static com.example.garage.functions.formatUtils.getTimeAgo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class user extends Fragment implements View.OnClickListener {

    private DocumentSnapshot lastVisible = null;

    FirebaseAuth auth;

    TextView screenTitle;
    ImageButton settingsBtn, backBtn;
    BottomNavigationView navbar;
    TabLayout tabLayout;
    LinearLayout postsContainer, postsContainerItems, garageContainer, garageContainerItems;
    ScrollView postsScrollView, garageScrollView;

    public user() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText(currentUser.getDisplayName().toString());

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(this);
        settingsBtn.setVisibility(View.VISIBLE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        postsContainer = view.findViewById(R.id.postsContainer);
        postsContainerItems = view.findViewById(R.id.postsContainerItems);
        garageContainer = view.findViewById(R.id.garageContainer);
        garageContainerItems = view.findViewById(R.id.garageContainerItems);

        postsScrollView = view.findViewById(R.id.postsScrollView);
        postsScrollView.setOnScrollChangeListener((scrollView, x, y, oldX, oldY) -> {
            int scrollViewHeight = scrollView.getHeight();
            int scrollY = scrollView.getScrollY();
            int contentHeight = postsContainerItems.getHeight();
            if (scrollY + scrollViewHeight == contentHeight) {
                loadPosts();
            }
        });

        garageScrollView = view.findViewById(R.id.garageScrollView);
        garageScrollView.setOnScrollChangeListener((scrollView, x, y, oldX, oldY) -> {
            int scrollViewHeight = scrollView.getHeight();
            int scrollY = scrollView.getScrollY();
            int contentHeight = garageContainerItems.getHeight();
            if (scrollY + scrollViewHeight == contentHeight) {
                loadPosts();
            }
        });

        loadPosts();
        loadGarge();

        tabLayout = view.findViewById(R.id.userTabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedTab = tab.getText().toString();

                if (selectedTab.equals("Posts")) {
                    postsContainer.setVisibility(View.VISIBLE);
                    garageContainer.setVisibility(View.GONE);
                    loadPosts();
                }

                if (selectedTab.equals("Garage")) {
                    postsContainer.setVisibility(View.GONE);
                    garageContainer.setVisibility(View.VISIBLE);
                    loadGarge();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });

        return view;
    }

    private void loadGarge() {
    }

    private void loadPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        String currentUserId = currentUser.getUid();

        Query query = db.collection("posts")
                .whereEqualTo("authorId", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    return;
                }

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String postId = document.getId();
                    String title = document.getString("title");
                    String imageId = document.getString("imageId");
                    Timestamp firestoreTimestamp = document.getTimestamp("timestamp");
                    String timeAgo = (firestoreTimestamp != null) ? getTimeAgo(firestoreTimestamp.toDate()) : "";
                    List<String> likes = (List<String>) document.get("likes");

                    try {
                        View postView = LayoutInflater.from(getContext()).inflate(R.layout.compact_post_item, postsContainer, false);

                        TextView titleView = postView.findViewById(R.id.postTitle);
                        TextView timestampView = postView.findViewById(R.id.postTimestamp);
                        TextView likeCount = postView.findViewById(R.id.likeCount);
                        ImageButton likeButton = postView.findViewById(R.id.likeBtn);
                        ImageView imageView = postView.findViewById(R.id.postImage);

                        titleView.setText(title);
                        timestampView.setText(timeAgo);
                        likeCount.setText(formatCount(document.getLong("likeCount").longValue()));

                        if (imageId != null) {
                            getImageFromFirestore(imageId).addOnSuccessListener(bitmap -> imageView.setImageBitmap(bitmap));
                        } else {
                            imageView.setVisibility(View.GONE);
                        }

                        likeButton.setImageResource(likes != null && likes.contains(auth.getCurrentUser().getUid()) ? R.drawable.heart_filled : R.drawable.heart);
                        likeButton.setOnClickListener(v -> toggleLikePost(postId, likeButton, likeCount));

                        postsContainerItems.addView(postView);
                    } catch (Exception error) {
                        return;
                    }
                }

                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
            }
        });
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


    @Override
    public void onClick(View view) {
        if (view == settingsBtn) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, new settings());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}