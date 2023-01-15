package com.example.utils;

public class AdminState {
    private static String userName;
    public static void logOut(){}

    private static Integer connectedPaletteId;
    private static Integer connectedPackagingId;

    private static int notificationCount = 0;
    private static Integer connectedNotificationId;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        AdminState.userName = userName;
    }

    public static Integer getConnectedPaletteId() {return connectedPaletteId;}

    public static void setConnectedPaletteId(Integer connectedPaletteId) {AdminState.connectedPaletteId = connectedPaletteId;}

    public static Integer getConnectedPackagingId() {return connectedPackagingId;}

    public static void setConnectedPackagingId(Integer connectedPackagingId) {AdminState.connectedPackagingId = connectedPackagingId;}

    public static int getNotificationCount() {return notificationCount;}

    public static void setNotificationCount(int notificationCount) {AdminState.notificationCount = notificationCount;}

    public static Integer getConnectedNotificationId() {return connectedNotificationId;}

    public static void setConnectedNotificationId(Integer connectedNotificationId) {AdminState.connectedNotificationId = connectedNotificationId;}
}