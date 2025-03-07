package com.example.garage.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.garage.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDialog extends DialogFragment {

    private String postId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PostDialog() {
    }

    public static PostDialog newInstance(String postId) {
        PostDialog dialog = new PostDialog();
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

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v ->
                db.collection("posts").document(postId).delete().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                ).addOnFailureListener(aVoid -> {
                    Toast.makeText(getContext(), "Error deleting post", Toast.LENGTH_SHORT).show();
                    dismiss();
                }));

        return view;
    }
}
