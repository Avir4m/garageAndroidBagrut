package com.example.garage.add;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.garage.R;

public class add extends Fragment {

    TextView screenTitle;

    public add() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Add");

        return inflater.inflate(R.layout.fragment_add, container, false);
    }
}