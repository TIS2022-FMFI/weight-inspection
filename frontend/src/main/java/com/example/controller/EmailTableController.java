package com.example.controller;

import com.example.model.Email;
import com.example.scene.SceneName;
import com.example.utils.AHClientHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class EmailTableController extends TableController implements Swappable {

    ObservableList<Email> emails;

    @FXML
    private GridPane mainGrid;
    @FXML
    private TableView<Email> tableView;
    @FXML
    private TableColumn<Email, Integer> idColumn;
    @FXML
    private TableColumn<Email, String> emailColumn;
    @FXML
    private TableColumn<Email, String> sendExportsColumn;
    @FXML
    private TableColumn<Email, String> actionColumn1;
    @FXML
    private TableColumn<Email, String> actionColumn2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);

        emails = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        sendExportsColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn1.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn2.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        editableCols();
        tableView.setItems(emails);
    }

    private void editableCols() {
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setEmail(e.getNewValue()));

        tableView.setEditable(true);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void updateButtons() {

        EmailTableController self = this;

        Callback<TableColumn<Email, String>, TableCell<Email, String>> updateFactory = new Callback<TableColumn<Email, String>, TableCell<Email, String>>() {
            @Override
            public TableCell<Email, String> call(final TableColumn<Email, String> param) {
                final TableCell<Email, String> cell = new TableCell<Email, String>() {
                    final Button btn = new Button("ULOZIT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Email email = getTableView().getItems().get(getIndex());
                                email.put(self);
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

        Callback<TableColumn<Email, String>, TableCell<Email, String>> deleteFactory = new Callback<TableColumn<Email, String>, TableCell<Email, String>>() {
            @Override
            public TableCell<Email, String> call(final TableColumn<Email, String> param) {
                final TableCell<Email, String> cell = new TableCell<Email, String>() {
                    final Button btn = new Button("VYMAZAT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Email email = getTableView().getItems().get(getIndex());
                                email.delete(self);
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
        Callback<TableColumn<Email, String>, TableCell<Email, String>> exportsFactory = new Callback<TableColumn<Email, String>, TableCell<Email, String>>() {
            @Override
            public TableCell<Email, String> call(final TableColumn<Email, String> param) {
                final TableCell<Email, String> cell = new TableCell<Email, String>() {
                    final CheckBox chkbx = new CheckBox();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            chkbx.setOnAction(event -> {
                                System.out.println("hi");
                                Email email = getTableView().getItems().get(getIndex());
                                email.setSendExports(chkbx.isSelected());
                            });
                            chkbx.setSelected(getTableView().getItems().get(getIndex()).getSendExports());
                            setGraphic(chkbx);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        sendExportsColumn.setCellFactory(exportsFactory);
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
        AHClientHandler.getAHClientHandler().getPage("/email", currentPage, pageSize, emails, Email.class, this);
    }

    @FXML
    public void createNew() {
        Email newEmail = new Email();
        newEmail.post(this);
        pagination.setCurrentPageIndex(0);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        updateTable();
    }

    @Override
    public void onUnload() {
    }
}
