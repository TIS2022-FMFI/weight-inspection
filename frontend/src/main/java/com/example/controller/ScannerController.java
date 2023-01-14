package com.example.controller;

import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;

public abstract class ScannerController implements Initializable {

    private final StringBuffer barcode = new StringBuffer();
    private long lastEventTimeStamp = 0L;
    private long threshold = 50L;
    private int minBarcodeLength = 4;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public abstract void onBarcodeScanned(String barcode);

    @FXML
    public void keyTyped(KeyEvent keyEvent) {
        long now = Instant.now().toEpochMilli();

        if (now - this.lastEventTimeStamp > this.threshold) {
            barcode.delete(0, barcode.length());
        }
        this.lastEventTimeStamp = now;

        // 0x0020 is space
        if (keyEvent.getCharacter().getBytes()[0] == (byte) 9) {
            if (barcode.length() >= this.minBarcodeLength) {
                onBarcodeScanned(barcode.toString().trim());
            }
            barcode.delete(0, barcode.length());
        } else {
            barcode.append(keyEvent.getCharacter());
        }
        keyEvent.consume();
    }
}
