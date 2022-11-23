package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
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
        ObservableList<CustomerTM> obList = FXCollections.observableArrayList();
        for(Customer customer :Database.customerTable){
            if(customer.getName().contains(text) || customer.getAddress().contains(text)) {
                Button btn = new Button("Delete");
                CustomerTM tm = new CustomerTM(customer.getId(), customer.getName(),
                        customer.getAddress(), customer.getSalary(), btn);
                obList.add(tm);

                btn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to deleted this Customer.", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {
                        boolean isDeleted = Database.customerTable.remove(customer);
                        if (isDeleted) {
                            searchCustomer(searchText);
                            new Alert(Alert.AlertType.INFORMATION, "Customer Successfully deleted.").show();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Something went wrong.").show();
                        }
                    }

                });
            }
        }
        tblCustomer.setItems(obList);
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
            // save customer
            boolean isSaved =  Database.customerTable.add(customer);
            if(isSaved){
                searchCustomer(searchText);
                clearFields();
                new Alert(Alert.AlertType.INFORMATION, "Customer have been saved ! ").show();
            }else{
                new Alert(Alert.AlertType.WARNING, "Try Again! ").show();
            }

        }else{
            for( Customer c :Database.customerTable){
                if(txtCustomerId.getText().equalsIgnoreCase(c.getId())){
                    c.setName(txtCustomerName.getText());
                    c.setAddress(txtCustomerAddress.getText());
                    c.setSalary(Double.parseDouble(txtCustomerSalary.getText()));
                    new Alert(Alert.AlertType.INFORMATION, "Customer have been Updated ! ").show();
                    searchCustomer(searchText);
                    clearFields();
                }
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
