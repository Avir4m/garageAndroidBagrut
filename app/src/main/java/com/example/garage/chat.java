package com.example.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class chat extends Fragment implements View.OnClickListener {

    BottomNavigationView navbar;
    TextView screenTitle;
    ImageButton chatBtn, backBtn, settingsBtn, addBtn;

    public chat() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.GONE);

        chatBtn = getActivity().findViewById(R.id.chatBtn);
        chatBtn.setVisibility(View.GONE);

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Chats");

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view == backBtn) {
            getParentFragmentManager().popBackStack();
        }
    }
}