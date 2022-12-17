package com.example.controller;

import com.example.model.CommentModel;
import com.example.repository.CommentRepository;
import com.example.scene.SceneName;
import com.example.scene.SceneNavigator;

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

public class FirstController implements Initializable, Swappable {

    CommentRepository commentRepository = new CommentRepository();

    @FXML
    private TableView<CommentModel> tableView;
    @FXML
    private TableColumn<CommentModel, Integer> idColumn;
    @FXML
    private TableColumn<CommentModel, String> nameColumn;
    @FXML
    private TableColumn<CommentModel, String> emailColumn;
    @FXML
    private TableColumn<CommentModel, String> bodyColumn;
    @FXML
    private TableColumn<CommentModel, String> actionColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialized first");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        bodyColumn.setCellValueFactory(new PropertyValueFactory<>("body"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<CommentModel, String>, TableCell<CommentModel, String>> cellFactory = new Callback<TableColumn<CommentModel, String>, TableCell<CommentModel, String>>() {
            @Override
            public TableCell call(final TableColumn<CommentModel, String> param) {
                final TableCell<CommentModel, String> cell = new TableCell<CommentModel, String>() {
                    final Button btn = new Button("Just Do It");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                CommentModel commentModel = getTableView().getItems().get(getIndex());
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

        tableView.setItems(commentRepository.getComments());
        commentRepository.updateComments();
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
