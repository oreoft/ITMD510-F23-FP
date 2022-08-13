package cn.someget.controllers;

import cn.someget.application.Main;
import cn.someget.models.RoleType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

/**
 * all view router
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class Router {
    public static void goToLoginView() {
        try {
            AnchorPane root = FXMLLoader.load(Router.class.getResource("/views/LoginView.fxml"));
            Scene scene = new Scene(root);
            addCssForScene(scene);
            Main.stage.setScene(scene);
            Main.stage.setTitle("Login View");
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToAdminView() {
        try {
            AnchorPane root;
            root = FXMLLoader.load(Router.class.getResource("/views/AdminView.fxml"));
            Main.stage.setTitle("Admin View");
            Scene scene = new Scene(root);
            addCssForScene(scene);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToManagerView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Router.class.getResource("/views/AdminView.fxml"));
            AnchorPane root = fxmlLoader.load();
            Main.stage.setTitle("Manager View");
            Scene scene = new Scene(root);
            addCssForScene(scene);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToBooksOperateView() {
        try {
            AnchorPane root;
            root = FXMLLoader.load(Router.class.getResource("/views/BooksOperateView.fxml"));
            Main.stage.setTitle("Client View");
            Scene scene = new Scene(root);
            addCssForScene(scene);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }

    }

    private static void addCssForScene(Scene se) {
        se.getStylesheets().add(Router.class.getResource("/views/styles.css").toExternalForm());
    }

}

