package cn.someget.controllers;

import cn.someget.Dao.LoginModel;
import cn.someget.Dao.RecordHelper;
import cn.someget.models.AccountModel;
import cn.someget.models.BooksRecordModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

/**
 * all book operate
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class BooksOperateController implements Initializable {

    private int userid;
    @FXML
    private Label userBalance;
    @FXML
    private Label userLbl;

    /* TABLEVIEW intel */
    @FXML
    private TableView<BooksRecordModel> tblRecords;
    @FXML
    private TableColumn<BooksRecordModel, String> tid;
    @FXML
    private TableColumn<BooksRecordModel, String> balance;

    public void customResize(TableView<?> view) {
        AtomicLong width = new AtomicLong();
        view.getColumns().forEach(col -> width.addAndGet((long) col.getWidth()));
        double tableWidth = view.getWidth();

        if (tableWidth > width.get()) {
            view.getColumns().forEach(col -> col.setPrefWidth(col.getWidth() + ((tableWidth - width.get()) / view.getColumns().size())));
        }
    }
    /* End TABLEVIEW intel */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AccountModel current = LoginModel.getInstance().getAccount();
        userid = current.getCid();
        System.out.println("logged in id " + userid);
        userLbl.setText(String.format("Hello %s, your id: %d", current.getUname(), current.getCid()));

        tid.setCellValueFactory(new PropertyValueFactory<>("tid"));
        balance.setCellValueFactory(new PropertyValueFactory<>("balanceStr"));

        // auto adjust width of columns depending on their content
        tblRecords.setColumnResizePolicy(param -> true);
        Platform.runLater(() -> {
            updateUserBalance();
            customResize(tblRecords);
        });
        tblRecords.setVisible(false);
    }

    /**
     *  see record
     */
    public void viewRecords() {
        // load table data from FundsRecordModel List
        tblRecords.getItems().setAll(RecordHelper.getInstance().getRecords(userid));
        // set tableview to visible if not
        tblRecords.setVisible(true);
    }

    /**
     * logout
     */
    public void logout() {
        LoginModel.getInstance().logout();
        Router.goToLoginView();
    }

    /**
     * book trans
     */
    public void createTransaction() {

        TextInputDialog dialog = new TextInputDialog("Enter dollar amount");
        dialog.setTitle("Bank Account Entry Portal");
        dialog.setHeaderText("Enter Transaction");
        dialog.setContentText("Please enter your balance:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println("Balance entry: " + result.get());
            RecordHelper.getInstance().updateBalance(userid, Double.parseDouble(result.get()));
        }

        result.ifPresent(balance -> System.out.println("Balance entry: " + balance));

    }

    /**
     * book input
     */
    public void onStorage() {
        DialogController.showInputDialog("Storage", null,
                "Please enter the book of amount you want to Return:", null, s -> {
                    try {
                        double value = Double.parseDouble(s);
                        if (value <= 0) {
                            DialogController.showErrorDialog("Input invalid", "Please check and re-enter");
                        } else {
                            RecordHelper.getInstance().updateBalance(userid, value);
                            Platform.runLater(() -> {
                                updateUserBalance();
                                viewRecords();
                            });
                        }
                    } catch (NumberFormatException e) {
                        DialogController.showErrorDialog("Input invalid", "Please check and re-enter");
                    }
                    return s;
                });
    }

    /**
     * book extract
     */
    public void onExtract() {
        DialogController.showInputDialog("Extract", null,
                "Please enter the book of amount you want to Borrowing:", null, s -> {
                    try {
                        double value = Double.parseDouble(s);
                        RecordHelper.getInstance().extract(userid, value);
                        Platform.runLater(() -> {
                            updateUserBalance();
                            viewRecords();
                        });
                    } catch (NumberFormatException e) {
                        DialogController.showErrorDialog("Input invalid", "Please check and re-enter");
                    }
                    return s;
                });
    }

    /**
     * update
     */
    private void updateUserBalance() {
        double balance = RecordHelper.getInstance().getBalance(userid);
        userBalance.setText(String.format("the rest book:  %s", String.valueOf(Math.round(balance))));
    }
}
