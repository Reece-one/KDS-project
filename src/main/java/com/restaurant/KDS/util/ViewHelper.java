package com.restaurant.KDS.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

public class ViewHelper {

    public static void loadView(String fxmlPath, Node anyNode, ConfigurableApplicationContext springContext) throws Exception {
        FXMLLoader loader = new FXMLLoader(ViewHelper.class.getResource(fxmlPath));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) anyNode.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
