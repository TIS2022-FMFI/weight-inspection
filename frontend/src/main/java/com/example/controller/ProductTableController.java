package com.example.controller;

import com.example.model.Product;
import com.example.scene.SceneName;
import com.example.utils.AHClientHandler;

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
import java.util.ResourceBundle;

public class ProductTableController extends TableController implements Swappable {

    ObservableList<Product> products;

    @FXML
    private GridPane mainGrid;
    @FXML
    private TableView<Product> tableView;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> referenceColumn;
    @FXML
    private TableColumn<Product, String> weightColumn;
    @FXML
    private TableColumn<Product, String> actionColumn1;
    @FXML
    private TableColumn<Product, String> actionColumn2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);

        products = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        actionColumn1.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn2.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        editableCols();
        tableView.setItems(products);
    }

    private void editableCols() {
        referenceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        referenceColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setReference(e.getNewValue()));

        tableView.setEditable(true);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void updateButtons() {

        ProductTableController self = this;

        Callback<TableColumn<Product, String>, TableCell<Product, String>> updateFactory = new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
            @Override
            public TableCell<Product, String> call(final TableColumn<Product, String> param) {
                final TableCell<Product, String> cell = new TableCell<Product, String>() {
                    final Button btn = new Button("ULOZIT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Product product = getTableView().getItems().get(getIndex());
                                product.put(self);
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

        Callback<TableColumn<Product, String>, TableCell<Product, String>> deleteFactory = new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
            @Override
            public TableCell<Product, String> call(final TableColumn<Product, String> param) {
                final TableCell<Product, String> cell = new TableCell<Product, String>() {
                    final Button btn = new Button("VYMAZAT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Product product = getTableView().getItems().get(getIndex());
                                product.delete(self);
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
        AHClientHandler.getAHClientHandler().getPage("/product", currentPage, pageSize, products, Product.class, this);
    }

    @FXML
    public void createNew() {
        Product newProduct = new Product();
        newProduct.post(this);
        pagination.setCurrentPageIndex(0);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        updateTable();
    }

    @Override
    public void onUnload() {}
}