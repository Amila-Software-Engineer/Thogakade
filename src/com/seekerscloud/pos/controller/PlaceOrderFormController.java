package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.modal.Item;
import com.seekerscloud.pos.modal.ItemDetails;
import com.seekerscloud.pos.modal.Order;
import com.seekerscloud.pos.view.tm.CartTM;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class PlaceOrderFormController {
    public AnchorPane placeOrderContext;
    public TextField txtCustomerName;
    public TextField txtCustomerAddress;
    public TextField txtCustomerSalary;
    public JFXButton btnSaveUpdateItem;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtOrderId;
    public TextField txtOrderDate;
    public ComboBox<String> cmbCustomer;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colUnitPrice;
    public TableColumn colQty;
    public TableColumn colTotal;
    public TableColumn colOption;
    public ComboBox<String> cmbItemCode;
    public JFXButton placeOrder;
    public JFXButton addToCart;
    public TextField txtQty;
    public TableView<CartTM> tblCart;
    public Label lblTotal;

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        setDateAndOrderId();
        loadAllCustomerId();
        loadAllItemCodes();

        cmbCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(null != newValue){
                setCustomerDetails(newValue);
            }
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(null != newValue){
                setItemDetails(newValue);
            }
        });
    }

    private void setOrderId(){
        if(Database.orderTable.isEmpty()){
            txtOrderId.setText("D-1");
            return;
        }
        String tempOrderId = Database.orderTable.get(Database.orderTable.size()-1).getOrderId();
        String[] array = tempOrderId.split("-");
        int tempNumber =  Integer.parseInt(array[1]);
        int finalizeOrderId = tempNumber+1;
        txtOrderId.setText("D-"+ finalizeOrderId);
    }
    private void setItemDetails(String newValue) {
        for(Item item: Database.itemTable){
            if(item.getCode().equals(newValue)){
                txtDescription.setText(item.getDescription());
                txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
            }
        }
    }


    private void setCustomerDetails(String newValue) {
        for(Customer c: Database.customerTable){
            if(c.getId().equals(newValue)){
                txtCustomerName.setText(c.getName());
                txtCustomerAddress.setText(c.getAddress());
                txtCustomerSalary.setText(String.valueOf(c.getSalary()));
            }
        }
    }

    private void loadAllItemCodes() {
        for(Item item: Database.itemTable){
            cmbItemCode.getItems().add(item.getCode());
        }
    }

    private void loadAllCustomerId() {
       for(Customer c: Database.customerTable){
        cmbCustomer.getItems().add(c.getId());
       }
    }

    private void setDateAndOrderId() {
//        Date date = new Date();
//        SimpleDateFormat df =  new SimpleDateFormat("YYYY-MM-DD");
//        txtOrderDate.setText(df.format(date));
        txtOrderDate.setText(new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        setOrderId();
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUI("DashboardForm","Dashboard");
    }

    public void saveItemOnAction(ActionEvent actionEvent) {
    }

    private void setUI(String location, String title) throws IOException {
        Stage stage = (Stage) placeOrderContext.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene( new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }

    public void placeOrderOnAction(ActionEvent actionEvent) {
        if(obList.isEmpty())return;
        ArrayList<ItemDetails> details =new ArrayList<>();
        for(CartTM tm: obList){
            details.add(new ItemDetails(tm.getCode(),tm.getUnitPrice(),tm.getQty()));
        }
        Order order = new Order(txtOrderId.getText(),new Date(),
                Double.parseDouble(lblTotal.getText()), cmbCustomer.getValue(),details);
        manageQty();
        Database.orderTable.add(order);
        clearAll();
     }

    private void clearAll() {
        obList.clear();
        calculateTotal();
        clearFields();

        //=================
        cmbCustomer.setValue(null);
        cmbItemCode.setValue(null);
        //=================
        txtCustomerAddress.clear();
        txtCustomerSalary.clear();
        txtCustomerName.clear();
        cmbCustomer.requestFocus();
        setOrderId();
    }

    ObservableList<CartTM> obList = FXCollections.observableArrayList();
    public void addToCartOnAction(ActionEvent actionEvent) {
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty =Integer.parseInt(txtQty.getText());
        double total = unitPrice*qty;
        Button btn = new Button("Remove");
        int row = isAlreadyExists(cmbItemCode.getValue());
        CartTM tm = new CartTM(cmbItemCode.getValue(),txtDescription.getText(), unitPrice,qty,total, btn );
        if(row == -1){
            obList.add(tm);
            tblCart.setItems(obList);
        }else{
            int tempQty = obList.get(row).getQty()+qty;
            double tempTotal = unitPrice*tempQty;
            obList.get(row).setQty(tempQty);
            obList.get(row).setTotal(tempTotal);
            tblCart.refresh();
        }
        calculateTotal();
        clearFields();
        cmbItemCode.requestFocus();

        btn.setOnAction(e->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete Cart Item",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> val = alert.showAndWait();
            if(val.get() == ButtonType.YES){
                for(CartTM t : obList){
                    if(t.getCode().equals(tm.getCode())){
                        obList.remove(t);
                        calculateTotal();
                        return;
                    }
                }
            }
            tblCart.refresh();
        });
    }

    private void clearFields() {
        txtQty.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
    }

    private int isAlreadyExists(String code) {
        for(int i=0; i<obList.size();i++){
            if(obList.get(i).getCode().equals(code)){
                return i;
            }
        }
        return -1;
    }

    private void calculateTotal(){
        double total =0.00;
        for(CartTM tm: obList){
            total += tm.getTotal();
        }
        lblTotal.setText(String.valueOf(total));
    }

    private void manageQty(){
        for(CartTM tm: obList){
            for(Item i : Database.itemTable){
                if(i.getCode().equals(tm.getCode())){
                    i.setQtyOnHand(i.getQtyOnHand() - tm.getQty());
                    break;
                }
            }
        }
    }

}
