package com.example.controller;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AdminState;

import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppMainMenuController extends TableController implements Swappable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    @Override
    public void updateTable() {
    }

    @Override
    public void updateButtons() {
    }

    @FXML
    public void openOption1() {
        SceneNavigator.setScene(SceneName.SCAN_PAGE);
    }

    @FXML
    public void openOption2() {
        SceneNavigator.setScene(SceneName.ADMIN_MAIN_MENU);
    }

    @FXML
    public void openWeb() {
        try {
            java.awt.Desktop.getDesktop().browse(new URI(AdminState.getWebPage()));
        } catch (IOException | URISyntaxException e) {
        }
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
    }

    @Override
    public void onUnload() {
    }
}
