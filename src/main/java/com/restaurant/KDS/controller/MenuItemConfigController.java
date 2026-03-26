package com.restaurant.KDS.controller;

import com.restaurant.KDS.service.MenuService;
import com.restaurant.KDS.service.StationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class MenuItemConfigController {

    private final MenuService menuService;
    private final ConfigurableApplicationContext springContext;

    public MenuItemConfigController(MenuService menuService, ConfigurableApplicationContext springContext) {
        this.menuService = menuService;
        this.springContext = springContext;
    }

    @FXML
    private TableView menuItemTableView;

    @FXML
    private void onAddMenuItem() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddMenuItem.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) menuItemTableView.getScene().getWindow();
        stage.setScene(new Scene(root));

    }

}
