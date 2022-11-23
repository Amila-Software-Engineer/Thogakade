package com.seekerscloud.pos.controller;

import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.ItemDetails;
import com.seekerscloud.pos.modal.Order;
import com.seekerscloud.pos.view.tm.ItemDetailsTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemDetailsFormController {
    public AnchorPane itemDetailsContext;
    public TableView tblItemDetails;
    public TableColumn colItemCode;
    public TableColumn colUnitPrice;
    public TableColumn colQty;
    public TableColumn colTotal;

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

    }

    public void loadOrders(String id) {
        for(Order o : Database.orderTable){
            if(o.getOrderId().equals(id)){
                ObservableList<ItemDetailsTM> obList = FXCollections.observableArrayList();
                for(ItemDetails d: o.getItemDetails()){
                    double tempUnitPrice = d.getUnitPrice();
                    int tempQtyOnHand = d.getQty();
                    double tempTotal = tempQtyOnHand *tempUnitPrice;
                    obList.add(new ItemDetailsTM(d.getItemCode(),d.getUnitPrice(),d.getQty(),tempTotal));
                }
                tblItemDetails.setItems(obList);
                return;
            }

        }
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUI("DashboardForm","Dashboard");
    }
    private void setUI(String location, String title) throws IOException {
        Stage stage = (Stage) itemDetailsContext.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene( new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }
}
