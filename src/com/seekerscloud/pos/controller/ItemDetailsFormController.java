package com.seekerscloud.pos.controller;

import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.ItemDetails;
import com.seekerscloud.pos.modal.Order;
import com.seekerscloud.pos.view.tm.ItemDetailsTM;
import com.seekerscloud.pos.view.tm.OrderTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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

        try{
            String sql = "SELECT o.orderId, d.itemCode,d.orderId, d.unitPrice,d.qty FROM  `order` o " +
                    "INNER  join order_details d ON o.orderId = d.orderId AND o.orderId=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,id);
            ResultSet set= statement.executeQuery();

            ObservableList<ItemDetailsTM> obList = FXCollections.observableArrayList();
            while (set.next()) {
                double tempUnitPrice = set.getDouble(4);
                int tempQtyOnHand = set.getInt(5);
                double tempTotal = tempQtyOnHand *tempUnitPrice;
                obList.add(new ItemDetailsTM(set.getString(2),tempUnitPrice,tempQtyOnHand,tempTotal));

            }
            tblItemDetails.setItems(obList);

        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
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
