package com.example.garage.dialogs;

import static com.example.garage.functions.ImageUtils.deleteImageFromFirebase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.garage.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostAuthorDialog extends BottomSheetDialogFragment {

    private String postId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PostAuthorDialog() {
    }

    public static PostAuthorDialog newInstance(String postId) {
        PostAuthorDialog dialog = new PostAuthorDialog();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_dialog, container, false);

        Button deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            db.collection("posts").document(postId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imageId = documentSnapshot.getString("imageId");

                            db.collection("posts").document(postId).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                        dismiss();

                                        if (imageId != null) {
                                            deleteImageFromFirebase(imageId);
                                        }
                                    });
                        }
                    });
        });


        return view;
    }
}
