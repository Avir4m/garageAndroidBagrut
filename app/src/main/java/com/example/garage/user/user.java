package com.example.garage.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.garage.R;
import com.example.garage.WelcomeScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class user extends Fragment {

    FirebaseAuth auth;

    Button signoutBtn;

    TextView usernameText;

    public user() {
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        signoutBtn = view.findViewById(R.id.signoutBtn);
        usernameText = view.findViewById(R.id.usernameText);

       usernameText.setText(currentUser.getDisplayName());

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getActivity(), WelcomeScreen.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}