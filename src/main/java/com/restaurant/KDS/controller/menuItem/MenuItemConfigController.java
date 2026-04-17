package com.restaurant.KDS.controller.menuItem;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.service.MenuService;
import com.restaurant.KDS.util.ViewHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class MenuItemConfigController {

    private final MenuService menuService;
    private final ConfigurableApplicationContext springContext;

    public MenuItemConfigController(MenuService menuService, ConfigurableApplicationContext springContext) {
        this.menuService = menuService;
        this.springContext = springContext;
    }

    @FXML
    private TableView<MenuItem> menuItemTableView;

    @FXML
    private TableColumn<MenuItem, String> idColumn;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, String> categoryColumn;

    @FXML
    private TableColumn<MenuItem, Boolean> availableColumn;

    @FXML
    private void onAddMenuItem() throws Exception {
        ViewHelper.loadView("/fxml/AddMenuItem.fxml", menuItemTableView, springContext);
    }

    /**
     * On initialize, create a table with each row representing a {@link MenuItem} entry.
     * Clicking the row open a view to edit the item.
     */
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        menuItemTableView.getItems().addAll(menuService.getAllMenuItems());

        menuItemTableView.setOnMouseClicked(event -> {
            MenuItem selected = menuItemTableView.getSelectionModel().getSelectedItem();
            if (selected != null && event.getClickCount() == 2) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditMenuItem.fxml"));
                    loader.setControllerFactory(springContext::getBean);
                    Parent root = loader.load();

                    EditMenuItemController controller = loader.getController();
                    controller.setMenuItem(selected);

                    Stage stage = (Stage) menuItemTableView.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
