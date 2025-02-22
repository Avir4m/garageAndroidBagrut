package com.example.garage.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.garage.functions.ImageUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;

import java.util.Date;
import java.util.List;

public class Post {
    private String title;
    private String author;
    private String authorId;
    private Date timestamp;
    private List<String> likes;
    private int likeCount;
    private String imageId;

    public Post(String title, String author, String authorId, Date timestamp, List<String> likes, int likeCount, String imageId) {
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.timestamp = timestamp;
        this.likes = likes;
        this.likeCount = likeCount;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void uploadPostToFirestore(Context context, Bitmap bitmap, CollectionReference postsCollection) {
        if (bitmap != null) {
            ImageUtils.uploadImageToFirestore(bitmap)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String docId) {
                            setImageId(docId); // Set imageId from Firestore
                            saveToFirestore(context, postsCollection);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("Firestore", "Image upload failed: ", e);
                            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            saveToFirestore(context, postsCollection);
        }
    }

    private void saveToFirestore(Context context, CollectionReference postsCollection) {
        postsCollection.add(this)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Post added successfully");
                    Toast.makeText(context, "Post added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to add post", e);
                    Toast.makeText(context, "Failed to add post", Toast.LENGTH_SHORT).show();
                });
    }
}
