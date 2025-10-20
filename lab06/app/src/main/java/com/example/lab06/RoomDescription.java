package com.example.lab06;

public class RoomDescription {
    private String title;
    private String description;
    private int imageUrl;

    public RoomDescription(String title, String description, int imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageUrl() {
        return imageUrl;
    }
}
