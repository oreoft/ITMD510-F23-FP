package cn.someget.controllers;

import cn.someget.Dao.AccountHelper;
import cn.someget.Dao.LoginModel;
import cn.someget.Dao.RecordHelper;
import cn.someget.models.AccountModel;
import cn.someget.models.BooksRecordModel;
import cn.someget.models.RoleType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * all admin operate
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class AdminController implements Initializable {

    @FXML
    private Pane paneRecords;
    @FXML
    private Pane paneAddBank;
    @FXML
    private Pane paneAccounts;
    @FXML
    private Label userLbl;
    @FXML
    private Label filteredBalance;

    @FXML
    private TableView<AccountModel> tblAccounts;
    @FXML
    private TableColumn<AccountModel, String> accountID;
    @FXML
    private TableColumn<AccountModel, String> accountName;
    @FXML
    private TableColumn<AccountModel, String> roleType;

    @FXML
    private TableView<BooksRecordModel> tblRecords;
    @FXML
    private TableColumn<BooksRecordModel, String> recordTid;
    @FXML
    private TableColumn<BooksRecordModel, String> recordCid;
    @FXML
    private TableColumn<BooksRecordModel, String> recordAmount;

    private RoleType managerType = RoleType.ADMIN;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountID.setCellValueFactory(new PropertyValueFactory<>("cid"));
        accountName.setCellValueFactory(new PropertyValueFactory<>("uname"));
        roleType.setCellValueFactory(new PropertyValueFactory<>("roleTypeString"));
        Platform.runLater(this::viewAccounts);

        recordTid.setCellValueFactory(new PropertyValueFactory<>("tid"));
        recordCid.setCellValueFactory(new PropertyValueFactory<>("cid"));
        recordAmount.setCellValueFactory(new PropertyValueFactory<>("balanceStr"));
    }

    public void viewAccounts() {
        paneAccounts.setVisible(true);
        paneRecords.setVisible(false);
        paneAddBank.setVisible(false);
        updateAccountTableData();
    }

    public void viewRecords() {
        paneAccounts.setVisible(false);
        paneRecords.setVisible(true);
        paneAddBank.setVisible(false);
        updateRecordTableData();
    }

    public void logout() {
        LoginModel.getInstance().logout();
        Router.goToLoginView();
    }

    public void clickFilter() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Filter");
        dialog.setHeaderText("Filter records by cid:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cid -> {
            boolean flag = false;
            try {
                int intCid = Integer.parseInt(cid);
                if (intCid >= 0) {
                    flag = true;
                    filterByCid(intCid);
                }
            } catch (Exception ignored) {
            }
            if (!flag) {
                DialogController.showErrorDialog("Error", "Please check your input cid");
            }
        });
    }

    private void filterByCid(int cid) {
        List<BooksRecordModel> records = RecordHelper.getInstance().getRecords(cid);
        tblRecords.getItems().setAll(records);
        if (!records.isEmpty()) {
            filteredBalance.setVisible(true);
            double sum = 0;
            for (BooksRecordModel model : records) {
                sum += model.getBalance();
            }
            updateUserBalance(cid, sum);
        } else {
            filteredBalance.setVisible(false);
        }
    }

    private void updateUserBalance(int cid, double balance) {
        filteredBalance.setText(String.format("Cid: %d, book of balance:  %s", cid, balance));
    }

    public void addAccount() {
        DialogController.accountInfoInputDialog("Add Account", LoginModel.getInstance().getAccount(), null, accountModel -> {
            Boolean flag = AccountHelper.getInstance().createUser(accountModel.getUname(), accountModel.getPasswdEncrypted(), accountModel.getRoleType());
            if (Boolean.TRUE.equals(flag)) {
                Platform.runLater(this::updateAccountTableData);
            }
            return accountModel;
        });
    }

    public void editAccount() {
        AccountModel model = tblAccounts.getSelectionModel().getSelectedItem();
        if (model == null) {
            return;
        }
        DialogController.accountInfoInputDialog("Edit Account", LoginModel.getInstance().getAccount(), model, accountModel -> {
            if (!Objects.equals(accountModel.getPasswdEncrypted(), model.getPasswdEncrypted())) {
                // passwd changed, need re encrypt
                accountModel.setPasswdEncrypted(AccountHelper.getInstance().encryptedPassword(accountModel.getPasswdEncrypted()));
            }
            Boolean flag = AccountHelper.getInstance().editAccount(accountModel, model);
            if (Boolean.TRUE.equals(flag)) {
                Platform.runLater(this::updateAccountTableData);
            }
            return accountModel;
        });
    }

    public void deleteAccount() {
        AccountModel model = tblAccounts.getSelectionModel().getSelectedItem();
        if (model != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure you want delete this account?");
            alert.setContentText(String.format("Delete Account: %s, role type is: %s", model.getUname(), model.getRoleTypeString()));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                Boolean flag = AccountHelper.getInstance().deleteAccount(model.getCid());
                if (Boolean.TRUE.equals(flag)) {
                    tblAccounts.getItems().remove(model);
                }
            }

        }
    }

    public void addRecord() {
        DialogController.recordInfoInputDialog("Add Record", null, new Function<BooksRecordModel, BooksRecordModel>() {
            private void run() {
                updateRecordTableData();
            }

            @Override
            public BooksRecordModel apply(BooksRecordModel fundsRecordModel) {
                Boolean flag = RecordHelper.getInstance().updateBalance(fundsRecordModel.getCid(), fundsRecordModel.getBalance());
                if (Boolean.TRUE.equals(flag)) {
                    Platform.runLater(this::run);
                }
                return fundsRecordModel;
            }
        });
    }

    public void editRecord() {
        BooksRecordModel model = tblRecords.getSelectionModel().getSelectedItem();
        if (model == null) {
            return;
        }
        DialogController.recordInfoInputDialog("Edit Record", model, fundsRecordModel -> {
            Boolean flag = RecordHelper.getInstance().updateRecord(model.getTid(), fundsRecordModel.getBalance());
            if (Boolean.TRUE.equals(flag)) {
                Platform.runLater(this::updateRecordTableData);
            }
            return fundsRecordModel;
        });
    }

    public void deleteRecord() {
        BooksRecordModel model = tblRecords.getSelectionModel().getSelectedItem();
        Boolean flag = RecordHelper.getInstance().deleteRecord(model.getTid());
        if (Boolean.TRUE.equals(flag)) {
            Platform.runLater(this::updateRecordTableData);
        }
    }

    private void updateAccountTableData() {
        tblAccounts.getItems().setAll(AccountHelper.getInstance().getAccounts(managerType));
    }

    private void updateRecordTableData() {
        filteredBalance.setVisible(false);
        tblRecords.getItems().setAll(RecordHelper.getInstance().getRecords(-1));
    }
}
