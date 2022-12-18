package com.example.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class AdminPanelController extends HBox {

    public AdminPanelController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/adminPanel.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}