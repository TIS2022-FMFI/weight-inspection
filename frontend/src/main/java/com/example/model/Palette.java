package com.example.model;

import com.example.controller.TableController;
import com.example.utils.AHClientHandler;

public class Palette {
    private Integer id;
    private String type = "";
    private String name = "";
    private Float weight = 0f;

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
        if (weight == null)
        {
            return "";
        }
        return weight.toString();
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getPicturePath() {
        return "/static/palette/" + String.valueOf(id);
    }

    public void post(TableController controller) {
        if (id == null) {
            AHClientHandler.getAHClientHandler().postRequest("/palette", this, controller);
            return;
        }
        throw new IllegalStateException("Can't POST palette that has ID, probably ment to use PUT");
    }

    public void put(TableController controller) {
        AHClientHandler.getAHClientHandler().putRequest("/palette/" + String.valueOf(id), this, controller);
    }

    public void delete(TableController controller) {
        AHClientHandler.getAHClientHandler().deleteRequest("/palette/" + String.valueOf(id), controller);
    }
}
