package com.example.controller;

import com.example.model.Notification;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.AdminState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NotificationPageController implements Swappable, Initializable {

    SceneName previousScene;

    @FXML
    private GridPane mainGrid;

    @FXML
    private Label typeLabel1;

    @FXML
    private TextArea textArea1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);
    }

    @FXML
    public void exit() {
        SceneNavigator.setScene(previousScene);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        Notification notification = AHClientHandler.getAHClientHandler().getRequestSync("/notification/" + AdminState.getConnectedNotificationId(),new ArrayList<>(), Notification.class);
        previousScene = previousSceneName;

        if (notification == null)
        {
            SceneNavigator.setScene(previousSceneName);
        }

        typeLabel1.setText(notification.getType());
        textArea1.setText(notification.getDescription());
    }

    @Override
    public void onUnload() {}
}