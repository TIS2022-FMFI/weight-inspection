package com.example.controller;

import com.example.scene.SceneName;

import javafx.fxml.FXML;

public class correctWeighingController extends ScannerController implements Swappable {

    @Override
    public void onLoad(SceneName previousSceneName) {
        
    }

    @Override
    public void onUnload() {

    }

    @Override
    public void onBarcodeScanned(String barcode) {
        switch (barcode) {
            case "ALLDONE":
                allDone();
                return;
        }
    }

    @FXML
    private void allDone() {
    }

}
