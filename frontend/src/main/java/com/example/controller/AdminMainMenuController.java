package com.example.controller;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMainMenuController extends TableController implements Swappable {

    @FXML
    private GridPane mainGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);
    }

    @FXML
    @Override
    public void updateTable() {}

    @Override
    public void updateButtons() {}

    @FXML
    public void openTable1() {
        SceneNavigator.setScene(SceneName.WEIGHINGS);
    }

    @FXML
    public void openTable2() {
        SceneNavigator.setScene(SceneName.PRODUCTS);
    }

    @FXML
    public void openTable3() {
        SceneNavigator.setScene(SceneName.PACKAGES);
    }

    @FXML
    public void openTable4() {
        SceneNavigator.setScene(SceneName.PALETTES);
    }

    @FXML
    public void openTable5() {
        SceneNavigator.setScene(SceneName.ADMINS);
    }

    @FXML
    public void openTable6() {
        SceneNavigator.setScene(SceneName.EMAILS);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {}

    @Override
    public void onUnload() {}
}
