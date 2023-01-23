package com.example.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.asynchttpclient.Param;

import com.example.model.Admin;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.AdminState;
import com.google.gson.JsonSyntaxException;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class loginController implements Initializable, Swappable {

    @FXML
    public TextField loginField;
    public PasswordField passField;

    @Override
    public void onLoad(SceneName previousSceneName) {
        loginField.setText("");
        passField.setText("");
    }

    @FXML
    public void login() {
        String login = loginField.getText();
        String pass = passField.getText();
        AdminState.setUserName(login);
        AdminState.setPassword(pass);
        Admin admin = AHClientHandler.getAHClientHandler().getRequestSync("/admin/login", new ArrayList<>(),
                Admin.class);
        if (AdminState.getUserName() != "" && AdminState.getPassword() != "") {
            SceneNavigator.setScene(SceneName.APP_MAIN_MENU);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void onUnload() {
    }

}
