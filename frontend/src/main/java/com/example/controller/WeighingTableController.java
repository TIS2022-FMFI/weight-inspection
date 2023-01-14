package com.example.controller;

import com.example.model.Weighing;
import com.example.scene.SceneName;
import com.example.utils.AHClientHandler;
import com.example.utils.TextFieldFilters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class WeighingTableController extends TableController implements Swappable {

    ObservableList<Weighing> weighings;

    @FXML
    private GridPane mainGrid;
    @FXML
    private TableView<Weighing> tableView;
    @FXML
    private TableColumn<Weighing, Integer> idColumn;
    @FXML
    private TableColumn<Weighing, String> idpColumn;
    @FXML
    private TableColumn<Weighing, String> weightColumn;
    @FXML
    private TableColumn<Weighing, Integer> quantityColumn;
    @FXML
    private TableColumn<Weighing, Timestamp> weighedOnColumn;
    @FXML
    private TableColumn<Weighing, Boolean> isCorrectColumn;
    @FXML
    private TableColumn<Weighing, String> packagingNameColumn;
    @FXML
    private TableColumn<Weighing, String> paletteNameColumn;
    @FXML
    private TableColumn<Weighing, String> referenceColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);

        weighings = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idpColumn.setCellValueFactory(new PropertyValueFactory<>("IDP"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        weighedOnColumn.setCellValueFactory(new PropertyValueFactory<>("weighedOn"));
        isCorrectColumn.setCellValueFactory(new PropertyValueFactory<>("isCorrect"));
        packagingNameColumn.setCellValueFactory(new PropertyValueFactory<>("packagingName"));
        paletteNameColumn.setCellValueFactory(new PropertyValueFactory<>("paletteName"));
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));

        tableView.setItems(weighings);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void updateButtons() {
        WeighingTableController self = this;
    }

    @FXML
    @Override
    public void updateTable() {
        String strPageSize = pageSizeBox.getValue();
        if (strPageSize == null) {
            strPageSize = "10";
        }
        int pageSize = Integer.valueOf(strPageSize);

        int currentPage = 0;
        if (pagination != null) {
            currentPage = pagination.getCurrentPageIndex();
        }
        AHClientHandler.getAHClientHandler().getPage("/weighing", currentPage, pageSize, weighings, Weighing.class, this);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        updateTable();
    }

    @Override
    public void onUnload() {}
}