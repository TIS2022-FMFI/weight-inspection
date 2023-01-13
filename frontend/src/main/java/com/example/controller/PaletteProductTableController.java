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

public class PaletteProductTableController extends TableController implements Swappable {

    ObservableList<Product> products;

    private Integer paletteId;

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
        actionColumn1.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        editableCols();
        tableView.setItems(products);
    }

    private void editableCols() {
        referenceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        referenceColumn.setOnEditCommit(
                e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setReference(e.getNewValue()));

        tableView.setEditable(true);
    }

    @FXML
    public void onPagingSizeUpdate() {
        pagination.setCurrentPageIndex(0);
        updateTable();
    }

    @Override
    public void updateButtons() {

        PaletteProductTableController self = this;

        Callback<TableColumn<Product, String>, TableCell<Product, String>> updateFactory = new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
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
                                    product.deleteForPalette(self, paletteId);
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

        actionColumn1.setCellFactory(updateFactory);
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
        AHClientHandler.getAHClientHandler().getPage("/palette/" + paletteId + "/product", currentPage, pageSize,
                products, Product.class, this);
    }

    @FXML
    public void createNew() {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Write an id of a product (product with that id has to exist).");
        dialog.setContentText("Please enter id:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            int conn_id = Integer.valueOf(TextFieldFilters.formatTextToInt(result.get()));
            Product newProduct = new Product();
            newProduct.setId(conn_id);
            newProduct.postForPalette(this, paletteId);
            pagination.setCurrentPageIndex(0);
        }
    }

    @FXML
    public void back() {
        AdminState.setConnectedPaletteId(null);
        idLabel.setText("");
        products.clear();
        SceneNavigator.setScene(SceneName.PALETTES);
    }

    @Override
    public void onLoad(SceneName previousSceneName) {
        if (AdminState.getConnectedPaletteId() == null) {
            SceneNavigator.setScene(previousSceneName);
        }
        paletteId = AdminState.getConnectedPaletteId();
        idLabel.setText(paletteId.toString());
        updateTable();
    }

    @Override
    public void onUnload() {
    }
}
