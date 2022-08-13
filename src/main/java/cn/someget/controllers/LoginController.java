package cn.someget.controllers;

import cn.someget.Dao.AccountHelper;
import cn.someget.Dao.LoginModel;
import cn.someget.models.RoleType;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * all user login
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private final LoginModel model;

    public LoginController() {
        model = LoginModel.getInstance();
    }

    public void login() {
        String username = this.txtUsername.getText();
        String password = this.txtPassword.getText();
        if (checkInputUserNameAndPassword(username, password)) {
            // authentication check
            checkCredentials(username, password);
        }
    }

    public void signup() {
        String username = this.txtUsername.getText();
        String password = this.txtPassword.getText();
        if (checkInputUserNameAndPassword(username, password)) {
            if (Boolean.TRUE.equals(AccountHelper.getInstance().createUser(username, password, RoleType.CUSTOMER))) {
                // authentication check
                checkCredentials(username, password);
            }
        }
    }

    public boolean checkInputUserNameAndPassword(String username, String password) {
        // Validations
        if (username == null || "".equals(username.trim())) {
            DialogController.showErrorDialog("Error", "Username Cannot be empty or spaces");
            return false;
        }
        if (password == null || "".equals(password.trim())) {
            DialogController.showErrorDialog("Error", "Password Cannot be empty or spaces");
            return false;
        }
        if ("".equals(username.trim()) && "".equals(password.trim())) {
            DialogController.showErrorDialog("Error", "User name / Password Cannot be empty or spaces");
            return false;
        }
        return true;
    }

    public void checkCredentials(String username, String password) {
        Boolean isValid = model.getCredentials(username, password);
        if (Boolean.FALSE.equals(isValid)) {
            DialogController.showErrorDialog("Error", "User account password is incorrect or User does not exist!");
            return;
        }
        if (Boolean.TRUE.equals(model.isAdmin())) {
            // If user is admin, inflate admin view
            Router.goToAdminView();
        } else if (model.getAccount().getRoleType() == RoleType.ACCOUNT_MANAGER) {
            Router.goToManagerView();
        } else {
            // If user is customer, inflate customer view
            Router.goToBooksOperateView();
        }
    }
}