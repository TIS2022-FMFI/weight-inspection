package com.example.controller;

import com.example.model.Admin;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.AdminState;
import com.example.utils.TextFieldFilters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminTableController extends TableController implements Swappable {

    ObservableList<Admin> admins;

    @FXML
    private GridPane mainGrid;
    @FXML
    private TableView<Admin> tableView;
    @FXML
    private TableColumn<Admin, Integer> idColumn;
    @FXML
    private TableColumn<Admin, String> emailIdColumn;
    @FXML
    private TableColumn<Admin, String> firstNameColumn;
    @FXML
    private TableColumn<Admin, String> lastNameColumn;
    @FXML
    private TableColumn<Admin, String> userNameColumn;
    @FXML
    private TableColumn<Admin, String> passWordColumn;
    @FXML
    private TableColumn<Admin, String> actionColumn1;
    @FXML
    private TableColumn<Admin, String> actionColumn2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);

        admins = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailIdColumn.setCellValueFactory(new PropertyValueFactory<>("emailId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passWordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        actionColumn1.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn2.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        editableCols();
        tableView.setItems(admins);
    }

    private void editableCols() {
        emailIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailIdColumn.setOnEditCommit(e -> {
            String newId = TextFieldFilters.formatTextToInt(e.getNewValue());
            if (newId.isEmpty()) {
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setEmailId(null);
            } else {
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setEmailId(Integer.valueOf(newId));
            }
        });

        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setFirstName(e.getNewValue()));

        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setLastName(e.getNewValue()));

        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setUsername(e.getNewValue()));

        passWordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passWordColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setPassword(e.getNewValue()));

        tableView.setEditable(true);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void updateButtons() {

        AdminTableController self = this;

        Callback<TableColumn<Admin, String>, TableCell<Admin, String>> updateFactory = new Callback<TableColumn<Admin, String>, TableCell<Admin, String>>() {
            @Override
            public TableCell<Admin, String> call(final TableColumn<Admin, String> param) {
                final TableCell<Admin, String> cell = new TableCell<Admin, String>() {
                    final Button btn = new Button("ULOZIT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Admin admin = getTableView().getItems().get(getIndex());
                                admin.put(self);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn1.setCellFactory(updateFactory);

        Callback<TableColumn<Admin, String>, TableCell<Admin, String>> deleteFactory = new Callback<TableColumn<Admin, String>, TableCell<Admin, String>>() {
            @Override
            public TableCell<Admin, String> call(final TableColumn<Admin, String> param) {
                final TableCell<Admin, String> cell = new TableCell<Admin, String>() {
                    final Button btn = new Button("VYMAZAT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Admin admin = getTableView().getItems().get(getIndex());
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation Dialog");
                                alert.setHeaderText("Are you sure you want to delete this item?");

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK) {
                                    admin.delete(self);
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

        actionColumn2.setCellFactory(deleteFactory);
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
        AHClientHandler.getAHClientHandler().getPage("/admin", currentPage, pageSize, admins, Admin.class, this);
    }

    @FXML
    public void createNew() {
        Admin newAdmin = new Admin();
        newAdmin.post(this);
        pagination.setCurrentPageIndex(0);
    }

    @FXML
    public void back() {
        admins.clear();
        SceneNavigator.setScene(SceneName.ADMIN_MAIN_MENU);
    }

    @FXML
    public void logOut() {
        AdminState.logOut();
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        updateTable();
    }

    @Override
    public void onUnload() {
    }
}
