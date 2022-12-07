package com.example.weight_inspection.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull
    private String reference;

    @NotNull
    private float weigth;

    public Product() {}

    @Override
    public String toString() {
        return "Product [id=" + id + ", reference=" + reference + ", weigth=" + weigth + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public float getWeigth() {
        return weigth;
    }

    public void setWeigth(float weigth) {
        this.weigth = weigth;
    }
}