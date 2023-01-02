package com.example.model;

import com.example.utils.AHClientHandler;

public class Palette {
    private Integer id;
    private String type;
    private String name;
    private Float weight;

    public int getId() {
        return id;
    }

    public void setId(Integer palette_id) {
        this.id = palette_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String palette_type) {
        this.type = palette_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight.toString();
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getPicturePath() {
        return "/static/palette/" + String.valueOf(id);
    }

    public void post() {
        if (id == null) {
            AHClientHandler.getAHClientHandler().postRequest("/palette", this);
            return;
        }
        throw new IllegalStateException("Can't POST palette that has ID, probably ment to use PUT");
    }

    public void put() {
        AHClientHandler.getAHClientHandler().putRequest("/palette/" + String.valueOf(id), this);
    }

    public void delete() {
        AHClientHandler.getAHClientHandler().deleteRequest("/palette/" + String.valueOf(id));
    }
}
