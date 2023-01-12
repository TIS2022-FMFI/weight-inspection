package com.example.utils;

public class AdminState {
    private static String userName;
    private static int notificationCount = 0;
    private static Integer connectedPaletteId;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        AdminState.userName = userName;
    }

    public static int getNotificationCount() {
        return notificationCount;
    }

    public static void setNotificationCount(int notificationCount) {
        AdminState.notificationCount = notificationCount;
    }

    public static Integer getConnectedPaletteId() {
        return connectedPaletteId;
    }

    public static void setConnectedPaletteId(Integer connectedPaletteId) {
        AdminState.connectedPaletteId = connectedPaletteId;
    }

}