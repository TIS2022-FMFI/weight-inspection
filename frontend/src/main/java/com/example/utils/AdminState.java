package com.example.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;

import javafx.beans.value.ObservableStringValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

public class AdminState {
    private static String userName;
    private static String password;
    private static String server;
    private static String webPage;

    private static Integer connectedPaletteId;
    private static Integer connectedPackagingId;

    private static int notificationCount = 0;
    private static Integer connectedNotificationId;

    public static File pickImage(String newName) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(SceneNavigator.getStage());
        if (file == null) {
            return null;
        }
        Image image = new Image(file.toURI().toString());
        if (image.getHeight() > 800 || image.getWidth() > 800 || !file.toURI().toString().contains(".png")) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Nastala chyba");
            errorAlert.setContentText("Obrazok musi byt .png, a najviac rozmeru 800x800");
            errorAlert.showAndWait();
            return null;
        }

        Path copied = Paths.get("./" + newName);
        Path originalPath = file.toPath();
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            return null;
        }
        return copied.toFile();
    }

    public static void logOut() {
        setUserName("");
        setPassword("");
        AHClientHandler.remake();
        SceneNavigator.setScene(SceneName.LOGIN);
    }

    public static void setPassword(String password) {
        AdminState.password = password;
    }

    public static String getPassword() {
        return password;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        AdminState.userName = userName;
    }

    public static Integer getConnectedPaletteId() {
        return connectedPaletteId;
    }

    public static void setConnectedPaletteId(Integer connectedPaletteId) {
        AdminState.connectedPaletteId = connectedPaletteId;
    }

    public static Integer getConnectedPackagingId() {
        return connectedPackagingId;
    }

    public static void setConnectedPackagingId(Integer connectedPackagingId) {
        AdminState.connectedPackagingId = connectedPackagingId;
    }

    public static int getNotificationCount() {
        return notificationCount;
    }

    public static void setNotificationCount(int notificationCount) {
        AdminState.notificationCount = notificationCount;
    }

    public static Integer getConnectedNotificationId() {
        return connectedNotificationId;
    }

    public static void setConnectedNotificationId(Integer connectedNotificationId) {
        AdminState.connectedNotificationId = connectedNotificationId;
    }

    public static String getServer() {
        return server;
    }

    public static void setServer(String server) {
        AdminState.server = server;
    }

    public static String getWebPage() {
        return webPage;
    }

    public static void setWebPage(String webPage) {
        AdminState.webPage = webPage;
    }
}