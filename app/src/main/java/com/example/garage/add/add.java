package com.example.garage.add;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.garage.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class add extends Fragment {

    TextView screenTitle;
    BottomNavigationView navbar;

    public add() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Add");

        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        return inflater.inflate(R.layout.fragment_add, container, false);
    }
}