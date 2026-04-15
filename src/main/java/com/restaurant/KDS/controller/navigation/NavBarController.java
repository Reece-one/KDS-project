package com.restaurant.KDS.controller.navigation;

import com.restaurant.KDS.util.ViewHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class NavBarController {

    private final ConfigurableApplicationContext springContext;

    @FXML
    private Node parentVBox;

    public NavBarController(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }


    @FXML
    private void onOrder() throws Exception {
        ViewHelper.loadView("/fxml/OrderEntry.fxml", parentVBox, springContext);
    }

    @FXML
    private void onMenu() throws Exception {
        ViewHelper.loadView("/fxml/MenuItemConfig.fxml", parentVBox, springContext);
    }

    @FXML
    private void onStation() throws Exception {
        ViewHelper.loadView("/fxml/StationConfig.fxml", parentVBox, springContext);
    }
}
