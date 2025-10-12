package com.example.lab4_fragments;

public class Comment {
    private String username;
    private String text;
    private float rating;

    public Comment(String username, String text, float rating) {
        this.username = username;
        this.text = text;
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public float getRating() {
        return rating;
    }
}