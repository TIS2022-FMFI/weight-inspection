package com.example.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

public class WorkerPanelController extends HBox {

    public WorkerPanelController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/workerPanel.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
