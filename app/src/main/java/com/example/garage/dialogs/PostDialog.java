package com.example.garage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.garage.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDialog extends BottomSheetDialogFragment {

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

        return view;
    }
}
