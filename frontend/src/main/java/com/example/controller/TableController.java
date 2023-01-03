package com.example.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;

public abstract class TableController implements Initializable {

    @FXML
    protected ComboBox<String> pageSizeBox;
    @FXML
    protected Pagination pagination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initializing combobox
        ObservableList<String> observablePageSizes = FXCollections.observableArrayList();
        ArrayList<String> pageSizes = new ArrayList<String>(Arrays.asList("10", "20", "30", "40", "50"));
        observablePageSizes.addAll(pageSizes);
        pageSizeBox.setItems(observablePageSizes);

        pagination.setPageFactory((pageIndex) -> {
            updateTable();
            return new Label();
        });
    }

    public void updateTable() {
    }

    public void updateButtons() {
    }

    public void setPaging(int totalPages, int page) {
        if (pagination.getCurrentPageIndex() != page) {
            pagination.setCurrentPageIndex(page);
        }
        if (pagination.getPageCount() != totalPages) {
            pagination.setPageCount(totalPages);
        }
    }

}
