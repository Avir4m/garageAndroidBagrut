package com.example.garage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.adapters.HomePostAdapter;
import com.example.garage.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class home extends Fragment implements View.OnClickListener {

    TextView screenTitle, loadingText;
    ImageButton chatBtn, backBtn, settingsBtn, addBtn;
    BottomNavigationView navbar;
    RecyclerView recyclerView;

    private HomePostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    ;

    public home() {
    }

    @Override
    public void onResume() {
        super.onResume();
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

        recyclerView = view.findViewById(R.id.postsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new HomePostAdapter(getContext(), postList, userId -> navigateToUserProfile(userId));
        recyclerView.setAdapter(postAdapter);

        loadPosts();


        return view;
    }

    private void loadPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String postId = doc.getId();
                        Post post = doc.toObject(Post.class);
                        post.setPostId(postId);
                        postList.add(post);
                    }

                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    postAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Error getting posts: ", e);
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