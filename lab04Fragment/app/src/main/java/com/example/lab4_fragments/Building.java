package com.example.lab4_fragments;

public class Building {
    private String title;
    private String category;
    private String description;
    private int imageResId;
    private double latitude;
    private double longitude;

    public Building(String title, String category, String description, int imageResId, double latitude, double longitude) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.imageResId = imageResId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
