package com.example.model;

public class Product {
    private Integer id;
    private String reference;
    private Float weight;

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

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }
}
