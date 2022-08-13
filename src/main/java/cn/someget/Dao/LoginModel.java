package cn.someget.Dao;

import cn.someget.models.AccountModel;
import cn.someget.models.RoleType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static cn.someget.utils.Constants.USER_TABLE;


/**
 * login service
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class LoginModel {

    /**
     * constructor
     */
    DBConnect conn;

    private LoginModel() {
        conn = new DBConnect();
    }

    /**
     * Singleton
     */
    private static final LoginModel INSTANCE = new LoginModel();

    public static LoginModel getInstance() {
        return INSTANCE;
    }

    private AccountModel account = new AccountModel();

    public AccountModel getAccount() {
        return this.account;
    }

    public int getId() {
        return account.getCid();
    }

    public Boolean isAdmin() {
        return account.getRoleType() == RoleType.ADMIN;
    }

    public void logout() {
        this.account = new AccountModel();
    }


    /**
     * judge user
     * @param username username
     * @param password password
     * @return this result
     */
    public Boolean getCredentials(String username, String password) {
        String query = String.format("SELECT * FROM %s WHERE uname = ? and passwd = ?;", USER_TABLE);

        try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, AccountHelper.getInstance().encryptedPassword(password));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                account.setCid(rs.getInt("id"));
                account.setPasswdEncrypted(rs.getString("passwd"));
                account.setRoleType(RoleType.values()[rs.getInt("role")]);
                account.setUname(rs.getString("uname"));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != conn.getConnection()) {
                conn.getConnection();
            }
        }
        return false;
    }
}
