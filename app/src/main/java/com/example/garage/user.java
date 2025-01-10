package com.example.garage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class user extends Fragment implements View.OnClickListener {

    FirebaseAuth auth;

    TextView screenTitle;
    ImageButton settingsBtn, backBtn;
    BottomNavigationView navbar;
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

        return view;
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