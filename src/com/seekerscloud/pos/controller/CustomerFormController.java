package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.mysql.cj.protocol.Resultset;
import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.view.tm.CustomerTM;
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
import java.util.List;
import java.util.Optional;

public class CustomerFormController {
    public AnchorPane customerContext;
    public TextField txtCustomerId;
    public TextField txtCustomerName;
    public TextField txtCustomerAddress;
    public TextField txtCustomerSalary;
    public TextField txtCustomerSearch;
    public TableView<CustomerTM> tblCustomer;
    public TableColumn colCustomerId;
    public TableColumn colCustomerName;
    public TableColumn colAddress;
    public TableColumn colOption;
    public TableColumn colSalary;
    public JFXButton btnSaveUpdateCustomer;

    public String searchText = "";

    public void initialize(){
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        searchCustomer(searchText);
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(null != newValue){
                setData(newValue);

            }
        });
        txtCustomerSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue;
            searchCustomer(searchText);
        });
    }

    private void setData(CustomerTM tm) {

        txtCustomerId.setText(tm.getId());
        txtCustomerName.setText(tm.getName());
        txtCustomerAddress.setText(tm.getAddress());
        txtCustomerSalary.setText(String.valueOf(tm.getSalary()));
        btnSaveUpdateCustomer.setText("Update Customer");
    }

    private void searchCustomer(String text) {
        String searchText = "%"+text+"%";
        try{
            ObservableList<CustomerTM> obList = FXCollections.observableArrayList();
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","943043167v");
            String sql = "SELECT * FROM customer WHERE name LIKE ? || address LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,searchText);
            statement.setString(2,searchText);
             ResultSet  set  = statement.executeQuery();
             while(set.next()){
                     Button btn = new Button("Delete");
                     CustomerTM tm = new CustomerTM(set.getString("id"),
                             set.getString("name"),
                             set.getString("address"),
                             set.getDouble("salary"),
                             btn);
                     obList.add(tm);

                     btn.setOnAction(e -> {
                         Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to deleted this Customer.", ButtonType.YES, ButtonType.NO);
                         Optional<ButtonType> buttonType = alert.showAndWait();
                         if (buttonType.get() == ButtonType.YES) {
                             try{
                                 Class.forName("com.mysql.jdbc.Driver");
                                 Connection connection1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","943043167v");
                                 String sql1 = "DELETE * FROM customer WHERE id=?";
                                 PreparedStatement statement1 = connection1.prepareStatement(sql1);
                                 statement1.setString(1,tm.getId());
                                 if (statement1.executeUpdate()>0) {
                                     searchCustomer(searchText);
                                     new Alert(Alert.AlertType.INFORMATION, "Customer Successfully deleted.").show();
                                 } else {
                                     new Alert(Alert.AlertType.ERROR, "Something went wrong.").show();
                                 }

                             }catch (ClassNotFoundException | SQLException em){
                                 em.printStackTrace();
                             }
                         }

                     });
                 tblCustomer.setItems(obList);
             }
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }


    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUI("DashboardForm","Dashboard");
    }

    public void addNewCustomerOnAction(ActionEvent actionEvent) {
        btnSaveUpdateCustomer.setText("Save Customer");
    }

    public void saveCustomerOnAction(ActionEvent actionEvent) {
        Customer customer = new Customer(txtCustomerId.getText(), txtCustomerName.getText(), txtCustomerAddress.getText(),
        Double.parseDouble(txtCustomerSalary.getText()));

        if(btnSaveUpdateCustomer.getText().equalsIgnoreCase("save customer")){
            try {
                String sql = "INSERT INTO customer VALUES(?,?,?,?)";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, customer.getId());
                statement.setString(2, customer.getName());
                statement.setString(3, customer.getAddress());
                statement.setDouble(4, customer.getSalary());

                if(statement.executeUpdate()>0){
                    searchCustomer(searchText);
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Customer have been saved ! ").show();
                }else{
                    new Alert(Alert.AlertType.WARNING, "Try Again! ").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                e.printStackTrace();
            }

            // save customer


        }else{

            try{
                    String sql = "UPDATE customer SET name=? ,address=? ,salary=? WHERE id=?";
                    PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                    statement.setString(1, customer.getName());
                    statement.setString(2, customer.getAddress());
                    statement.setDouble(3, customer.getSalary());
                    statement.setString(4, customer.getId());
                    if(statement.executeUpdate()>0){
                        new Alert(Alert.AlertType.INFORMATION, "Customer have been Updated ! ").show();
                        searchCustomer(searchText);
                        clearFields();
                    }
            }catch (ClassNotFoundException | SQLException ex){
                ex.printStackTrace();
            }


        }

    }


    private void clearFields(){
        txtCustomerId.clear();
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        txtCustomerSalary.clear();
        btnSaveUpdateCustomer.setText("Save Customer");
    }
    private void setUI(String location, String title) throws IOException {
        Stage stage = (Stage) customerContext.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene( new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }
}
