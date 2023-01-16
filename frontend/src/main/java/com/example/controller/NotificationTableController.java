package com.example.controller;

import com.example.model.Notification;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;
import com.example.utils.AdminState;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class NotificationTableController extends TableController implements Swappable {

    ObservableList<Notification> notifications;
    SceneName previousScene;

    @FXML
    private GridPane mainGrid;
    @FXML
    private TableView<Notification> tableView;
    @FXML
    private TableColumn<Notification, Integer> idColumn;
    @FXML
    private TableColumn<Notification, String> typeColumn;
    @FXML
    private TableColumn<Notification, String> actionColumn1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);

        notifications = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        actionColumn1.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        tableView.setItems(notifications);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void updateButtons() {

        Callback<TableColumn<Notification, String>, TableCell<Notification, String>> connectedFactory = new Callback<TableColumn<Notification, String>, TableCell<Notification, String>>() {
            @Override
            public TableCell<Notification, String> call(final TableColumn<Notification, String> param) {
                final TableCell<Notification, String> cell = new TableCell<Notification, String>() {
                    final Button btn = new Button("OTVORIT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Notification notification = getTableView().getItems().get(getIndex());
                                AdminState.setConnectedNotificationId(notification.getId());
                                notifications.clear();
                                SceneNavigator.setScene(SceneName.NOTIFICATION);
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
        AHClientHandler.getAHClientHandler().getPage("/notification", currentPage, pageSize, notifications, Notification.class, this);
    }

    @FXML
    public void exit() {
        notifications.clear();
        SceneNavigator.setScene(previousScene);
    }

    @FXML
    public void logOut() {AdminState.logOut();}

    @Override
    public void onLoad(SceneName previousSceneName) {

        if (previousSceneName != SceneName.NOTIFICATION && previousSceneName != SceneName.NOTIFICATIONS)
        {
            previousScene = previousSceneName;
        }
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void onUnload() {}
}
