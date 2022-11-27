package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.seekerscloud.pos.db.DBConnection;
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
import java.sql.*;
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
                setCustomerDetails();
            }
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(null != newValue){
                setItemDetails(newValue);
            }
        });
    }

    private void setOrderId(){

        try{
            String sql = "SELECT orderId FROM  `order` ORDER BY orderId  DESC  LIMIT 1";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set= statement.executeQuery();
            if(set.next()){
               // GEN id
                String tempOrderId = set.getString(1);
                String[] array = tempOrderId.split("-");
                int tempNumber =  Integer.parseInt(array[1]);
                int finalizeOrderId = tempNumber+1;
                txtOrderId.setText("D-"+ finalizeOrderId);
            }else{
                //D-1
                txtOrderId.setText("D-1");
                return;
            }

        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
        }
    }
    private void setItemDetails(String newValue) {

        try{
            String sql = "SELECT * FROM  item WHERE code=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1, cmbItemCode.getValue());
            ResultSet set= statement.executeQuery();
            if(set.next()){
                txtDescription.setText(set.getString(2));
                txtUnitPrice.setText(String.valueOf(set.getString(3)));
                txtQtyOnHand.setText(String.valueOf(set.getString(4)));
            }

        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
        }

    }


    private void setCustomerDetails() {

        try{
            String sql = "SELECT * FROM  customer WHERE id=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1, cmbCustomer.getValue());
            ResultSet set= statement.executeQuery();
            if(set.next()){
                txtCustomerName.setText(set.getString(2));
                txtCustomerAddress.setText(set.getString(3));
                txtCustomerSalary.setText(String.valueOf(set.getString(4)));
            }

        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
        }
    }

    private void loadAllItemCodes() {
        try{
            String sql = "SELECT code FROM  item";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set  = statement.executeQuery();

            ArrayList<String> idList = new ArrayList<>();
            while(set.next()){
                idList.add(set.getString(1));
            }
            ObservableList<String> obList = FXCollections.observableArrayList(idList);
            cmbItemCode.setItems(obList);
        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
        }
    }

    private void loadAllCustomerId() {
        try{
            String sql = "SELECT id FROM  customer";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set  = statement.executeQuery();
            ArrayList<String> idList = new ArrayList<>();
            while(set.next()){
                idList.add(set.getString(1));
            }
            ObservableList<String> obList = FXCollections.observableArrayList(idList);
            cmbCustomer.setItems(obList);

        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
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



    private void setUI(String location, String title) throws IOException {
        Stage stage = (Stage) placeOrderContext.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene( new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }

    public void placeOrderOnAction(ActionEvent actionEvent) throws SQLException {
        if(obList.isEmpty())return;
        ArrayList<ItemDetails> details =new ArrayList<>();
        for(CartTM tm: obList){
            details.add(new ItemDetails(tm.getCode(),tm.getUnitPrice(),tm.getQty()));
        }
        Order order = new Order(txtOrderId.getText(),
                new Date(),
                Double.parseDouble(lblTotal.getText()),
                cmbCustomer.getValue(),
                details);
            //place order
        Connection con =null;
        try{
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false);
            String sql = "INSERT INTO `order` VALUES(?,?,?,?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, order.getOrderId());
            statement.setString(2, txtOrderDate.getText());
            statement.setDouble(3, order.getTotalCost());
            statement.setString(4, order.getCustomer());
            boolean isOrderSaved =  statement.executeUpdate()>0;
            if(isOrderSaved){
                //update qty
                boolean isAllUpdated =  manageQty(details);
                if(isAllUpdated){
                    con.commit();
                    new Alert(Alert.AlertType.CONFIRMATION, "Order placed.").show();
                    clearAll();

                }else{
                    con.setAutoCommit(true);
                    con.rollback();
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }

            }else{
                // alert
                con.setAutoCommit(true);
                con.rollback();
                new Alert(Alert.AlertType.WARNING, "Try Again").show();
            }

        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
        }finally {
            con.setAutoCommit(true);
        }
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

    private boolean checkQty(String code, int qty){
        try{
           String sql = "SELECT qtyOnHand FROM  item WHERE code=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1, code);
            ResultSet set= statement.executeQuery();
            if(set.next()){
              int tempQty = set.getInt(1);
                    if(tempQty >= qty){
                        return true;
                    }else{
                        return false;
                    }

            }else{
                return false;
            }

        }catch (ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }
    ObservableList<CartTM> obList = FXCollections.observableArrayList();
    public void addToCartOnAction(ActionEvent actionEvent) {
        if(!checkQty(cmbItemCode.getValue(),Integer.parseInt(txtQty.getText()))){
            new Alert(Alert.AlertType.WARNING, "Invalid Qty").show();
            return;
        }
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
            if(!checkQty(cmbItemCode.getValue(),tempQty)){
                new Alert(Alert.AlertType.WARNING, "Invalid Qty").show();
                return;
            }
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

    private boolean manageQty(ArrayList<ItemDetails> details){
        try {

            for(ItemDetails d: details){
                String sql = "insert into `order_details` values(?,?,?,?) ";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, d.getItemCode());
                statement.setString(2, txtOrderId.getText());
                statement.setDouble(3, d.getUnitPrice());
                statement.setInt(4, d.getQty());
                boolean isOrderUpdate =  statement.executeUpdate() > 0;
                if(isOrderUpdate){
                   boolean  isQtyUpdate= update(d);
                   if(!isQtyUpdate){
                       return false;
                   }

                }else{
                    return false;
                }
            }
                   return true;


        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean update(ItemDetails d) {
        try {
            String sql = "update `item` set qtyOnHand=(qtyOnHand-?) where code=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setInt(1, d.getQty());
            statement.setString(2, d.getItemCode());
            return statement.executeUpdate() > 0;


        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
             return false;
        }

    }
}
