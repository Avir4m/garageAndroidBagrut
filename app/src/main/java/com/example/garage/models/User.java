package com.example.garage.models;

public class User {
    private String userId;
    private String username;
    private String name;
    private String image;

    public User() {}

    public User(String userId, String username, String name, String image) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.image = image;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getImage() { return image; }
}
