package com.example.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.adapters.EventAdapter;
import com.example.garage.models.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class events extends Fragment {

    TextView screenTitle, loadingText;
    BottomNavigationView navbar;
    ImageButton backBtn, settingsBtn, addBtn;
    RecyclerView recyclerView;

    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();

    public events() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        screenTitle = getActivity().findViewById(R.id.screenTitle);
        screenTitle.setText("Events");

        navbar = getActivity().findViewById(R.id.bottomNav);
        navbar.setVisibility(View.VISIBLE);

        settingsBtn = getActivity().findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);

        addBtn = getActivity().findViewById(R.id.addBtn);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.frame, new addEvent()).addToBackStack(null).commit();
        });

        loadingText = view.findViewById(R.id.loading_text);

        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);
        recyclerView.setAdapter(eventAdapter);

        loadEvents();

        return view;
    }

    private void loadEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String postId = doc.getId();
                        Event event = doc.toObject(Event.class);
                        event.setId(postId);
                        eventList.add(event);
                    }

                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    eventAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                });

    }
}