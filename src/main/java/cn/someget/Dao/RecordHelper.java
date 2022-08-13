package cn.someget.Dao;

import cn.someget.controllers.DialogController;
import cn.someget.models.BooksRecordModel;
import cn.someget.utils.Md5Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static cn.someget.utils.Constants.*;

/**
 * record dao
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class RecordHelper {

    /**
     * Singleton
     */
    private static final RecordHelper INSTANCE = new RecordHelper();

    public static RecordHelper getInstance() {
        return INSTANCE;
    }

    /**
     * constructor
     */
    private final DBConnect connect;

    private RecordHelper() {
        this.connect = new DBConnect();
    }

    /**
     * cid: -1 for all users records.
     *
     * @param cid pk
     * @return list
     */
    public List<BooksRecordModel> getRecords(int cid) {
        String query = "SELECT tid,cid,balance FROM " + ACCOUNT_TABLE;
        if (cid != -1) {
            query += (" WHERE cid = " + cid + ";");
        }
        List<BooksRecordModel> accounts = new ArrayList<>();
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                BooksRecordModel account = new BooksRecordModel();
                // grab record data by table field name into FundsRecordModel account object
                account.setCid(resultSet.getInt("cid"));
                account.setTid(resultSet.getInt("tid"));
                account.setBalance(resultSet.getDouble("balance"));
                long bookNum = Math.round(account.getBalance());
                account.setBalanceStr(account.getBalance() > 0 ? String.format("return %d", bookNum) : String.format("borrowing %d", -bookNum));
                // add account data to arraylist
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Accounts: " + e);
        }
        // return arraylist
        return accounts;
    }

    public double getBalance(int cid) {
        if (AccountHelper.getInstance().getAccount(cid, null) == null) {
            DialogController.showErrorDialog("Update Record Failed", "");
            return 0.0;
        }
        String query = String.format("SELECT SUM(balance) FROM %s WHERE cid = %d;", ACCOUNT_TABLE, cid);
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet result = stmt.executeQuery(query);
            if (result.next()) {
                return result.getDouble(1);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            DialogController.showErrorDialog("Update Record Failed", se.toString());
        }
        return 0.0;
    }

    public void extract(int cid, double balance) {
        if (balance <= 0) {
            DialogController.showErrorDialog("Input invalid", "Please check and re-enter");
            return;
        }
        double remainBalance = RecordHelper.getInstance().getBalance(cid);
        if (remainBalance < balance) {
            DialogController.showErrorDialog("Lack of balance", String.format("You have %.2f in your account, please retry", remainBalance));
        } else {
            RecordHelper.getInstance().updateBalance(cid, -balance);
        }
    }

    public Boolean updateBalance(int cid, double amount) {
        if (AccountHelper.getInstance().getAccount(cid, null) == null) {
            DialogController.showErrorDialog("Update Record Failed", "User doesn't exits.");
            return false;
        } else {
            try {
                String sql = String.format("INSERT INTO %s(cid,balance,create_time) VALUES (?,?,?)", ACCOUNT_TABLE);
                PreparedStatement stmt = connect.getConnection().prepareStatement(sql);
                stmt.setInt(1, cid);
                stmt.setDouble(2, amount);
                Timestamp date = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(3, date);
                int ret = stmt.executeUpdate();
                if (ret == 1) {
                    return true;
                }
            } catch (SQLException se) {
                se.printStackTrace();
                DialogController.showErrorDialog("Update Record Failed", se.toString());
            }
        }
        return false;
    }

    public Boolean updateRecord(int tid, double amount) {
        String query = String.format("UPDATE %s SET balance=%f WHERE tid=%d", ACCOUNT_TABLE, amount, tid);
        try {
            Statement stmt = connect.getConnection().createStatement();
            int ret = stmt.executeUpdate(query);
            if (ret == 1) {
                DialogController.showInfoDialog("", "Update Record Success", "");
                return true;
            } else {
                DialogController.showErrorDialog("Update Record Failed", "Record doesn't exist.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            DialogController.showErrorDialog("Update Record Failed", se.toString());
        }
        return false;
    }

    public Boolean deleteRecord(int tid) {
        String query = String.format("DELETE FROM %s WHERE tid=%d", ACCOUNT_TABLE, tid);
        try {
            Statement stmt = connect.getConnection().createStatement();
            int ret = stmt.executeUpdate(query);
            if (ret == 1) {
                DialogController.showInfoDialog("", "Delete Record Success", "");
                return true;
            } else {
                DialogController.showErrorDialog("Delete Record Failed", "Record doesn't exist.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            DialogController.showErrorDialog("Delete Record Failed", se.toString());
        }
        return false;
    }

    public void setupSqlTable() {
        String createUsersTable = String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(id INTEGER not NULL AUTO_INCREMENT, " +
                " uname VARCHAR(255), " +
                " passwd VARCHAR(255)," +
                " role int, " +
                "create_time datetime," +
                " PRIMARY KEY ( id )," +
                "UNIQUE (uname))", USER_TABLE);


        String createTransactionRecordsTable = String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(tid INTEGER not NULL AUTO_INCREMENT, " +
                " cid int, " +
                " balance numeric(38,2), " +
                "create_time datetime," +
                " PRIMARY KEY ( tid ))", ACCOUNT_TABLE);
        try {
            Statement stmt = connect.getConnection().createStatement();
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createTransactionRecordsTable);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public void setupInitRole() {
        String createUserSql = String.format("replace into %s( uname, passwd, role, create_time) values(?,?,?,?);", USER_TABLE);
        try {
            PreparedStatement stmt = connect.getConnection().prepareStatement(createUserSql);
            stmt.setString(1, "admin");
            stmt.setString(2, Md5Utils.toMD5("123456"));
            stmt.setInt(3, 0);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            stmt.setString(1, "manage");
            stmt.setString(2, Md5Utils.toMD5("123456"));
            stmt.setInt(3, 1);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();

        } catch (Exception se) {
            se.printStackTrace();
        }
    }
}
