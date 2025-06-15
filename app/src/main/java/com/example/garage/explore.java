package com.example.garage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.adapters.UserAdapter;
import com.example.garage.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class explore extends Fragment {

    private TextView screenTitle;
    private ImageButton backBtn, settingsBtn, addBtn;
    private EditText searchBar;
    private RecyclerView usersRecycler;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public explore() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        FragmentActivity activity = getActivity();

        addBtn = activity.findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

        settingsBtn = activity.findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = activity.findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

        screenTitle = activity.findViewById(R.id.screenTitle);
        screenTitle.setText("Search");

        searchBar = view.findViewById(R.id.search_bar);

        usersRecycler = view.findViewById(R.id.users_recycler);

        usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(getContext(), userList, user -> navigateToUserProfile(user.getUserId()));
        usersRecycler.setAdapter(userAdapter);

        searchBar.requestFocus();
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();
                if (!query.isEmpty()) {
                    searchUsers(query);
                } else {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    private void searchUsers(String query) {
        db.collection("users")
                .whereGreaterThanOrEqualTo("username", query)
                .whereLessThanOrEqualTo("username", query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String userId = doc.getId();
                        String username = doc.getString("username");
                        String name = doc.getString("name");
                        String image = doc.getString("profilePicture");
                        userList.add(new User(userId, username, name, image));
                    }
                    userAdapter.notifyDataSetChanged();
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