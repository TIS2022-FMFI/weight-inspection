package com.example.model;

import com.example.utils.AHClientHandler;

public class Packaging {
    private Integer id;
    private String type;
    private String name;
    private Float weight;

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

    public String getPicturePath() {
        return "/static/packaging/" + String.valueOf(id);
    }

    public void post() {
        if (id == null) {
            AHClientHandler.getAHClientHandler().postRequest("/packaging", this);
            return;
        }
        throw new IllegalStateException("Can't POST packaging that has ID, probably ment to use PUT");
    }

    public void put() {
        AHClientHandler.getAHClientHandler().putRequest("/packaging/" + String.valueOf(id), this);
    }

    public void delete() {
        AHClientHandler.getAHClientHandler().deleteRequest("/packaging/" + String.valueOf(id));
    }
}