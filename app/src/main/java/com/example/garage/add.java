package com.example.garage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class add extends Fragment implements View.OnClickListener {

    BottomNavigationView navbar;
    Button pickImageBtn, submitBtn;
    ImageView imagePreview;
    EditText titleInput;
    FirebaseAuth auth;

    private static final int PICK_IMAGE_REQUEST = 1;

    public add() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        pickImageBtn = view.findViewById(R.id.pickImage);
        pickImageBtn.setOnClickListener(this);

        submitBtn = view.findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(this);

        titleInput = view.findViewById(R.id.postTitleInput);

        imagePreview = view.findViewById(R.id.imagePreview);

        auth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == pickImageBtn) {
            openImagePicker();
        }
        if (view == submitBtn) {
            addPostToFirestore();
        }
    }

    private void addPostToFirestore() {
        String title = titleInput.getText().toString();

        if (title.isEmpty() ) {
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsCollection = db.collection("posts");

        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("author", auth.getCurrentUser().getDisplayName());
        post.put("authorEmail", auth.getCurrentUser().getEmail());
        post.put("timestamp", new Date());

        postsCollection.add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Post added successfully", Toast.LENGTH_SHORT).show();
                    titleInput.setText("");
                    imagePreview.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to add post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(e.getMessage());
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
