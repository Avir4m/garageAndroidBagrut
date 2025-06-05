package com.example.garage;

import static com.example.garage.functions.ImageUtils.getImageFromFirestore;

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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class editPost extends Fragment {

    private String postId;

    ImageView postImage;
    EditText postTitle;
    Button saveBtn;
    ImageButton backBtn, addBtn;
    TextView screenTitle;

    public editPost() {
    }

    public static editPost newInstance(String postId) {
        editPost fragment = new editPost();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);
        FragmentActivity activity = getActivity();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        screenTitle = activity.findViewById(R.id.screenTitle);
        screenTitle.setText("Edit Post");

        addBtn = activity.findViewById(R.id.addBtn);
        addBtn.setVisibility(View.GONE);

        backBtn = activity.findViewById(R.id.backBtn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        activity.findViewById(R.id.bottomNav).setVisibility(View.GONE);

        postImage = view.findViewById(R.id.postImage);
        postTitle = view.findViewById(R.id.postTitle);
        saveBtn = view.findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(v -> {
            String updatedTitle = postTitle.getText().toString();

            if (updatedTitle.isEmpty()) {
                postTitle.setError("Title cannot be empty");
                return;
            }

            db.collection("posts").document(postId)
                    .update("title", updatedTitle, "edited", true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Post updated successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update post, try again later.", Toast.LENGTH_SHORT).show();
                    });
        });

        db.collection("posts").document(postId).get().addOnSuccessListener(documentSnapshot -> {
            postTitle.setText(documentSnapshot.getString("title"));

            getImageFromFirestore(documentSnapshot.getString("imageId")).addOnSuccessListener(decodedBitmap -> {
                postImage.setImageBitmap(decodedBitmap);
            });
        });

        return view;
    }
}