package com.example.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.scene.SceneName;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ScanPageController extends ScannerController implements Swappable {

    @FXML
    private GridPane mainGrid;
    @FXML
    private Label refferenceLabel;
    @FXML
    private Label quantityLabel;
    @FXML
    private Label idpLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        WorkerPanelController workerPanel = new WorkerPanelController();
        mainGrid.getChildren().add(workerPanel);

    }

    @FXML
    public void back() {
    }

    @FXML
    public void next() {
    }

    @Override
    public void onLoad(SceneName previousSceneName) {

    }

    @Override
    public void onUnload() {

    }

    @Override
    public void onBarcodeScanned(String barcode) {
        switch (barcode) {
            case "NEWPALL":
                break;
            case "NEXT":
                break;
        }
    }

}
