package com.example.model;

import com.example.utils.AHClientHandler;

public class Product
{
    private Integer id;
    private String reference;
    private Float weight;

    public int getId()
    {
        return id;
    }

    public void setId(Integer product_id)
    {
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

    public void post()
    {
        if (id == null)
        {
            AHClientHandler.getAHClientHandler().postRequest("/product", this);
            return;
        }
        throw new IllegalStateException("Can't POST product that has ID, probably ment to use PUT");
    }

    public void put()
    {
        AHClientHandler.getAHClientHandler().putRequest("/product/" + String.valueOf(id), this);
    }

    public void delete()
    {
        AHClientHandler.getAHClientHandler().deleteRequest("/product/" + String.valueOf(id));
    }
}
