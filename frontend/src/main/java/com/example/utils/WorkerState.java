package com.example.utils;

public class WorkerState {
    private static String IDP;
    private static String refference;
    private static Integer quantity;

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
