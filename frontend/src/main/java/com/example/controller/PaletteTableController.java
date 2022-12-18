package com.example.controller;

import com.example.model.Palette;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;
import com.example.utils.AHClientHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class PaletteTableController implements Initializable, Swappable {

    ObservableList<Palette> palettes;
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
        palettes = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn2.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn3.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Palette, String>, TableCell<Palette, String>> cellFactory = new Callback<TableColumn<Palette, String>, TableCell<Palette, String>>() {
            @Override
            public TableCell<Palette, String> call(final TableColumn<Palette, String> param) {
                final TableCell<Palette, String> cell = new TableCell<Palette, String>() {
                    final Button btn = new Button("Just Do It");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Palette commentModel = getTableView().getItems().get(getIndex());
                                btn.setText(commentModel.getId()
                                        + ".   " + commentModel.getName());
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(cellFactory);

        tableView.setItems(palettes);

        AHClientHandler.getAHClientHandler().getPage("/palette", 0, 10, palettes);
    }

    @FXML
    public void onOtherPageClicked() {
        SceneNavigator.setScene(SceneName.LAST);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {

    }

    @Override
    public void onUnload() {

    }

}
