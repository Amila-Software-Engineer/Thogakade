package com.seekerscloud.pos.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardFormController {
    public AnchorPane dashboardContext;
    public Label lblDate;
    public Label lblTime;

    public void initialize(){
        setDateAndTime();
    }

    private void setDateAndTime() {
        // set time
        Timeline time =  new Timeline(
                new KeyFrame(Duration.ZERO, e->{
                    DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd   |   HH:mm:ss");
                    lblDate.setText(LocalDateTime.now().format(formatter));
                }),new KeyFrame(Duration.seconds(1)));
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    public void openCustomerFormOnAction(ActionEvent actionEvent) throws IOException {
        setUI("CustomerForm","Customer Management");
    }

    public void openItemFormOnAction(ActionEvent actionEvent) throws IOException {
        setUI("ItemForm","Item  Management");
    }

    public void openOrderDetailsFormOnAction(ActionEvent actionEvent) throws IOException {
        setUI("OrderDetailsForm","Order Details Management");
    }

    public void openPlaceOrderFormOnAction(ActionEvent actionEvent) throws IOException {
        setUI("PlaceOrderForm","Place Order Management");
    }

    private void setUI(String location, String title) throws IOException {
        Stage stage = (Stage) dashboardContext.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene( new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }
}
