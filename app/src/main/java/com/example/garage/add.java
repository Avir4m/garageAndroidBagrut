package com.example.garage;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class add extends Fragment implements View.OnClickListener {

    TextView screenTitle;
    BottomNavigationView navbar;
    Button submitBtn, pickImageBtn;
    ImageView imagePreview;

    private static final int PICK_IMAGE_REQUEST = 1;

    public add() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Add");

        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        submitBtn = view.findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(this);

        pickImageBtn = view.findViewById(R.id.pickImage);
        pickImageBtn.setOnClickListener(this);

        imagePreview = view.findViewById(R.id.imagePreview);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == pickImageBtn) {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}