package com.example;

import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AdminState;
import com.example.utils.Config;
import com.google.gson.Gson;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Reading config
        try {
            Gson gson = new Gson();

            Reader reader = Files.newBufferedReader(Paths.get("./config.json"));

            Config config = gson.fromJson(reader, Config.class);

            reader.close();

            AdminState.setServer(config.getServer());
            AdminState.setWebPage(config.getWebPage());

        } catch (Exception ex) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("Reading confing file failed");
            errorAlert.showAndWait();
            return;
        }

        HashMap<SceneName, String> scenes = new HashMap<>();
        scenes.put(SceneName.PALETTES, "/paletteTable.fxml");
        scenes.put(SceneName.PACKAGES, "/packagingTable.fxml");
        scenes.put(SceneName.PRODUCTS, "/productTable.fxml");
        scenes.put(SceneName.EMAILS, "/emailTable.fxml");
        scenes.put(SceneName.ADMINS, "/adminTable.fxml");
        scenes.put(SceneName.PALETTE_PRODUCT, "/paletteProductTable.fxml");
        scenes.put(SceneName.PACKAGING_PRODUCT, "/packagingProductTable.fxml");
        scenes.put(SceneName.WEIGHINGS, "/weighingTable.fxml");
        scenes.put(SceneName.SCAN_PAGE, "/scanPage.fxml");
        scenes.put(SceneName.PALETTE_PICK, "/palettePickPage.fxml");
        scenes.put(SceneName.PACKAGING_PICK, "/packPickPage.fxml");
        scenes.put(SceneName.CORRECT_WEIGHING, "/properWeighingSumPage.fxml");
        scenes.put(SceneName.WRONG_WEIGHING, "/wrongWeighingSumPage.fxml");
        scenes.put(SceneName.ADMIN_MAIN_MENU, "/adminMainMenuPage.fxml");
        scenes.put(SceneName.NOTIFICATIONS, "/notificationTable.fxml");
        scenes.put(SceneName.NOTIFICATION, "/notificationPage.fxml");
        scenes.put(SceneName.APP_MAIN_MENU, "/appMainMenuPage.fxml");
        scenes.put(SceneName.LOGIN, "/loginPage.fxml");

        SceneNavigator.initialize(primaryStage, scenes);
        SceneNavigator.setScene(SceneName.LOGIN);
        SceneNavigator.show();
    }
}