package com.example.garage.adapters;

import static com.example.garage.functions.ImageUtils.getImageFromFirestore;
import static com.example.garage.functions.formatUtils.formatCount;
import static com.example.garage.functions.formatUtils.getTimeAgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.R;
import com.example.garage.dialogs.PostAuthorDialog;
import com.example.garage.dialogs.PostDialog;
import com.example.garage.functions.postInteractions;
import com.example.garage.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.PostViewHolder> {
    private final Context context;
    private final List<Post> posts;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(String userId);
    }

    public HomePostAdapter(Context context, List<Post> posts, OnUserClickListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        holder.postTitle.setText(post.getTitle());
        Date timestamp = post.getTimestamp().toDate();
        holder.timestamp.setText(getTimeAgo(timestamp));
        holder.likeCount.setText(String.valueOf(formatCount(post.getLikeCount())));
        holder.postAuthor.setText(post.getAuthor());

        holder.postAuthor.setOnClickListener(v -> listener.onUserClick(post.getAuthorId()));
        holder.likeButton.setOnClickListener(v -> postInteractions.toggleLikePost(post.getPostId(), holder.likeButton, holder.likeCount));
        holder.saveButton.setOnClickListener(v -> postInteractions.toggleSavePost(post.getPostId(), holder.saveButton));

        holder.dotsButton.setOnClickListener(v -> {
            if (Objects.equals(post.getAuthorId(), currentUserId)) {
                PostAuthorDialog postAuthorDialog = PostAuthorDialog.newInstance(post.getPostId());
                postAuthorDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "PostAuthorDialog");
            } else {
                PostDialog postDialog = PostDialog.newInstance(post.getPostId(), post.getAuthorId());
                postDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "PostDialog");
            }
        });


        if (post.getLikes() != null && post.getLikes().contains(currentUserId)) {
            holder.likeButton.setImageResource(R.drawable.heart_filled);
        } else {
            holder.likeButton.setImageResource(R.drawable.heart);
        }

        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> savedPosts = (List<String>) documentSnapshot.get("savedPosts");
                        if (savedPosts != null && savedPosts.contains(post.getPostId())) {
                            holder.saveButton.setImageResource(R.drawable.bookmark_filled);
                        } else {
                            holder.saveButton.setImageResource(R.drawable.bookmark);
                        }
                    }
                });

        if (post.getImageId() != null && !post.getImageId().isEmpty()) {
            loadImageFromFirestore(post.getImageId(), holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void loadImageFromFirestore(String imageId, final ImageView imageView) {
        getImageFromFirestore(imageId)
                .addOnSuccessListener(bitmap -> {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(e -> imageView.setVisibility(View.GONE));
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postAuthor, timestamp, likeCount;
        ImageView postImage;
        ImageButton likeButton, saveButton, dotsButton;

        public PostViewHolder(View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postAuthor = itemView.findViewById(R.id.postAuthor);
            timestamp = itemView.findViewById(R.id.postTimestamp);
            likeCount = itemView.findViewById(R.id.likeCount);
            postImage = itemView.findViewById(R.id.postImage);
            likeButton = itemView.findViewById(R.id.likeBtn);
            saveButton = itemView.findViewById(R.id.saveBtn);
            dotsButton = itemView.findViewById(R.id.dotsButton);
        }
    }
}
