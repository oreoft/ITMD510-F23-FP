package cn.someget.application;

import cn.someget.Dao.RecordHelper;
import cn.someget.controllers.Router;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * entry of fx project
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class Main extends Application {

    /**
     * set global stage object
     */
    public static Stage stage;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        Router.goToLoginView();
        stage.show();
        // init table
        RecordHelper.getInstance().setupSqlTable();
        // init super role
        RecordHelper.getInstance().setupInitRole();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
