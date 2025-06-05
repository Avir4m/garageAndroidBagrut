package com.example.garage.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Post {
    private String postId;
    private String title;
    private String authorId;
    private String author;
    private Timestamp timestamp;
    private List<String> likes;
    private int likeCount;
    private String imageId;
    private boolean archived;
    private boolean edited;

    public Post() {
    }

    public Post(String postId, String title, String authorId, String author, Timestamp timestamp, List<String> likes, int likeCount, String imageId, boolean archived, boolean edited) {
        this.postId = postId;
        this.title = title;
        this.authorId = authorId;
        this.author = author;
        this.timestamp = timestamp;
        this.likes = likes;
        this.likeCount = likeCount;
        this.imageId = imageId;
        this.archived = archived;
        this.edited = edited;
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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public boolean getArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean getEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
