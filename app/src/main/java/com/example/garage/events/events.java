package com.example.garage.events;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.garage.R;


public class events extends Fragment {

    TextView screenTitle;

    public events() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Events");

        return inflater.inflate(R.layout.fragment_events, container, false);
    }
}