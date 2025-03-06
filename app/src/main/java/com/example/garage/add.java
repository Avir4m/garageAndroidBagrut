package com.example.garage;

import static com.example.garage.functions.ImageUtils.uploadImageToFirestore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class add extends Fragment implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    boolean imageSelected = false;
    Button pickImageBtn, submitBtn;
    ImageView imagePreview;
    EditText titleInput;
    FirebaseAuth auth;
    TextView screenTitle;
    ImageButton chatBtn, backBtn, settingsBtn, addBtn;
    private Bitmap selectedBitmap;

    public add() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Add");

        chatBtn = getActivity().findViewById(R.id.chatBtn);
        chatBtn.setVisibility(View.GONE);

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

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
                selectedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imagePreview.setImageBitmap(selectedBitmap);
                imagePreview.setVisibility(View.VISIBLE);
                imageSelected = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
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

        if (imageSelected) {
            uploadImageToFirestore(((BitmapDrawable) imagePreview.getDrawable()).getBitmap()).addOnSuccessListener(docId ->
                    post.put("imageId", docId));
        }

        postsCollection.add(post).addOnSuccessListener(documentReference -> {
            Toast.makeText(getContext(), "Post has been uploaded successfully.", Toast.LENGTH_SHORT).show();
            titleInput.setText("");
            imageSelected = false;
        });
    }
}
