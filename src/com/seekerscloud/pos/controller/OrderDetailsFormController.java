package com.seekerscloud.pos.controller;

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
        ObservableList<OrderTM> obList = FXCollections.observableArrayList();
        for(Order o : Database.orderTable){
            Button btn= new Button("View More");
            obList.add(new OrderTM(o.getOrderId(),"",o.getDate(),o.getTotalCost(),btn));
            btn.setOnAction(e->{
                try {
                FXMLLoader loader =  new FXMLLoader(getClass().getResource("../view/ItemDetailsForm.fxml"));
                    Parent parent = loader.load();
                    ItemDetailsFormController controller = loader.getController();
                    controller.loadOrders(o.getOrderId());
                    Stage stage = new Stage();
                    stage.setScene(new Scene(parent));
                    stage.show();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        tblOrderDetails.setItems(obList);
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
