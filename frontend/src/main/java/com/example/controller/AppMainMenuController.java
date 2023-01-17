package com.example.controller;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class AppMainMenuController extends TableController implements Swappable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    @Override
    public void updateTable() {}

    @Override
    public void updateButtons() {}

    @FXML
    public void openOption1() {
        SceneNavigator.setScene(SceneName.SCAN_PAGE);
    }

    @FXML
    public void openOption2() {
        SceneNavigator.setScene(SceneName.ADMIN_MAIN_MENU);
    }

    //Toto treba doriešiť...
    @FXML
    public void openOption3() {}

    @Override
    public void onLoad(SceneName previousSceneName) {}

    @Override
    public void onUnload() {}
}
