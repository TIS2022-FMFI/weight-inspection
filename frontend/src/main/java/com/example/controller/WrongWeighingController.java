package com.example.controller;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AdminState;
import com.example.utils.WorkerState;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WrongWeighingController extends ScannerController implements Swappable {

    @FXML
    public Label packLabel;
    @FXML
    public ImageView packPhoto;
    @FXML
    public Label paletteLabel;
    @FXML
    public ImageView palettePhoto;

    @Override
    public void onLoad(SceneName previousSceneName) {
        packLabel.setText(WorkerState.getWorkerState().getWeighing().getPackaging().getName());
        Image image = new Image(
                AdminState.getServer() + WorkerState.getWorkerState().getWeighing().getPackaging().getPicturePath(),
                true);
        packPhoto.setImage(image);
        paletteLabel.setText(WorkerState.getWorkerState().getWeighing().getPalette().getName());
        Image image2 = new Image(
                AdminState.getServer() + WorkerState.getWorkerState().getWeighing().getPalette().getPicturePath(),
                true);
        palettePhoto.setImage(image2);
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
