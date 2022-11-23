package com.seekerscloud.pos.db;

import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.modal.Item;
import com.seekerscloud.pos.modal.Order;

import java.util.ArrayList;

public class Database {
    public static ArrayList<Customer> customerTable = new ArrayList<Customer>();

    public static ArrayList<Item> itemTable = new ArrayList<Item>();

    public static ArrayList<Order> orderTable = new ArrayList<Order>();

    static {
        customerTable.add( new Customer("C001", "Kamal","Colombo",245455));
        customerTable.add( new Customer("C002", "Nimal","Kandy",453543));
        customerTable.add( new Customer("C003", "Sunil","Gale",534634));
        customerTable.add( new Customer("C004", "Amila","Kelani",6454244));

        itemTable.add(new Item("I001","Mango",432,54 ));
        itemTable.add(new Item("I002","Banana",543,534 ));
        itemTable.add(new Item("I003","Apple",433,46 ));
        itemTable.add(new Item("I004","Orange",653,65 ));
    }
}
