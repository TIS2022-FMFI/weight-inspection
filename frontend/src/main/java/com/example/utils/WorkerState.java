package com.example.utils;

import com.example.model.Weighing;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;

public class WorkerState {
    private String idp;
    private String reference;
    private Integer productId;
    private Integer quantity;
    private Integer packagingId;
    private Integer paletteId;
    private Float weight;
    private Weighing weighing;
    private static WorkerState workerState;

    public static WorkerState getWorkerState() {
        if (workerState == null) {
            workerState = new WorkerState();
        }
        return workerState;
    }

    public String getIdp() {
        return idp;
    }

    public void setIdp(String iDP) {
        idp = iDP;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPackagingId() {
        return packagingId;
    }

    public void setPackagingId(Integer packagingId) {
        this.packagingId = packagingId;
    }

    public Integer getPaletteId() {
        return paletteId;
    }

    public void setPaletteId(Integer paletteId) {
        this.paletteId = paletteId;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Weighing getWeighing() {
        return weighing;
    }

    public void setWeighing(Weighing weighing) {
        this.weighing = weighing;
    }

    public void sendWeighing(Boolean withSummary) {
        Integer pId = getProductId();
        setProductId(null);
        setWeight(ScalesConnector.getWeight());
        Weighing weighing = AHClientHandler.getAHClientHandler().postRequestSync("/weighing", this,
                Weighing.class);
        setWeighing(weighing);
        if (weighing == null || !withSummary) {
            setIdp(null);
            setPackagingId(null);
            setPaletteId(null);
            setProductId(null);
            setQuantity(null);
            setReference(null);
            setWeighing(null);
            setWeight(null);
            SceneNavigator.setScene(SceneName.SCAN_PAGE);
            return;
        }
        if (!weighing.getCorrect()) {
            setProductId(pId);
            SceneNavigator.setScene(SceneName.WRONG_WEIGHING);
            return;
        }
        SceneNavigator.setScene(SceneName.CORRECT_WEIGHING);
    }
}
