package com.example.controller;

import com.example.scene.SceneNavigator;
import com.example.scene.SceneName;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LastController implements Initializable, Swappable {
    @Override
    public void onLoad(SceneName previousSceneName) {
    }

    @Override
    public void onUnload() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialized second");
    }

    @FXML
    public void onBackClicked() {
        SceneNavigator.setScene(SceneName.FIRST);
    }

}
