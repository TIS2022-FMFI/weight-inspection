package com.example.model;

import java.sql.Timestamp;

public class Weighing {
    private Integer id;
    private String idp = "";
    private Float weight = 0f;
    private Integer quantity;
    private Timestamp weighedOn;
    private Boolean correct = true;
    private String packagingName = "";
    private String paletteName = "";
    private String productReference = "";
    private Palette palette;
    private Packaging packaging;
    private Float calculatedWeight;

    public Float getCalculatedWeight() {
        return calculatedWeight;
    }

    public void setCalculatedWeight(Float calculatedWeight) {
        this.calculatedWeight = calculatedWeight;
    }

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    public Packaging getPackaging() {
        return packaging;
    }

    public void setPackaging(Packaging packaging) {
        this.packaging = packaging;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdp() {
        return idp;
    }

    public void setIdp(String IDP) {
        this.idp = IDP;
    }

    public String getWeight() {
        return weight.toString();
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Timestamp getWeighedOn() {
        return weighedOn;
    }

    public void setWeighedOn(Timestamp weighedOn) {
        this.weighedOn = weighedOn;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        correct = correct;
    }

    public String getPackagingName() {
        return packagingName;
    }

    public void setPackagingName(String packagingName) {
        this.packagingName = packagingName;
    }

    public String getPaletteName() {
        return paletteName;
    }

    public void setPaletteName(String paletteName) {
        this.paletteName = paletteName;
    }

    public String getProductReference() {
        return productReference;
    }

    public void setProductReference(String reference) {
        this.productReference = reference;
    }
}
