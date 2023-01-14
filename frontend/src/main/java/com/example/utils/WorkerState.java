package com.example.utils;

import com.example.model.Weighing;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;

public class WorkerState {
    private static String IDP;
    private static String reference;
    private static Integer productId;
    private static Integer quantity;
    private static Integer packagingId;
    private static Integer paletteId;
    private static Float weight;
    private static Weighing weighing;

    public static Weighing getWeighing() {
        return weighing;
    }

    public static void setWeighing(Weighing weighing) {
        WorkerState.weighing = weighing;
    }

    public static Integer getProductId() {
        return productId;
    }

    public static void setProductId(Integer productId) {
        WorkerState.productId = productId;
    }

    public static Integer getPackagingId() {
        return packagingId;
    }

    public static void setPackagingId(Integer packagingId) {
        WorkerState.packagingId = packagingId;
    }

    public static Integer getPaletteId() {
        return paletteId;
    }

    public static void setPaletteId(Integer paletteId) {
        WorkerState.paletteId = paletteId;
    }

    public static Float getWeight() {
        return weight;
    }

    public static void setWeight(Float weight) {
        WorkerState.weight = weight;
    }

    public static String getIDP() {
        return IDP;
    }

    public static void setIDP(String IDP) {
        WorkerState.IDP = IDP;
    }

    public static String getReference() {
        return reference;
    }

    public static void setReference(String refference) {
        WorkerState.reference = refference;
    }

    public static Integer getQuantity() {
        return quantity;
    }

    public static void setQuantity(Integer quantity) {
        WorkerState.quantity = quantity;
    }

    public static void sendWeighing() {
        Integer pId = getProductId();
        setProductId(null);
        setWeight(ScalesConnector.getWeight());
        Weighing weighing = AHClientHandler.getAHClientHandler().postRequestSync("weighing", new WorkerState(),
                Weighing.class);
        setWeighing(weighing);
        if (!weighing.getCorrect()) {
            setProductId(pId);
            SceneNavigator.setScene(SceneName.WRONG_WEIGHING);
        }
        SceneNavigator.setScene(SceneName.CORRECT_WEIGHING);
    }
}
