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
import com.example.garage.editPost;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
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
        View view = inflater.inflate(R.layout.post_dialog_author, container, false);

        Button deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> deleteBtn());

        Button archiveButton = view.findViewById(R.id.archiveButton);
        archiveButton.setOnClickListener(v -> archiveBtn());

        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> editBtn());

        db.collection("posts").document(postId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean archived = documentSnapshot.getBoolean("archived");
                if (archived != null && archived) {
                    archiveButton.setText("Unarchive");
                } else {
                    archiveButton.setText("Archive");
                }
            }
        });


        return view;
    }

    public void editBtn() {
        if (getActivity() != null) {
            editPost editFragment = editPost.newInstance(postId);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, editFragment)
                    .addToBackStack(null)
                    .commit();

            dismiss();
        }
    }



    public void archiveBtn() {
        DocumentReference docRef = db.collection("posts").document(postId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                boolean archived = Boolean.TRUE.equals(documentSnapshot.getBoolean("archived"));
                docRef.update("archived", !archived)
                        .addOnSuccessListener(aVoid -> {
                            if (archived) {
                                Toast.makeText(getContext(), "Post unarchived successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Post archived successfully", Toast.LENGTH_SHORT).show();
                            }
                            dismiss();
                        }).addOnFailureListener(e -> Toast.makeText(getContext(), "An error has occurred", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void deleteBtn() {
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
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                });
                        ;
                    }
                });
    }
}
