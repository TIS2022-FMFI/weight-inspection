package com.example.model;

import com.example.controller.TableController;
import com.example.utils.AHClientHandler;

public class Packaging {
    private Integer id;
    private String type = "";
    private String name = "";
    private Float weight = 0f;
    private Integer quantity = 0;

    public String getId() {
        if (id == null) {
            return "";
        }
        return id.toString();
    }

    public void setId(Integer packaging_id) {
        this.id = packaging_id;
    }

    public String getType() {
        if (type == null) {
            return "";
        }
        return type;
    }

    public void setType(String packaging_type) {
        this.type = packaging_type;
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        if (weight == null) {
            return "";
        }
        return weight.toString();
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getQuantity() {
        if (quantity == null) {
            return "";
        }
        return quantity.toString();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPicturePath() {
        if (id == null) {
            return "";
        }
        return "/static/packaging/" + String.valueOf(id);
    }

    public void post(TableController controller) {
        if (id == null) {
            AHClientHandler.getAHClientHandler().postRequest("/packaging", this, controller);
            return;
        }
        throw new IllegalStateException("Can't POST packaging that has ID, probably ment to use PUT");
    }

    public void put(TableController controller) {
        AHClientHandler.getAHClientHandler().putRequest("/packaging/" + String.valueOf(id), this, controller);
    }

    public void delete(TableController controller) {
        AHClientHandler.getAHClientHandler().deleteRequest("/packaging/" + String.valueOf(id), controller);
    }
}
