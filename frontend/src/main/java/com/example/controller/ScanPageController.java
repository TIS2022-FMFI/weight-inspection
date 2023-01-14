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
        if (idpLabel.getText() == "" || refferenceLabel.getText() == "" || quantityLabel.getText() == "") {
            return;
        }
        WorkerState.setIDP(idpLabel.getText());
        WorkerState.setReference(refferenceLabel.getText());
        WorkerState.setQuantity(Integer.valueOf(quantityLabel.getText()));
        List<Product> products = AHClientHandler.getAHClientHandler().getPageSync("/product",
                Arrays.asList(new Param("reference", WorkerState.getReference())), 0, 0, Product.class);
        if (products == null || products.size() != 1) {
            return;
        }
        Product product = products.get(0);
        WorkerState.setProductId(product.getId());
        SceneNavigator.setScene(SceneName.PALETTE_PICK);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        if (WorkerState.getIDP() == null || WorkerState.getReference() == null || WorkerState.getQuantity() == null) {
            refferenceLabel.setText("");
            idpLabel.setText("");
            quantityLabel.setText("");
            return;
        }
        refferenceLabel.setText(WorkerState.getReference());
        idpLabel.setText(WorkerState.getIDP());
        quantityLabel.setText(String.valueOf(WorkerState.getQuantity()));

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
