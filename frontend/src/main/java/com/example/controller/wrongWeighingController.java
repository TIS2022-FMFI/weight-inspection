package com.example.controller;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.WorkerState;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class wrongWeighingController extends ScannerController implements Swappable {

    @FXML
    public Label packLabel;
    @FXML
    public Label paletteLabel;

    @Override
    public void onLoad(SceneName previousSceneName) {
        packLabel.setText(WorkerState.getWorkerState().getWeighing().getPackaging().getName());
        paletteLabel.setText(WorkerState.getWorkerState().getWeighing().getPalette().toString());
        // TODO:
        // paletteLabel.setText(WorkerState.getWorkerState().getWeighing().getPalette().getName());
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
        WorkerState.getWorkerState().setIdp(null);
        WorkerState.getWorkerState().setPackagingId(null);
        WorkerState.getWorkerState().setPaletteId(null);
        WorkerState.getWorkerState().setProductId(null);
        WorkerState.getWorkerState().setQuantity(null);
        WorkerState.getWorkerState().setReference(null);
        WorkerState.getWorkerState().setWeighing(null);
        WorkerState.getWorkerState().setWeight(null);
        SceneNavigator.setScene(SceneName.SCAN_PAGE);
    }

}
