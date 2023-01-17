package com.example.controller;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AdminState;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminPanelController extends HBox implements Initializable {
    @FXML
    public Label nameLabel;

    public AdminPanelController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/adminPanel.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    public void notification() {
        SceneNavigator.setScene(SceneName.NOTIFICATIONS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameLabel.setText(AdminState.getUserName());
    }

}