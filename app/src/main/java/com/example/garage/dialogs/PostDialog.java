package com.example.garage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.garage.R;
import com.example.garage.functions.postInteractions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PostDialog extends BottomSheetDialogFragment {

    private String postId;
    private String postAuthorId;

    public PostDialog() {
    }

    public static PostDialog newInstance(String postId, String postAuthorId) {
        PostDialog dialog = new PostDialog();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        args.putString("postAuthorId", postAuthorId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
            postAuthorId = getArguments().getString("postAuthorId");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_dialog, container, false);

        Button aboutThisAccountBtn = view.findViewById(R.id.aboutThisAccountButton);
        aboutThisAccountBtn.setOnClickListener(v -> {
            AboutThisAccountDialog aboutThisAccountDialog = AboutThisAccountDialog.newInstance(postAuthorId);
            aboutThisAccountDialog.show(((FragmentActivity) requireContext()).getSupportFragmentManager(), "AboutThisAccountDialog");
        });

        Button hidePost = view.findViewById(R.id.hideButton);
        hidePost.setOnClickListener(v -> {
            postInteractions.toggleHidePost(postId);
            dismiss();
        });

        return view;
    }
}
