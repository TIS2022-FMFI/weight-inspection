package com.example.controller;

import com.example.model.Palette;
import com.example.model.Page;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.NumericTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class PaletteTableController extends TableController implements Swappable {

    ObservableList<Palette> palettes;

    @FXML
    private GridPane mainGrid;
    @FXML
    private TableView<Palette> tableView;
    @FXML
    private TableColumn<Palette, Integer> idColumn;
    @FXML
    private TableColumn<Palette, String> nameColumn;
    @FXML
    private TableColumn<Palette, String> typeColumn;
    @FXML
    private TableColumn<Palette, String> weightColumn;
    @FXML
    private TableColumn<Palette, String> photoColumn;
    @FXML
    private TableColumn<Palette, String> actionColumn;
    @FXML
    private TableColumn<Palette, String> actionColumn2;
    @FXML
    private TableColumn<Palette, String> actionColumn3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        // Initializing admin panel
        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);

        // Initializing cells for table view
        palettes = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn2.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn3.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        editableCols();

        tableView.setItems(palettes);

    }

    private void editableCols() {
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue()));

        typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        typeColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setType(e.getNewValue()));

        weightColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        weightColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow())
                        .setWeight(Float.valueOf(NumericTextField.fortmatFloatText(e.getNewValue()))));

        tableView.setEditable(true);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void setButtons() {
        PaletteTableController self = this;
        Callback<TableColumn<Palette, String>, TableCell<Palette, String>> connectedFactory = new Callback<TableColumn<Palette, String>, TableCell<Palette, String>>() {
            @Override
            public TableCell<Palette, String> call(final TableColumn<Palette, String> param) {
                final TableCell<Palette, String> cell = new TableCell<Palette, String>() {
                    final Button btn = new Button("PRIPOJENÉ PRODUKTY");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Palette palette = getTableView().getItems().get(getIndex());
                                btn.setText(palette.getId()
                                        + ".   " + palette.getName());
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(connectedFactory);

        Callback<TableColumn<Palette, String>, TableCell<Palette, String>> updateFactory = new Callback<TableColumn<Palette, String>, TableCell<Palette, String>>() {
            @Override
            public TableCell<Palette, String> call(final TableColumn<Palette, String> param) {
                final TableCell<Palette, String> cell = new TableCell<Palette, String>() {
                    final Button btn = new Button("OBNOVIT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Palette palette = getTableView().getItems().get(getIndex());
                                palette.put(self);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn2.setCellFactory(updateFactory);

        Callback<TableColumn<Palette, String>, TableCell<Palette, String>> deleteFactory = new Callback<TableColumn<Palette, String>, TableCell<Palette, String>>() {
            @Override
            public TableCell<Palette, String> call(final TableColumn<Palette, String> param) {
                final TableCell<Palette, String> cell = new TableCell<Palette, String>() {
                    final Button btn = new Button("VYMAZAŤ");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Palette palette = getTableView().getItems().get(getIndex());
                                palette.delete(self);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn3.setCellFactory(deleteFactory);
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
        AHClientHandler.getAHClientHandler().getPage("/palette", currentPage, pageSize, palettes,
                Palette.class, this);

    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        updateTable();
    }

    @Override
    public void onUnload() {

    }

}
