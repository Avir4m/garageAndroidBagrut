package com.example.garage;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.adapters.CompactPostAdapter;
import com.example.garage.adapters.VehicleAdapter;
import com.example.garage.dialogs.AddVehicleDialog;
import com.example.garage.models.Post;
import com.example.garage.models.Vehicle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class user extends Fragment implements View.OnClickListener {

    FirebaseAuth auth;
    TextView screenTitle;
    ImageButton settingsBtn, backBtn, chatBtn, addBtn;
    BottomNavigationView navbar;
    TabLayout tabLayout;
    LinearLayout postsContainer, garageContainer, ownProfileBtns, profileBtns;
    Button editProfileBtn, followBtn, addVehicleBtn, messageBtn;

    private String userId;

    private List<Vehicle> vehicleList;
    private VehicleAdapter garageAdapter;
    private CompactPostAdapter postAdapter;
    private List<Post> postList;


    public user() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        screenTitle = getActivity().findViewById(R.id.screenTitle);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);

        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        followBtn = view.findViewById(R.id.followBtn);
        messageBtn = view.findViewById(R.id.messageBtn);

        addVehicleBtn = view.findViewById(R.id.addVehicleBtn);
        addVehicleBtn.setOnClickListener(this);

        profileBtns = view.findViewById(R.id.profileBtns);
        ownProfileBtns = view.findViewById(R.id.ownProfileBtns);

        backBtn = getActivity().findViewById(R.id.backBtn);


        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(this);
        } else {
            userId = auth.getCurrentUser().getUid();
            settingsBtn.setOnClickListener(this);
            settingsBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.GONE);
        }

        if (userId.equals(auth.getCurrentUser().getUid())) {
            ownProfileBtns.setVisibility(View.VISIBLE);
            profileBtns.setVisibility(View.GONE);
        } else {
            ownProfileBtns.setVisibility(View.GONE);
            profileBtns.setVisibility(View.VISIBLE);
        }

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    screenTitle.setText(document.getString("name"));

                } else {
                    Toast.makeText(getActivity(), "No such user", Toast.LENGTH_SHORT).show();
                    screenTitle.setText("NoUserDocument");
                    Log.d("Firestore", "No such user document");
                }
            } else {
                Toast.makeText(getActivity(), "Error getting user", Toast.LENGTH_SHORT).show();
                screenTitle.setText("NoUserDocument");
                Log.d("Firestore", "Error getting user document: ", task.getException());
            }
        });

        chatBtn = getActivity().findViewById(R.id.chatBtn);
        chatBtn.setVisibility(View.GONE);

        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        postsContainer = view.findViewById(R.id.postsContainer);
        garageContainer = view.findViewById(R.id.garageContainer);

        RecyclerView garageRecyclerView = view.findViewById(R.id.garageRecyclerView);
        garageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        vehicleList = new ArrayList<>();
        garageAdapter = new VehicleAdapter(getContext(), vehicleList);
        garageRecyclerView.setAdapter(garageAdapter);

        loadGarage(userId);

        RecyclerView postRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new CompactPostAdapter(getContext(), postList);
        postRecyclerView.setAdapter(postAdapter);

        loadPosts(userId);


        tabLayout = view.findViewById(R.id.userTabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedTab = tab.getText().toString();

                if (selectedTab.equals("Posts")) {
                    postsContainer.setVisibility(View.VISIBLE);
                    garageContainer.setVisibility(View.GONE);
                    loadPosts(userId);
                }

                if (selectedTab.equals("Garage")) {
                    postsContainer.setVisibility(View.GONE);
                    garageContainer.setVisibility(View.VISIBLE);
                    loadGarage(userId);
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

    private void loadGarage(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("vehicles")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    vehicleList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Vehicle vehicle = doc.toObject(Vehicle.class);
                        vehicleList.add(vehicle);
                    }

                    garageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load vehicles", Toast.LENGTH_SHORT).show()
                );
    }


    private void loadPosts(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .whereEqualTo("authorId", userId)
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

                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Error getting posts: ", e);
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
        if (view == backBtn) {
            getParentFragmentManager().popBackStack();
        }
        if (view == addVehicleBtn) {
            AddVehicleDialog addVehicleDialog = new AddVehicleDialog();
            addVehicleDialog.show(getParentFragmentManager(), "AddVehicleDialog");
        }
    }
}