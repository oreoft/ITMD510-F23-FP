package cn.someget.Dao;

import cn.someget.controllers.DialogController;
import cn.someget.models.AccountModel;
import cn.someget.models.RoleType;
import cn.someget.utils.Md5Utils;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.someget.utils.Constants.USER_TABLE;

public class AccountHelper {

    /**
     * Singleton
     */
    private static final AccountHelper INSTANCE = new AccountHelper();

    public static AccountHelper getInstance() {
        return INSTANCE;
    }

    /**
     * constructor
     */
    private DBConnect connect;

    private AccountHelper() {
        this.connect = new DBConnect();
    }

    /**
     * fetch user account
     *
     * @param cid  pk
     * @param name username
     * @return the account bo
     */
    public AccountModel getAccount(int cid, String name) {
        String condition;
        if (name != null && !name.isEmpty()) {
            condition = " where uname = " + String.format("'%s'", name);
        } else {
            condition = " where id = " + cid;
        }
        String query = "select * from " + USER_TABLE + condition;
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                AccountModel account = new AccountModel();
                account.setCid(rs.getInt("id"));
                account.setPasswdEncrypted(rs.getString("passwd"));
                account.setRoleType(RoleType.values()[rs.getInt("role")]);
                account.setUname(rs.getString("uname"));
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * create user
     *
     * @param username this username
     * @param password the password
     * @param role     the role type
     * @return result
     */
    public Boolean createUser(String username, String password, RoleType role) {
        if (username != null && !username.isEmpty()) {
            AccountModel existAccount = getAccount(0, username);
            if (existAccount != null) {
                DialogController.showErrorDialog("User name exist", "User name exist, Please try another user name");
                return false;
            }
        }
        try {
            String sql = String.format("INSERT INTO %s(uname,passwd,role,create_time) VALUES (?,?,?,?)", USER_TABLE);
            PreparedStatement stmt = connect.getConnection().prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, encryptedPassword(password));
            stmt.setInt(3, role.ordinal());
            Timestamp date = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(4, date);
            int ret = stmt.executeUpdate();
            if (ret == 1) {
                return true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            DialogController.showErrorDialog(null, se.toString());
        }
        return false;
    }

    /**
     * encode password
     *
     * @param passwd use passwd
     * @return encoded passwd
     */
    public String encryptedPassword(String passwd) {
        String encryptedPassword = "";
        try {
            encryptedPassword = Md5Utils.toMD5(passwd);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptedPassword;
    }


    public List<AccountModel> getAccounts(RoleType ownerType) {
        String condition = "";
        switch (ownerType) {
            case CUSTOMER:
                return null;
            case ADMIN:
                condition = " WHERE role > 0;";
                break;
            case ACCOUNT_MANAGER:
                condition = " WHERE role > 1;";
                break;
            default:
        }
        List<AccountModel> accounts = new ArrayList<>();
        String query = "select * from " + USER_TABLE + condition;
        try {
            Statement statement = connect.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                AccountModel account = new AccountModel();
                // grab record data by table field name into FundsRecordModel account object
                account.setCid(resultSet.getInt("id"));
                account.setUname(resultSet.getString("uname"));
                account.setPasswdEncrypted(resultSet.getString("passwd"));

                account.setRoleType(RoleType.values()[resultSet.getInt("role")]);
                // add account data to arraylist
                accounts.add(account);
            }
        } catch (SQLException se) {
            System.out.println("Error fetching Accounts: " + se);
            DialogController.showErrorDialog(null, se.toString());
        }
        return accounts;
    }

    public Boolean deleteAccount(int cid) {
        String query = String.format("DELETE FROM %s WHERE id=%d", USER_TABLE, cid);
        try {
            Statement statement = connect.getConnection().createStatement();
            int result = statement.executeUpdate(query);
            if (result == 1) {
                return true;
            }
        } catch (SQLException se) {
            System.out.println("Error delete Account: " + se);
            DialogController.showErrorDialog(null, se.toString());
        }
        return false;
    }

    public Boolean editAccount(AccountModel newAccount, AccountModel old) {
        // user name changed
        if (!Objects.equals(newAccount.getUname(), old.getUname())) {
            // check whether the name is valid
            AccountModel existAccount = getAccount(0, newAccount.getUname());
            if (existAccount != null) {
                DialogController.showErrorDialog("User name exist", "User name exist,Please try another user name");
                return false;
            }
        }
        String query = String.format("UPDATE %s SET uname='%s', passwd='%s', role=%d WHERE id=%d;", USER_TABLE,
                newAccount.getUname(), newAccount.getPasswdEncrypted(), newAccount.getRoleType().ordinal(), newAccount.getCid());
        try {
            Statement statement = connect.getConnection().createStatement();
            int result = statement.executeUpdate(query);
            if (result == 1) {
                return true;
            }
        } catch (SQLException se) {
            System.out.println("Error edit Account: " + se);
            DialogController.showErrorDialog(null, se.toString());
        }
        return false;
    }
}
