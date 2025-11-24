package com.restaurant.KDS.controller;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.service.MenuService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class MainController {

    @FXML
    private TableView<MenuItem> menuTable;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, BigDecimal> priceColumn;

    private final MenuService menuService;

    @Autowired
    public MainController(MenuService menuService) {
        this.menuService = menuService;
    }

    @FXML
    private void initialize() {
        // Setup table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Load data
        loadMenuItems();
    }

    private void loadMenuItems() {
        menuTable.setItems(
                FXCollections.observableArrayList(menuService.getAllMenuItems())
        );
    }

    @FXML
    private void handleAddItem() {
        MenuItem burger = new MenuItem();
        burger.setName("Burger");
        burger.setPrice(BigDecimal.valueOf(10.00));
        burger.setCategory("Main");

        List<String> ingredients = new ArrayList<>();
        ingredients.add("Bread");
        ingredients.add("beef patty");
        ingredients.add("cheese");
        ingredients.add("pickle");
        ingredients.add("onion");
        ingredients.add("tomato");
        ingredients.add("sauce");

        burger.setIngredients(ingredients);

        List<String> allergens = new ArrayList<>();
        allergens.add("gluten");

        burger.setAllergens(allergens);
        burger.setAvailable(true);
        burger.setPrepTimeMinutes(3);

        List<String> stations = new ArrayList<>();
        stations.add("grill");
        stations.add("set");

        burger.setStations(stations);

        menuService.saveMenuItem(burger);
        loadMenuItems();
    }
}
