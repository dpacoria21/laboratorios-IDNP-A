package com.example.lab4_fragments;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Categorias")
public class Categoria {
    @PrimaryKey(autoGenerate = true)
    private int category_id;
    private String category_name;

    // Getters y setters
    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }
}

