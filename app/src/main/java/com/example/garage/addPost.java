package com.example.garage;

import static android.app.Activity.RESULT_OK;
import static com.example.garage.functions.ImageUtils.uploadImageToFirestore;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addPost extends Fragment {

    boolean imageSelected = false;

    Button pickImageBtn, submitBtn;
    ImageView imagePreview;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    imagePreview.setImageURI(selectedImageUri);
                    imagePreview.setVisibility(View.VISIBLE);
                    imageSelected = true;
                }
            });
    EditText titleInput;
    FirebaseAuth auth;
    TextView screenTitle;
    ImageButton backBtn, settingsBtn, addBtn;

    public addPost() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Add");

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        pickImageBtn = view.findViewById(R.id.pickImage);
        pickImageBtn.setOnClickListener(v -> openSystemImagePicker());

        submitBtn = view.findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(v -> addPostToFirestore());

        titleInput = view.findViewById(R.id.postTitleInput);
        imagePreview = view.findViewById(R.id.imagePreview);
        auth = FirebaseAuth.getInstance();


        return view;
    }

    private void openSystemImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        imagePickerLauncher.launch(intent);
    }

    private void addPostToFirestore() {
        String title = titleInput.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsCollection = db.collection("posts");

        FirebaseUser currentUser = auth.getCurrentUser();

        Map<String, Object> post = new HashMap<>();
        post.put("author", currentUser.getDisplayName());
        post.put("authorId", currentUser.getUid());
        post.put("likeCount", 0);
        post.put("likes", new ArrayList<>());
        post.put("timestamp", Timestamp.now());
        post.put("title", title);
        post.put("imageId", null);
        post.put("archived", false);

        if (imageSelected) {
            uploadImageToFirestore(((BitmapDrawable) imagePreview.getDrawable()).getBitmap()).addOnSuccessListener(docId -> {
                post.put("imageId", docId);

                postsCollection.add(post).addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Post has been uploaded successfully.", Toast.LENGTH_SHORT).show();
                    titleInput.setText("");
                    imageSelected = false;
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error uploading post.", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error uploading image.", Toast.LENGTH_SHORT).show();
            });
        } else {
            postsCollection.add(post).addOnSuccessListener(documentReference -> {
                Toast.makeText(getContext(), "Post has been uploaded successfully.", Toast.LENGTH_SHORT).show();
                titleInput.setText("");
                imageSelected = false;
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error uploading post.", Toast.LENGTH_SHORT).show();
            });
        }
    }

}
