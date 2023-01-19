package com.example.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.asynchttpclient.Param;

import com.example.model.Product;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.WorkerState;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

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
        if (idpLabel.getText() == "" || refferenceLabel.getText() == "" || quantityLabel.getText() == "") {
            return;
        }
        WorkerState.getWorkerState().setIdp(idpLabel.getText());
        WorkerState.getWorkerState().setReference(refferenceLabel.getText());
        WorkerState.getWorkerState().setQuantity(Integer.valueOf(quantityLabel.getText()));
        List<Product> products = AHClientHandler.getAHClientHandler().getPageSync("/product",
                Arrays.asList(new Param("reference", WorkerState.getWorkerState().getReference())), 0, 0,
                Product.class);
        if (products == null || products.size() != 1) {
            return;
        }
        Product product = products.get(0);
        try {
            WorkerState.getWorkerState().setProductId(Integer.valueOf(product.getId()));
        } catch (NumberFormatException e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Nastala chyba");
            errorAlert.setContentText("Asi ten product nema ID, poproste administratora o pomoc");
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e2 -> {
                errorAlert.hide();
            });
            errorAlert.show();
            delay.play();
        }
        SceneNavigator.setScene(SceneName.PALETTE_PICK);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        if (WorkerState.getWorkerState().getIdp() == null || WorkerState.getWorkerState().getReference() == null
                || WorkerState.getWorkerState().getQuantity() == null) {
            refferenceLabel.setText("");
            idpLabel.setText("");
            quantityLabel.setText("");
            return;
        }
        refferenceLabel.setText(WorkerState.getWorkerState().getReference());
        idpLabel.setText(WorkerState.getWorkerState().getIdp());
        quantityLabel.setText(String.valueOf(WorkerState.getWorkerState().getQuantity()));

    }

    @Override
    public void onUnload() {

    }

    @Override
    public void onBarcodeScanned(String barcode) {
        switch (barcode) {
            case "NEXT":
                next();
                return;
            case "BACK":
                back();
                return;
        }
        if (barcode.charAt(0) == 'P') {
            refferenceLabel.setText(barcode);
            return;
        }
        if (barcode.charAt(0) == 'M' || barcode.charAt(0) == 'S') {
            idpLabel.setText(barcode);
            return;
        }
        if (barcode.charAt(0) == 'Q') {
            quantityLabel.setText(barcode.substring(1));
            return;
        }
    }

}
