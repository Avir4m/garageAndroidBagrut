package com.example.garage.home;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garage.R;
import com.example.garage.chat.chat;
import com.example.garage.settings.settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class home extends Fragment implements View.OnClickListener {

    TextView screenTitle;
    ImageButton chatBtn, backBtn, settingsBtn;
    BottomNavigationView navbar;

    public home() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Home");

        chatBtn = getActivity().findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(this);
        chatBtn.setVisibility(View.VISIBLE);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view == chatBtn) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.relative, new chat());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}