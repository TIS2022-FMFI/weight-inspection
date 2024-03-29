package com.example.scene;

import com.example.controller.Swappable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneNavigator {
    private static final HashMap<SceneName, Swappable> controllers = new HashMap<>();
    private static final HashMap<SceneName, Scene> scenes = new HashMap<>();

    private static SceneName currentSceneName;
    private static Stage primaryStage;

    private static boolean scenesLoaded = false;

    /**
     * Lazy loads all scenes, and sets the current scene to firstSceneName.
     * 
     * @param primaryStage The primary stage
     */
    public static void initialize(Stage primaryStage, Map<SceneName, String> scenes) {
        if (scenesLoaded) {
            return;
        }
        SceneNavigator.primaryStage = primaryStage;
        loadAllScenes(scenes);
        scenesLoaded = true; // Can't use set setScene() until scene loaded flag is set to true
    }

    /**
     * Loads all scenes used by this application.
     * 
     * @param scenes Scenes used by this application
     */
    private static void loadAllScenes(Map<SceneName, String> scenes) {
        for (Map.Entry<SceneName, String> set : scenes.entrySet()) {
            loadScene(set.getKey(), set.getValue());
        }
    }

    private static void loadScene(SceneName sceneName, String fxmlResource) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlResource));
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            scenes.put(sceneName, scene);
            controllers.put(sceneName, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the next scene specified by nextSceneName.
     * 
     * @param nextSceneName The name of the next scene.
     */
    public static void setScene(SceneName nextSceneName) {
        if (!scenesLoaded) {
            return;
        }

        double width = 1300;
        double height = 800;

        if (currentSceneName != null) {
            width = scenes.get(currentSceneName).getWindow().getWidth();
            height = scenes.get(currentSceneName).getWindow().getHeight();

        }

        primaryStage.setScene(scenes.get(nextSceneName));

        Swappable nextController = controllers.get(nextSceneName);
        nextController.onLoad((currentSceneName == null) ? nextSceneName : currentSceneName);
        scenes.get(nextSceneName).getWindow().setWidth(width);
        scenes.get(nextSceneName).getWindow().setHeight(height);

        currentSceneName = nextSceneName;
    }

    /**
     * Shows the current scene
     */
    public static void show() {
        if (scenesLoaded && currentSceneName != null) {
            primaryStage.show();
        }
    }

    public static Stage getStage() {
        return primaryStage;
    }
}
