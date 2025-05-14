package com.example.garage.adapters;

import static com.example.garage.functions.ImageUtils.getImageFromFirestore;
import static com.example.garage.functions.eventInteractions.toggleJoinEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.R;
import com.example.garage.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private final Context context;
    private final List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventTitle.setText(event.getTitle());
        holder.eventLocation.setText(event.getLocation());
        holder.eventDate.setText(event.getDate());
        holder.eventTime.setText(event.getTime());
        holder.eventParticipateButton.setOnClickListener(v -> toggleJoinEvent(event.getId(), holder.eventParticipateButton, context));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

        if (!currentUserId.equals(event.getHostId())) {
            holder.manageBtn.setVisibility(View.GONE);
        }

        db.collection("events").document(event.getId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> joinedUsers = (List<String>) documentSnapshot.get("participants");
                if (joinedUsers != null && joinedUsers.contains(currentUserId)) {
                    holder.eventParticipateButton.setText("Participating");
                    holder.eventParticipateButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.secondary));
                    holder.eventParticipateButton.setTextColor(context.getResources().getColorStateList(R.color.text));
                } else {
                    holder.eventParticipateButton.setText("Join Event");
                    holder.eventParticipateButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.primary));
                    holder.eventParticipateButton.setTextColor(context.getResources().getColorStateList(R.color.black));
                }
            }
        });

        if (event.getImageId() != null && !event.getImageId().isEmpty()) {
            loadImageFromFirestore(event.getImageId(), holder.eventPicture);
        } else {
            holder.eventPicture.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventLocation, eventDate, eventTime;
        Button eventParticipateButton;
        ImageButton manageBtn;
        ImageView eventPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventPicture = itemView.findViewById(R.id.eventImage);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventParticipateButton = itemView.findViewById(R.id.eventJoinButton);
        }
    }

    private void loadImageFromFirestore(String imageId, final ImageView imageView) {
        getImageFromFirestore(imageId)
                .addOnSuccessListener(bitmap -> {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(e -> imageView.setVisibility(View.GONE));
    }
}

