package com.example.lab4_fragments;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Edificaciones",
        foreignKeys = @ForeignKey(entity = Categoria.class,
                parentColumns = "category_id",
                childColumns = "category_id",
                onDelete = ForeignKey.SET_NULL,
                onUpdate = ForeignKey.CASCADE))
public class Edificacion {
    @PrimaryKey(autoGenerate = true)
    private int building_id;
    private int category_id;
    private String title;
    private String description;
    private String image_res_id;
    private double latitude;
    private double longitude;

    // Getters y setters
    public int getBuildingId() {
        return building_id;
    }

    public void setBuildingId(int building_id) {
        this.building_id = building_id;
    }

    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageResId() {
        return image_res_id;
    }

    public void setImageResId(String image_res_id) {
        this.image_res_id = image_res_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

