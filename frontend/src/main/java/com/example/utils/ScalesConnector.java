package com.example.utils;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;

public class ScalesConnector {
    public static float getWeight() {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Please input the weight from the scales.");
        dialog.setContentText("This message will be replaced with direct communication with scales when it is done.");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String weightStr = TextFieldFilters.formatTextToFloat(result.get());
            if (weightStr.isEmpty()) {
                return getWeight();
            }
            return Float.valueOf(weightStr);
        }
        return getWeight();
    }
}
