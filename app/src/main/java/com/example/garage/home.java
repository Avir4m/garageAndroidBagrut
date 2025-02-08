package com.example.garage;

import static com.example.garage.functions.ImageUtils.getImageFromFirestore;
import static com.example.garage.functions.formatUtils.formatCount;
import static com.example.garage.functions.formatUtils.getTimeAgo;
import static com.example.garage.functions.postInteractions.toggleLikePost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;


public class home extends Fragment implements View.OnClickListener {

    private DocumentSnapshot lastVisible = null;

    TextView screenTitle, loadingText;
    ImageButton chatBtn, backBtn, settingsBtn, addBtn;
    BottomNavigationView navbar;
    LinearLayout postsContainer;
    ScrollView scrollView;

    public home() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lastVisible != null) {
            lastVisible = null;
            loadPosts();
        }
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

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

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
                    String author = document.getString("author");
                    String authorId = document.getString("authorId");
                    String imageId = document.getString("imageId");
                    Timestamp firestoreTimestamp = document.getTimestamp("timestamp");
                    List<String> likes = (List<String>) document.get("likes");

                    String timeAgo = "";
                    if (firestoreTimestamp != null) {
                        timeAgo = getTimeAgo(firestoreTimestamp.toDate());
                    }

                    View postView = LayoutInflater.from(getContext()).inflate(R.layout.post_item, postsContainer, false);

                    TextView titleView = postView.findViewById(R.id.postTitle);
                    TextView authorView = postView.findViewById(R.id.postAuthor);
                    TextView timestampView = postView.findViewById(R.id.postTimestamp);
                    TextView likeCount = postView.findViewById(R.id.likeCount);
                    ImageButton likeButton = postView.findViewById(R.id.likeBtn);
                    ImageView imageView = postView.findViewById(R.id.postImage);
                    LinearLayout userFrame = postView.findViewById(R.id.userFrame);

                    titleView.setText(title);
                    authorView.setText(author);
                    timestampView.setText(timeAgo);
                    likeCount.setText(formatCount(document.getLong("likeCount").longValue()));

                    if (imageId != null) {
                        getImageFromFirestore(imageId).addOnSuccessListener(bitmap -> imageView.setImageBitmap(bitmap));
                    } else {
                        imageView.setVisibility(View.GONE);
                    }

                    likeButton.setImageResource(likes != null && likes.contains(auth.getCurrentUser().getUid()) ? R.drawable.heart_filled : R.drawable.heart);
                    likeButton.setOnClickListener(v -> toggleLikePost(postId, likeButton, likeCount));

                    userFrame.setOnClickListener(v -> navigateToUserProfile(authorId));

                    postsContainer.addView(postView);
                }

                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                loadingText.setVisibility(View.GONE);

            } else {
                loadingText.setText("Error loading posts");
                loadingText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void navigateToUserProfile(String userId) {
        user userProfileFragment = new user();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        userProfileFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, userProfileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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