package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Item;
import com.seekerscloud.pos.view.tm.ItemTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ItemFormController {

    public TableView<ItemTM> tblItem;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colUnitPrice;
    public TableColumn colQtyOnHand;
    public TableColumn colOption;
    public AnchorPane itemContext;
    public TextField txtItemCode;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public JFXButton btnSaveUpdateItem;
    public TextField txtItemSearch;

    public String  searchText  = "";

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        searchItem(searchText);

        tblItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(null != newValue){
                setData(newValue);
            }
        });
        txtItemSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue;
            searchItem(searchText);
        });
    }

    private void setData(ItemTM item) {
        txtItemCode.setText(item.getCode());
        txtDescription.setText(item.getDescription());
        txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
        txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        btnSaveUpdateItem.setText("Update Item");
    }

    private void searchItem(String text) {
        ObservableList<ItemTM> obList = FXCollections.observableArrayList();
        for(Item item : Database.itemTable){
            if(item.getDescription().contains(text)) {
                Button btn = new Button("delete");
                obList.add(new ItemTM(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand(), btn));
                btn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this Item",
                            ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> val = alert.showAndWait();
                    if (val.get() == ButtonType.YES) {
                        boolean isDeleted = Database.itemTable.remove(item);
                        if (isDeleted) {
                            new Alert(Alert.AlertType.INFORMATION, "Item Delete Successfully").show();
                            searchItem(searchText);
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Item Delete Successfully").show();
                        }
                    }
                });
            }
        }
        tblItem.setItems(obList);
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUI("DashboardForm","Dashboard");
    }

    public void addNewItemOnAction(ActionEvent actionEvent) {
        clearFields();
    }

    public void saveItemOnAction(ActionEvent actionEvent) {
        Item item  = new Item(txtItemCode.getText(),txtDescription.getText(),
                Double.parseDouble(txtUnitPrice.getText()), Integer.parseInt(txtQtyOnHand.getText()));
        if(btnSaveUpdateItem.getText().equalsIgnoreCase("save item")){
            boolean isSaved = Database.itemTable.add(item);
            if(isSaved){
                new Alert(Alert.AlertType.INFORMATION, "Item Saved Successfully ").show();
                searchItem(searchText);
                clearFields();
            }else {
                new Alert(Alert.AlertType.ERROR, "Try Again.").show();
            }

        }else{
            // update
            for(Item i : Database.itemTable){
                if(txtItemCode.getText().equalsIgnoreCase(i.getCode())){
                    i.setDescription(txtDescription.getText());
                    i.setUnitPrice(Double.parseDouble(txtUnitPrice.getText()));
                    i.setQtyOnHand(Integer.parseInt(txtQtyOnHand.getText()));
                    new Alert(Alert.AlertType.INFORMATION, "Item have been update successfully.").show();
                    searchItem(searchText);
                    clearFields();
                }
            }
        }

    }
    private void clearFields(){
        txtItemCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtItemSearch.clear();
        btnSaveUpdateItem.setText("Save Item");
    }

    private void setUI(String location, String title) throws IOException {
        Stage stage = (Stage) itemContext.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene( new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }
}
