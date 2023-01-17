package com.example.model;

import com.example.controller.PackagingProductTableController;
import com.example.controller.PaletteProductTableController;
import com.example.controller.TableController;
import com.example.utils.AHClientHandler;

public class Product {
    private Integer id;
    private String reference = "";
    private Float weight = 0f;
    private Float tolerance = 0f;
    private Integer quantity = 0;

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

    public String getWeight() {
        if (weight == null) {
            return "";
        }
        return weight.toString();
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getTolerance() {
        return tolerance.toString();
    }

    public void setTolerance(Float tolerance) {
        this.tolerance = tolerance;
    }

    public String getQuantity() {
        return quantity.toString();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public void deleteForPalette(PaletteProductTableController controller, int paletteId) {
        AHClientHandler.getAHClientHandler().deleteRequest("/product/" + String.valueOf(id) + "/palette/" + String.valueOf(paletteId), controller);
    }

    public void postForPalette(PaletteProductTableController controller, Integer paletteId) {
        AHClientHandler.getAHClientHandler().postRequest("/product/" + String.valueOf(id) + "/palette/" + String.valueOf(paletteId), null, controller);
    }

    public void putForPackaging(PackagingProductTableController controller, int packagingId) {
        setReference(null);
        setWeight(null);
        AHClientHandler.getAHClientHandler().putRequest("/product/" + String.valueOf(id) + "/packaging/" + String.valueOf(packagingId), this, controller);
    }

    public void deleteForPackaging(PackagingProductTableController controller, int packagingId) {
        AHClientHandler.getAHClientHandler().deleteRequest("/product/" + String.valueOf(id) + "/packaging/" + String.valueOf(packagingId), controller);
    }

    public void postForPackaging(PackagingProductTableController controller, Integer packagingId) {
        setReference(null);
        setWeight(null);
        AHClientHandler.getAHClientHandler().postRequest("/product/" + String.valueOf(id) + "/packaging/" + String.valueOf(packagingId), this, controller);
    }
}
