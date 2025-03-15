package com.example.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class search extends Fragment {

    TextView screenTitle;
    ImageButton backBtn, settingsBtn, addBtn;

    public search() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Explore");

        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}