package com.example.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.adapters.PostAdapter;
import com.example.garage.functions.postUtils;
import com.example.garage.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class home extends Fragment {

    TextView screenTitle, loadingText;
    ImageButton backBtn, settingsBtn, addBtn;
    BottomNavigationView navbar;
    RecyclerView recyclerView;

    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

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

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.frame, new addPost()).addToBackStack(null).commit();
        });

        loadingText = view.findViewById(R.id.loading_text);

        recyclerView = view.findViewById(R.id.postsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList, userId -> navigateToUserProfile(userId));
        recyclerView.setAdapter(postAdapter);

        loadPosts();


        return view;
    }

    private void loadPosts() {
        postUtils.loadHomePosts(getContext(), postList, postAdapter, () -> {
            loadingText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }


    private void navigateToUserProfile(String userId) {
        user userProfileFragment = new user();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        userProfileFragment.setArguments(args);

        getParentFragmentManager().beginTransaction().replace(R.id.frame, userProfileFragment).addToBackStack(null).commit();
    }
}