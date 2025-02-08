package com.example.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class events extends Fragment {

    TextView screenTitle;
    BottomNavigationView navbar;
    ImageButton chatBtn, backBtn, settingsBtn, addBtn;

    public events() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Events");

        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        chatBtn = getActivity().findViewById(R.id.chatBtn);
        chatBtn.setVisibility(View.GONE);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.VISIBLE);

        return view;
    }
}