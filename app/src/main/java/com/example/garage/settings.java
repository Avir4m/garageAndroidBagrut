package com.example.garage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.adapters.PostAdapter;
import com.example.garage.functions.postUtils;
import com.example.garage.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class settings extends Fragment implements View.OnClickListener {

    FirebaseAuth auth;

    TextView screenTitle, signoutBtn, accountBtn, archivedBtn, savedBtn, hiddenBtn, email;
    ImageButton settingsBtn, backBtn;
    BottomNavigationView navBar;
    LinearLayout settingsView, acconntView, archivedView, savedView, hiddenView;
    RecyclerView savedRecyclerView, archivedRecyclerView, hiddenRecyclerView;

    private PostAdapter savedPostAdapter, archivedPostAdapter, hiddenPostAdapter;
    private List<Post> savedPostList = new ArrayList<>();
    private List<Post> archivedPostList = new ArrayList<>();
    private List<Post> hiddenPostList = new ArrayList<>();


    public settings() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Settings");

        auth = FirebaseAuth.getInstance();

        email = view.findViewById(R.id.email);
        email.setText(auth.getCurrentUser().getEmail());

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);

        signoutBtn = view.findViewById(R.id.signoutBtn);
        signoutBtn.setOnClickListener(this);

        navBar = getActivity().findViewById(R.id.bottomNav);
        navBar.setVisibility(View.INVISIBLE);

        settingsView = view.findViewById(R.id.settingsView);
        acconntView = view.findViewById(R.id.accountView);
        savedView = view.findViewById(R.id.savedView);
        archivedView = view.findViewById(R.id.archivedView);
        hiddenView = view.findViewById(R.id.hiddenView);

        accountBtn = view.findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(v -> {
            screenTitle.setText("Account Settings");
            settingsView.setVisibility(View.GONE);
            acconntView.setVisibility(View.VISIBLE);
        });

        savedBtn = view.findViewById(R.id.savedBtn);
        savedBtn.setOnClickListener(v -> {
            screenTitle.setText("Saved");
            settingsView.setVisibility(View.GONE);
            savedView.setVisibility(View.VISIBLE);
            postUtils.loadSavedPosts(getContext(), savedPostList, savedPostAdapter, () -> {
            });
        });

        archivedBtn = view.findViewById(R.id.archivedBtn);
        archivedBtn.setOnClickListener(v -> {
            screenTitle.setText("Archived");
            settingsView.setVisibility(View.GONE);
            archivedView.setVisibility(View.VISIBLE);
            postUtils.loadArchivedPosts(getContext(), archivedPostList, archivedPostAdapter, () -> {
            });
        });

        hiddenBtn = view.findViewById(R.id.hiddenBtn);
        hiddenBtn.setOnClickListener(v -> {
            screenTitle.setText("Hidden");
            settingsView.setVisibility(View.GONE);
            hiddenView.setVisibility(View.VISIBLE);
            postUtils.loadHiddenPosts(getContext(), hiddenPostList, hiddenPostAdapter, () -> {
            });
        });

        savedRecyclerView = view.findViewById(R.id.savedRecyclerView);
        savedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        savedPostList = new ArrayList<>();
        savedPostAdapter = new PostAdapter(getContext(), savedPostList, userId -> navigateToUserProfile(userId));
        savedRecyclerView.setAdapter(savedPostAdapter);

        archivedRecyclerView = view.findViewById(R.id.archivedRecyclerView);
        archivedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        archivedPostList = new ArrayList<>();
        archivedPostAdapter = new PostAdapter(getContext(), archivedPostList, userId -> navigateToUserProfile(userId));
        archivedRecyclerView.setAdapter(archivedPostAdapter);

        hiddenRecyclerView = view.findViewById(R.id.hiddenRecyclerView);
        hiddenRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        hiddenPostList = new ArrayList<>();
        hiddenPostAdapter = new PostAdapter(getContext(), hiddenPostList, userId -> navigateToUserProfile(userId));
        hiddenRecyclerView.setAdapter(hiddenPostAdapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == backBtn) {
            if (settingsView.getVisibility() == View.VISIBLE) {
                getParentFragmentManager().popBackStack();
            } else {
                screenTitle.setText("Settings");
                settingsView.setVisibility(View.VISIBLE);
                acconntView.setVisibility(View.GONE);
                savedView.setVisibility(View.GONE);
                archivedView.setVisibility(View.GONE);
                hiddenView.setVisibility(View.GONE);
            }
        } else if (view == signoutBtn) {
            auth.signOut();
            Intent intent = new Intent(getActivity(), WelcomeScreen.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void navigateToUserProfile(String userId) {
        user userProfileFragment = new user();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        userProfileFragment.setArguments(args);

        getParentFragmentManager().beginTransaction().replace(R.id.frame, userProfileFragment).addToBackStack(null).commit();
    }
}