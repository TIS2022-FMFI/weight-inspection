package com.example.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.model.Palette;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.AdminState;
import com.example.utils.WorkerState;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class PalettePickController extends ScannerController implements Swappable {

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
    private ImageView photo0;
    @FXML
    private ImageView photo1;
    @FXML
    private ImageView photo2;
    @FXML
    private ImageView photo3;

    private int currentIndex = 0;

    private List<Palette> palettes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        WorkerPanelController workerPanel = new WorkerPanelController();
        mainGrid.getChildren().add(workerPanel);

    }

    public void updateTable() {
        if (currentIndex < palettes.size()) {
            name0.setText(palettes.get(currentIndex).getName());
            type0.setText(palettes.get(currentIndex).getType());
            Image image = new Image(AdminState.getServer() + palettes.get(currentIndex).getPicturePath(), true);
            photo0.setImage(image);
        } else {
            name0.setText("");
            type0.setText("");
            photo0.setImage(null);
        }
        if (currentIndex + 1 < palettes.size()) {
            name1.setText(palettes.get(currentIndex + 1).getName());
            type1.setText(palettes.get(currentIndex + 1).getType());
            Image image = new Image(AdminState.getServer() + palettes.get(currentIndex + 1).getPicturePath(), true);
            photo1.setImage(image);
        } else {
            name1.setText("");
            type1.setText("");
            photo1.setImage(null);
        }
        if (currentIndex + 2 < palettes.size()) {
            name2.setText(palettes.get(currentIndex + 2).getName());
            type2.setText(palettes.get(currentIndex + 2).getType());
            Image image = new Image(AdminState.getServer() + palettes.get(currentIndex + 2).getPicturePath(), true);
            photo2.setImage(image);
        } else {
            name2.setText("");
            type2.setText("");
            photo2.setImage(null);
        }
        if (currentIndex + 3 < palettes.size()) {
            name3.setText(palettes.get(currentIndex + 3).getName());
            type3.setText(palettes.get(currentIndex + 3).getType());
            Image image = new Image(AdminState.getServer() + palettes.get(currentIndex + 3).getPicturePath(), true);
            photo3.setImage(image);
        } else {
            name3.setText("");
            type3.setText("");
            photo3.setImage(null);
        }
    }

    @FXML
    public void back() {
        palettes = null;
        WorkerState.getWorkerState().setPaletteId(null);
        SceneNavigator.setScene(SceneName.SCAN_PAGE);
    }

    @FXML
    public void down() {
        if (currentIndex + 4 < palettes.size()) {
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
    public void newPalette() {
        WorkerState.getWorkerState().sendWeighing(false);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        currentIndex = 0;
        palettes = AHClientHandler.getAHClientHandler().getPageSync(
                "/product/" + WorkerState.getWorkerState().getProductId() + "/palette/", new ArrayList<>(), 0, 0,
                Palette.class);
        if (palettes == null) {
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
                case "NEWPALL":
                    newPalette();
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
                    WorkerState.getWorkerState().setPaletteId(Integer.valueOf(palettes.get(currentIndex).getId()));
                    SceneNavigator.setScene(SceneName.PACKAGING_PICK);
                    return;
                case "2OPTION":
                    WorkerState.getWorkerState().setPaletteId(Integer.valueOf(palettes.get(currentIndex + 1).getId()));
                    SceneNavigator.setScene(SceneName.PACKAGING_PICK);
                    return;
                case "3OPTION":
                    WorkerState.getWorkerState().setPaletteId(Integer.valueOf(palettes.get(currentIndex + 2).getId()));
                    SceneNavigator.setScene(SceneName.PACKAGING_PICK);
                    return;
                case "4OPTION":
                    WorkerState.getWorkerState().setPaletteId(Integer.valueOf(palettes.get(currentIndex + 3).getId()));
                    SceneNavigator.setScene(SceneName.PACKAGING_PICK);
                    return;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
        }
    }

}
