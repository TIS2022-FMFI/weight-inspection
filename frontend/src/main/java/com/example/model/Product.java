package com.example.model;

import com.example.controller.TableController;
import com.example.utils.AHClientHandler;

public class Product {
    private Integer id;
    private String reference = "";
    private Float weight = 0f;

    public int getId() {
        return id;
    }

    public void setId(Integer product_id) {
        this.id = product_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getWeight() {return weight.toString();}

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public void post(TableController controller) {
        if (id == null) {
            AHClientHandler.getAHClientHandler().postRequest("/product", this, controller);
            return;
        }
        throw new IllegalStateException("Can't POST product that has ID, probably ment to use PUT");
    }

    public void put(TableController controller) {
        AHClientHandler.getAHClientHandler().putRequest("/product/" + String.valueOf(id), this, controller);
    }

    public void delete(TableController controller) {
        AHClientHandler.getAHClientHandler().deleteRequest("/product/" + String.valueOf(id), controller);
    }
}
