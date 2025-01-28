package com.example.garage;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class settings extends Fragment implements View.OnClickListener {

    FirebaseAuth auth;

    TextView userEmail;
    ImageButton settingsBtn, backBtn;
    BottomNavigationView navBar;
    Button signoutBtn;

    public settings() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        auth = FirebaseAuth.getInstance();

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);

        signoutBtn = view.findViewById(R.id.signoutBtn);
        signoutBtn.setOnClickListener(this);

        navBar = getActivity().findViewById(R.id.bottomNav);
        navBar.setVisibility(View.INVISIBLE);

        userEmail = view.findViewById(R.id.userEmail);
        userEmail.setText(auth.getCurrentUser().getEmail());

        return view;
    }

    @Override
    public void onClick(View view) {

        if (view == backBtn) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, new user());
            transaction.addToBackStack(null);
            transaction.commit();
        }

        else if (view == signoutBtn) {
            auth.signOut();
            Intent intent = new Intent(getActivity(), WelcomeScreen.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}