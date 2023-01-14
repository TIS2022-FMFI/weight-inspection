package com.example.utils;

public class WorkerState {
    private static String IDP;
    private static String refference;
    private static Integer productId;
    private static Integer quantity;
    private static Integer packagingId;
    private static Integer paletteId;
    private static Integer weight;

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

    public static Integer getWeight() {
        return weight;
    }

    public static void setWeight(Integer weight) {
        WorkerState.weight = weight;
    }

    public static String getIDP() {
        return IDP;
    }

    public static void setIDP(String IDP) {
        WorkerState.IDP = IDP;
    }

    public static String getRefference() {
        return refference;
    }

    public static void setRefference(String refference) {
        WorkerState.refference = refference;
    }

    public static Integer getQuantity() {
        return quantity;
    }

    public static void setQuantity(Integer quantity) {
        WorkerState.quantity = quantity;
    }
}
