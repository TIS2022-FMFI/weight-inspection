package com.example.model;

public class Packaging {
    private Integer id;
    private String type;
    private String name;
    private Float weight;
    private String picture_path;

    public int getId() {
        return id;
    }

    public void setId(Integer packaging_id) {
        this.id = packaging_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String packaging_type) {
        this.type = packaging_type;
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
