package com.example.controller;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.WorkerState;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class correctWeighingController extends ScannerController implements Swappable {

    @FXML
    public Label weight;
    @FXML
    public Label weightDifference;
    @FXML
    public Label expectedWeight;

    @Override
    public void onLoad(SceneName previousSceneName) {
        weight.setText(WorkerState.getWorkerState().getWeight().toString());
        expectedWeight.setText(WorkerState.getWorkerState().getWeighing().getCalculatedWeight().toString());
        weightDifference
                .setText(new Float(Math.abs(WorkerState.getWorkerState().getWeight()
                        - WorkerState.getWorkerState().getWeighing().getCalculatedWeight()))
                        .toString());
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
