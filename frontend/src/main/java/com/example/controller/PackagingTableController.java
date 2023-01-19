package com.example.controller;

import com.example.model.Packaging;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.AdminState;
import com.example.utils.TextFieldFilters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PackagingTableController extends TableController implements Swappable {

    ObservableList<Packaging> packages;

    @FXML
    private GridPane mainGrid;
    @FXML
    private TableView<Packaging> tableView;
    @FXML
    private TableColumn<Packaging, Integer> idColumn;
    @FXML
    private TableColumn<Packaging, String> nameColumn;
    @FXML
    private TableColumn<Packaging, String> typeColumn;
    @FXML
    private TableColumn<Packaging, String> weightColumn;
    @FXML
    private TableColumn<Packaging, String> photoColumn;
    @FXML
    private TableColumn<Packaging, String> actionColumn1;
    @FXML
    private TableColumn<Packaging, String> actionColumn2;
    @FXML
    private TableColumn<Packaging, String> actionColumn3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);

        packages = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn1.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn2.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn3.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        editableCols();
        tableView.setItems(packages);
    }

    private void editableCols() {
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue()));

        typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        typeColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setType(e.getNewValue()));

        weightColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        weightColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow())
                .setWeight(Float.valueOf(TextFieldFilters.formatTextToFloat(e.getNewValue()))));

        tableView.setEditable(true);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void updateButtons() {

        PackagingTableController self = this;

        Callback<TableColumn<Packaging, String>, TableCell<Packaging, String>> connectedFactory = new Callback<TableColumn<Packaging, String>, TableCell<Packaging, String>>() {
            @Override
            public TableCell<Packaging, String> call(final TableColumn<Packaging, String> param) {
                final TableCell<Packaging, String> cell = new TableCell<Packaging, String>() {
                    final Button btn = new Button("PRIPOJENE PRODUKTY");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Packaging packaging = getTableView().getItems().get(getIndex());
                                try {
                                    AdminState.setConnectedPackagingId(Integer.valueOf(packaging.getId()));
                                    packages.clear();
                                    SceneNavigator.setScene(SceneName.PACKAGING_PRODUCT);
                                } catch (NumberFormatException e) {
                                }
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn1.setCellFactory(connectedFactory);

        Callback<TableColumn<Packaging, String>, TableCell<Packaging, String>> updateFactory = new Callback<TableColumn<Packaging, String>, TableCell<Packaging, String>>() {
            @Override
            public TableCell<Packaging, String> call(final TableColumn<Packaging, String> param) {
                final TableCell<Packaging, String> cell = new TableCell<Packaging, String>() {
                    final Button btn = new Button("ULOZIT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Packaging packaging = getTableView().getItems().get(getIndex());
                                packaging.put(self);
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

        Callback<TableColumn<Packaging, String>, TableCell<Packaging, String>> deleteFactory = new Callback<TableColumn<Packaging, String>, TableCell<Packaging, String>>() {
            @Override
            public TableCell<Packaging, String> call(final TableColumn<Packaging, String> param) {
                final TableCell<Packaging, String> cell = new TableCell<Packaging, String>() {
                    final Button btn = new Button("VYMAZAT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Packaging packaging = getTableView().getItems().get(getIndex());
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation Dialog");
                                alert.setHeaderText("Are you sure you want to delete this item?");

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK) {
                                    packaging.delete(self);
                                }
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
        AHClientHandler.getAHClientHandler().getPage("/packaging", currentPage, pageSize, packages, Packaging.class,
                this);
    }

    @FXML
    public void createNew() {
        Packaging newPackaging = new Packaging();
        newPackaging.post(this);
        pagination.setCurrentPageIndex(0);
    }

    @FXML
    public void back() {
        packages.clear();
        SceneNavigator.setScene(SceneName.ADMIN_MAIN_MENU);
    }

    @FXML
    public void logOut() {
        AdminState.logOut();
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void onUnload() {
    }
}