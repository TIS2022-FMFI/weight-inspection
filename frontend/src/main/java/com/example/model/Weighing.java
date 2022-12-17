package com.example.model;

import java.sql.Timestamp;

public class Weighing {
    private Integer id;
    private String IDP;
    private Float weight;
    private Integer quantity;
    private Timestamp weighed_on;
    private Boolean is_correct;
    private String packaging_name;
    private String palette_name;
    private String reference;

    public int getId() {
        return id;
    }

    public void setId(Integer weighing_id) {
        this.id = weighing_id;
    }

    public String getIDP() {
        return IDP;
    }

    public void setIDP(String IDP) {
        this.IDP = IDP;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Timestamp getWeighed_on() {
        return weighed_on;
    }

    public void setWeighed_on(Timestamp weighed_on) {
        this.weighed_on = weighed_on;
    }

    public Boolean getIs_correct() {
        return is_correct;
    }

    public void setIs_correct(Boolean is_correct) {
        this.is_correct = is_correct;
    }

    public String getPackaging_name() {
        return packaging_name;
    }

    public void setPackaging_name(String packaging_name) {
        this.packaging_name = packaging_name;
    }

    public String getPalette_name() {
        return palette_name;
    }

    public void setPalette_name(String palette_name) {
        this.palette_name = palette_name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

}
