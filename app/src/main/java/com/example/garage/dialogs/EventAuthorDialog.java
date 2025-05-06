package com.example.garage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.garage.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EventAuthorDialog extends BottomSheetDialogFragment {

    private String eventId;

    public static EventAuthorDialog newInstance(String eventId) {
        EventAuthorDialog dialog = new EventAuthorDialog();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_dialog_author, container, false);

        Button deleteBtn = view.findViewById(R.id.deleteButton);
        Button editBtn = view.findViewById(R.id.editButton);

        deleteBtn.setOnClickListener(v -> dismiss());

        editBtn.setOnClickListener(v -> dismiss());

        return view;
    }
}
