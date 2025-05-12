package com.example.garage;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class user extends Fragment {
    TextView screenTitle, noVehiclesText, noPostsText, displayName, vehiclesCount, followersCount;
    ImageButton settingsBtn, backBtn, addBtn;
    ImageView userImage, addUserImageBtn;
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
        addVehicleBtn.setOnClickListener(v -> {
            AddVehicleDialog addVehicleDialog = new AddVehicleDialog();
            addVehicleDialog.show(getParentFragmentManager(), "AddVehicleDialog");
        });
        profileBtns = view.findViewById(R.id.profileBtns);
        ownProfileBtns = view.findViewById(R.id.ownProfileBtns);
        backBtn = getActivity().findViewById(R.id.backBtn);
        noVehiclesText = view.findViewById(R.id.noVehiclesText);
        noPostsText = view.findViewById(R.id.noPostsText);
        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);
        displayName = view.findViewById(R.id.displayName);
        vehiclesCount = view.findViewById(R.id.vehiclesCount);
        followersCount = view.findViewById(R.id.followersCount);
        userImage = view.findViewById(R.id.userImage);
        addUserImageBtn = view.findViewById(R.id.addUserImageBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        } else {
            userId = auth.getCurrentUser().getUid();
            settingsBtn.setVisibility(View.VISIBLE);
            settingsBtn.setOnClickListener(v -> {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, new settings());
                transaction.addToBackStack(null);
                transaction.commit();
            });
            backBtn.setVisibility(View.GONE);
        }

        followBtn.setOnClickListener(v -> follow(userId));

        if (userId.equals(auth.getCurrentUser().getUid())) {
            ownProfileBtns.setVisibility(View.VISIBLE);
            profileBtns.setVisibility(View.GONE);
            addUserImageBtn.setVisibility(View.VISIBLE);

            userImage.setOnClickListener(v -> setProfilePicture());
        } else {
            ownProfileBtns.setVisibility(View.GONE);
            profileBtns.setVisibility(View.VISIBLE);
        }

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString("username");
                    screenTitle.setText(name);
                    displayName.setText(name);
                } else {
                    Toast.makeText(getActivity(), "No such user", Toast.LENGTH_SHORT).show();
                    screenTitle.setText("NoUserDocument");
                }
            } else {
                Toast.makeText(getActivity(), "Error getting user", Toast.LENGTH_SHORT).show();
                screenTitle.setText("NoUserDocument");
            }
        });

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
                }

                if (selectedTab.equals("Garage")) {
                    postsContainer.setVisibility(View.GONE);
                    garageContainer.setVisibility(View.VISIBLE);
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

    private void setProfilePicture() {
    }

    private void follow(String userId) {

    }

    private void loadGarage(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("vehicles")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    vehicleList.clear();

                    if (!queryDocumentSnapshots.isEmpty()) {
                        noVehiclesText.setVisibility(View.GONE);
                        vehiclesCount.setText(Integer.toString(queryDocumentSnapshots.size()));
                    }

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Vehicle vehicle = doc.toObject(Vehicle.class);
                        vehicleList.add(vehicle);
                    }

                    garageAdapter.notifyDataSetChanged();

                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load vehicles", Toast.LENGTH_SHORT).show());
    }


    private void loadPosts(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .whereEqualTo("authorId", userId)
                .whereEqualTo("archived", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();

                    if (!queryDocumentSnapshots.isEmpty()) {
                        noPostsText.setVisibility(View.GONE);
                    }

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String postId = doc.getId();
                        Post post = doc.toObject(Post.class);
                        post.setPostId(postId);
                        postList.add(post);
                    }

                    postAdapter.notifyDataSetChanged();

                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show());
    }
}