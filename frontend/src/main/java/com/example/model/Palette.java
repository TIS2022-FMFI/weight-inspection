package com.example.model;

public class Palette {
    private int palette_id;
    private String palette_type;
    private String name;
    private Float weight;
    private String picture_path;

    public int getPalette_id() {
        return palette_id;
    }

    public void setPalette_id(int palette_id) {
        this.palette_id = palette_id;
    }

    public String getPalette_type() {
        return palette_type;
    }

    public void setPalette_type(String palette_type) {
        this.palette_type = palette_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }
}
