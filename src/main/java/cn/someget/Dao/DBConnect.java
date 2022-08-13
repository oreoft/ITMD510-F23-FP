package cn.someget.Dao;

import java.sql.Connection;
import java.sql.DriverManager;


/**
 * DBConnect
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class DBConnect {

    protected Connection connection;

    public Connection getConnection() {
        return connection;
    }

    private final static String URL = "jdbc:mysql://www.papademas.net:3307/510fp?autoReconnect=true&useSSL=false";
    private final static String USERNAME = "fp510";
    private final static String PASSWORD = "510";

    public DBConnect() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error creating connection to database: " + e);
            System.exit(-1);
        }
    }
}
