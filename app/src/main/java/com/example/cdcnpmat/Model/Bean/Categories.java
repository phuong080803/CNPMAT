package com.example.cdcnpmat.Model.Bean;

public class Categories {
    private int id;
    private String nameCategory;

    public Categories(int id, String nameCategory) {
        this.id = id;
        this.nameCategory = nameCategory;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNameCategory() {
        return nameCategory;
    }
    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }
}
