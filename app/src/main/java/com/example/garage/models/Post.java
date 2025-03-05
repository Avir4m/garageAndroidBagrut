package com.example.garage.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Post {
    private String postId;
    private String title;
    private String author;
    private String authorId;
    private Timestamp timestamp;
    private List<String> likes;
    private int likeCount;
    private String imageId;

    public Post() {
    }

    public Post(String postId, String title, String author, String authorId, Timestamp timestamp, List<String> likes, int likeCount, String imageId) {
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.timestamp = timestamp;
        this.likes = likes;
        this.likeCount = likeCount;
        this.imageId = imageId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
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
}
