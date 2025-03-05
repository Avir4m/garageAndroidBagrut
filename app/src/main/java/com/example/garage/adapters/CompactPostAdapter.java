package com.example.garage.adapters;

import static com.example.garage.functions.ImageUtils.getImageFromFirestore;
import static com.example.garage.functions.formatUtils.getTimeAgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.R;
import com.example.garage.models.Post;

import java.util.Date;
import java.util.List;

public class CompactPostAdapter extends RecyclerView.Adapter<CompactPostAdapter.PostViewHolder> {
    private final Context context;
    private final List<Post> posts;

    public CompactPostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.compact_post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.postTitle.setText(post.getTitle());
        Date timestamp = post.getTimestamp().toDate();
        String timeAgo = getTimeAgo(timestamp);
        holder.timestamp.setText(timeAgo);

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
                .addOnFailureListener(e -> {
                    imageView.setVisibility(View.GONE);
                });
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle;
        TextView timestamp;
        TextView likeCount;
        ImageView postImage;
        ImageButton likeButton;

        public PostViewHolder(View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            timestamp = itemView.findViewById(R.id.postTimestamp);
            likeCount = itemView.findViewById(R.id.likeCount);
            postImage = itemView.findViewById(R.id.postImage);
            likeButton = itemView.findViewById(R.id.likeBtn);
        }
    }
}
