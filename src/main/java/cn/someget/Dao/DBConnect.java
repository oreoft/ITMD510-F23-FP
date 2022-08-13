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

    private final static String URL = "jdbc:mysql://ziji:3306/510fp";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "jikechufa";

    public DBConnect() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error creating connection to database: " + e);
            System.exit(-1);
        }
    }
}
