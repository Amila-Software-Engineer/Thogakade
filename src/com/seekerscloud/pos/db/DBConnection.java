package com.seekerscloud.pos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // singleton 1st rule we need to have private static reference  method with classname
    private static DBConnection dbConnection;
    private Connection connection;

    // 2nd rule need to have private constructor  with classname
    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "943043167v");

    }

    //3rd rule  need to have public static method that reruns instance of the same class
    // with classname
    public static DBConnection getInstance() throws SQLException, ClassNotFoundException {
        if(dbConnection == null){
            dbConnection =  new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection(){
        return connection;
    }
}
