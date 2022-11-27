package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Item;
import com.seekerscloud.pos.view.tm.CustomerTM;
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
import java.sql.*;
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
        String searchText = "%"+text+"%";
        try{
            ObservableList<ItemTM> obList = FXCollections.observableArrayList();
            String sql = "SELECT * FROM item WHERE description LIKE ?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,searchText);
            ResultSet set  = statement.executeQuery();
            while(set.next()){
                Button btn = new Button("Delete");
                ItemTM tm = new ItemTM(set.getString(1),
                        set.getString(2),
                        set.getDouble(3),
                        set.getInt(4),
                        btn);
                obList.add(tm);

                btn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to deleted this Item.", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {
                        try{
                            String sql1 = "DELETE  FROM item WHERE code=?";
                            PreparedStatement statement1 = DBConnection.getInstance().getConnection().prepareStatement(sql);
                            statement1.setString(1,tm.getCode());
                            if (statement1.executeUpdate()>0) {
                                searchItem(searchText);
                                new Alert(Alert.AlertType.INFORMATION, "Item Successfully deleted.").show();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Something went wrong.").show();
                            }

                        }catch (ClassNotFoundException | SQLException em){
                            em.printStackTrace();
                        }
                    }

                });
                tblItem.setItems(obList);
            }
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
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
            try{
                String sql = "INSERT INTO item VALUES(?,?,?,?)";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, item.getCode());
                statement.setString(2, item.getDescription());
                statement.setDouble(3, item.getUnitPrice());
                statement.setInt(4, item.getQtyOnHand());
                if(statement.executeUpdate()>0){
                    new Alert(Alert.AlertType.INFORMATION, "Item saved ! ").show();
                    searchItem(searchText);
                    clearFields();
                }
            }catch (ClassNotFoundException | SQLException ex){
                ex.printStackTrace();
            }

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
            try{
                String sql = "update item set description=?, unitPrice=?, qtyOnHand=? where code=?";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, item.getDescription());
                statement.setDouble(2, item.getUnitPrice());
                statement.setDouble(3, item.getQtyOnHand());
                statement.setString(4, item.getCode());
                if(statement.executeUpdate()>0){
                    new Alert(Alert.AlertType.INFORMATION, "Item have been Updated ! ").show();
                    searchItem(searchText);
                    clearFields();
                }
            }catch (ClassNotFoundException | SQLException ex){
                ex.printStackTrace();
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
