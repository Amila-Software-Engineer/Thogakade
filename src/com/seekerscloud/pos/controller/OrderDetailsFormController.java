package com.seekerscloud.pos.controller;

import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.ItemDetails;
import com.seekerscloud.pos.modal.Order;
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

public class OrderDetailsFormController {
    public TableColumn colOrderId;
    public TableColumn colCustomer;
    public TableColumn colDate;
    public TableColumn colTotal;
    public TableColumn colOption;
    public AnchorPane orderDetailsContext;
    public TableView<OrderTM> tblOrderDetails;

    public void initialize(){
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        loadOrders();
    }

    private void loadOrders() {
        try{
            String sql = "SELECT * FROM  `order`";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set= statement.executeQuery();

            ObservableList<OrderTM> obList = FXCollections.observableArrayList();
            while (set.next()){
                Button btn= new Button("View More");
                OrderTM tm = new OrderTM(set.getString(1),set.getString(4),new Date(),set.getDouble(3),btn);
                obList.add(tm);
                btn.setOnAction(e->{
                    try {
                        FXMLLoader loader =  new FXMLLoader(getClass().getResource("../view/ItemDetailsForm.fxml"));
                        Parent parent = loader.load();
                        ItemDetailsFormController controller = loader.getController();
                        controller.loadOrders(tm.getOrderId());
                        Stage stage = new Stage();
                        stage.setScene(new Scene(parent));
                        stage.show();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        tblOrderDetails.setItems(obList);

        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
        }

    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUI("DashboardForm","Dashboard");
    }
    private void setUI(String location, String title) throws IOException {
        Stage stage = (Stage) orderDetailsContext.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene( new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }
}
