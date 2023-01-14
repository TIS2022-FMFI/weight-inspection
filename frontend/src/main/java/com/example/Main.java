package com.example;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.HashMap;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        HashMap<SceneName, String> scenes = new HashMap<>();
        scenes.put(SceneName.PALETTES, "/paletteTable.fxml");
        scenes.put(SceneName.PACKAGES, "/packagingTable.fxml");
        scenes.put(SceneName.PRODUCTS, "/productTable.fxml");
        scenes.put(SceneName.EMAILS, "/emailTable.fxml");
        scenes.put(SceneName.ADMINS, "/adminTable.fxml");
        scenes.put(SceneName.PALETTE_PRODUCT, "/paletteProductTable.fxml");
        scenes.put(SceneName.SCAN_PAGE, "/scanPage.fxml");

        SceneNavigator.initialize(primaryStage, scenes);
        SceneNavigator.setScene(SceneName.SCAN_PAGE);
        SceneNavigator.show();
    }
}