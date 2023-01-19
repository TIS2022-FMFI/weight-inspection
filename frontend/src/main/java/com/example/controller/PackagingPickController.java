package com.example.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.model.Packaging;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.WorkerState;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class PackagingPickController extends ScannerController implements Swappable {

    @FXML
    private GridPane mainGrid;
    @FXML
    private Label name0;
    @FXML
    private Label name1;
    @FXML
    private Label name2;
    @FXML
    private Label name3;
    @FXML
    private Label type0;
    @FXML
    private Label type1;
    @FXML
    private Label type2;
    @FXML
    private Label type3;
    @FXML
    private Label photo0;
    @FXML
    private Label photo1;
    @FXML
    private Label photo2;
    @FXML
    private Label photo3;

    private int currentIndex = 0;

    private List<Packaging> packagings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        WorkerPanelController workerPanel = new WorkerPanelController();
        mainGrid.getChildren().add(workerPanel);

    }

    public void updateTable() {
        if (currentIndex < packagings.size()) {
            name0.setText(packagings.get(currentIndex).getName());
            type0.setText(packagings.get(currentIndex).getType());
        } else {
            name0.setText("");
            type0.setText("");
        }
        if (currentIndex + 1 < packagings.size()) {
            name1.setText(packagings.get(currentIndex + 1).getName());
            type1.setText(packagings.get(currentIndex + 1).getType());
        } else {
            name1.setText("");
            type1.setText("");
        }
        if (currentIndex + 2 < packagings.size()) {
            name2.setText(packagings.get(currentIndex + 2).getName());
            type2.setText(packagings.get(currentIndex + 2).getType());
        } else {
            name2.setText("");
            type2.setText("");
        }
        if (currentIndex + 3 < packagings.size()) {
            name3.setText(packagings.get(currentIndex + 3).getName());
            type3.setText(packagings.get(currentIndex + 3).getType());
        } else {
            name3.setText("");
            type3.setText("");
        }
    }

    @FXML
    public void back() {
        packagings = null;
        WorkerState.getWorkerState().setPaletteId(null);
        WorkerState.getWorkerState().setPackagingId(null);
        SceneNavigator.setScene(SceneName.PALETTE_PICK);
    }

    @FXML
    public void down() {
        if (currentIndex + 4 < packagings.size()) {
            currentIndex += 4;
        }
        updateTable();
    }

    @FXML
    public void up() {
        if (currentIndex - 4 >= 0) {
            currentIndex -= 4;
        }
        updateTable();
    }

    @FXML
    public void newPackaging() {
        WorkerState.getWorkerState().sendWeighing(false);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        currentIndex = 0;
        packagings = AHClientHandler.getAHClientHandler().getPageSync(
                "/product/" + WorkerState.getWorkerState().getProductId() + "/packaging/", new ArrayList<>(), 0, 0,
                Packaging.class);
        if (packagings == null) {
            SceneNavigator.setScene(previousSceneName);
            return;
        }
        updateTable();
    }

    @Override
    public void onUnload() {

    }

    @Override
    public void onBarcodeScanned(String barcode) {
        try {
            switch (barcode) {
                case "NEWPACK":
                    newPackaging();
                    return;
                case "BACK":
                    back();
                    return;
                case "UPXX":
                    up();
                    return;
                case "DOWN":
                    down();
                    return;
                case "1OPTION":
                    WorkerState.getWorkerState().setPackagingId(Integer.valueOf(packagings.get(currentIndex).getId()));
                    packagings = null;
                    WorkerState.getWorkerState().sendWeighing(true);
                    return;
                case "2OPTION":
                    WorkerState.getWorkerState()
                            .setPackagingId(Integer.valueOf(packagings.get(currentIndex + 1).getId()));
                    packagings = null;
                    WorkerState.getWorkerState().sendWeighing(true);
                    return;
                case "3OPTION":
                    WorkerState.getWorkerState()
                            .setPackagingId(Integer.valueOf(packagings.get(currentIndex + 2).getId()));
                    packagings = null;
                    WorkerState.getWorkerState().sendWeighing(true);
                    return;
                case "4OPTION":
                    WorkerState.getWorkerState()
                            .setPackagingId(Integer.valueOf(packagings.get(currentIndex + 3).getId()));
                    packagings = null;
                    WorkerState.getWorkerState().sendWeighing(true);
                    return;
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e2) {
        }
    }

}
