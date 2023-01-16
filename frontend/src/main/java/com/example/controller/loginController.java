package com.example.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.asynchttpclient.Param;

import com.example.scene.SceneName;
import com.example.utils.AHClientHandler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class loginController implements Initializable, Swappable {

    @FXML
    public TextField loginField;
    public TextField passField;

    @Override
    public void onLoad(SceneName previousSceneName) {
        loginField.setText("");
        passField.setText("");
    }

    @FXML
    public void login() {
        String login = loginField.getText();
        String pass = passField.getText();
        AHClientHandler.getAHClientHandler().getRequestSync("/admin/login",
                Arrays.asList(new Param("login", login), new Param("password", pass)), );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void onUnload() {
    }

}
