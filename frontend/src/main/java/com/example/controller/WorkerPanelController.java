package com.example.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.utils.AdminState;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class WorkerPanelController extends HBox implements Initializable {
    @FXML
    public Label nameLabel;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameLabel.textProperty().bind(AdminState.getUserNameLabel());
    }
}
