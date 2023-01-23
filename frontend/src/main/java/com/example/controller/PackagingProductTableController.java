package com.example.controller;

import com.example.model.Product;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PackagingProductTableController extends TableController implements Swappable {

    ObservableList<Product> products;

    private Integer packagingId;

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
    private TableColumn<Product, String> numberOfPiecesColumn;
    @FXML
    private TableColumn<Product, String> toleranceColumn;
    @FXML
    private TableColumn<Product, String> actionColumn1;
    @FXML
    private TableColumn<Product, String> actionColumn2;
    @FXML
    private Label idLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        AdminPanelController adminPanel = new AdminPanelController();
        mainGrid.getChildren().add(adminPanel);

        products = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        numberOfPiecesColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        toleranceColumn.setCellValueFactory(new PropertyValueFactory<>("tolerance"));
        actionColumn1.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionColumn2.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        editableCols();
        tableView.setItems(products);
    }

    private void editableCols() {
        numberOfPiecesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        numberOfPiecesColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow())
                .setQuantity(Integer.valueOf(TextFieldFilters.formatTextToInt(e.getNewValue()))));

        toleranceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        toleranceColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow())
                .setTolerance(Float.valueOf(TextFieldFilters.formatTextToFloat(e.getNewValue()))));

        tableView.setEditable(true);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void updateButtons() {

        PackagingProductTableController self = this;

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
                                product.putForPackaging(self, packagingId);
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
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation Dialog");
                                alert.setHeaderText("Are you sure you want to delete this item?");

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK) {
                                    product.deleteForPackaging(self, packagingId);
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
        AHClientHandler.getAHClientHandler().getPage("/packaging/" + packagingId + "/product", currentPage, pageSize,
                products, Product.class, this);
    }

    @FXML
    public void createNew() {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Write an reference of a product (product with that reference has to exist).");
        dialog.setContentText("Please enter reference:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String conn_id = result.get();
            Product newProduct = new Product();
            newProduct.setReference(conn_id);
            newProduct.putForPackaging(this, packagingId);
            pagination.setCurrentPageIndex(0);
        }
    }

    @FXML
    public void back() {
        AdminState.setConnectedPackagingId(null);
        idLabel.setText("");
        products.clear();
        SceneNavigator.setScene(SceneName.PACKAGES);
    }

    @FXML
    public void logOut() {
        AdminState.logOut();
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        if (AdminState.getConnectedPackagingId() == null) {
            SceneNavigator.setScene(previousSceneName);
        }
        packagingId = AdminState.getConnectedPackagingId();
        idLabel.setText(packagingId.toString());
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void onUnload() {
    }
}