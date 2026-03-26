package com.restaurant.KDS.controller;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.MenuService;
import com.restaurant.KDS.service.StationService;
import com.restaurant.KDS.util.ViewHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class AddMenuItemController {

    private final MenuService menuService;
    private final ConfigurableApplicationContext springContext;
    private final StationService stationService;


    private List<String> ingredients, allergens;

    private List<Station> stations;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private TextField categoryTextField;

    @FXML
    private TextField ingredientTextField;

    @FXML
    private TextField allergenTextField;

    @FXML
    private TextField timeTextField;

    @FXML
    private FlowPane ingredientsFlowPane;

    @FXML
    private FlowPane allergensFlowPane;

    @FXML
    private FlowPane stationFlowPane;

    @FXML
    private CheckBox availableCheckBox;


    public AddMenuItemController(MenuService menuService, ConfigurableApplicationContext springContext, StationService stationService) {
        this.menuService = menuService;
        this.springContext = springContext;
        ingredients = new ArrayList<>();
        allergens = new ArrayList<>();
        this.stationService = stationService;
        stations = stationService.getAllStations();
    }


    private void addTag(TextField textField, List<String> list, FlowPane flowPane) {
        String value = textField.getText();
        if (value == null || value.trim().isEmpty()) return;
        list.add(value);
        textField.clear();

        flowPane.getChildren().clear();
        for (String item : list) {
            Button button = new Button(item);
            button.setOnAction(buttonEvent -> {
                list.remove(item);
                flowPane.getChildren().remove(button); //Clicking the button removes the item from the list
            });
            flowPane.getChildren().add(button);
        }
    }

    @FXML
    private void addIngredient() {
        addTag(ingredientTextField, ingredients, ingredientsFlowPane);
    }

    @FXML
    private void addAllergen() {
        addTag(allergenTextField, allergens, allergensFlowPane);
    }

    @FXML
    private void addStations() {
        stationFlowPane.getChildren().clear();
        for (Station station : stations) {
            CheckBox checkBox = new CheckBox(station.getName());
            stationFlowPane.getChildren().add(checkBox);
        }
    }

    @FXML
    public void initialize() {
        addStations();
    }

    @FXML
    private void onSubmit(ActionEvent event) throws Exception {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(nameTextField.getText());
        menuItem.setPrice(new BigDecimal(priceTextField.getText()));
        menuItem.setCategory(categoryTextField.getText());
        menuItem.setIngredients(ingredients);
        menuItem.setAllergens(allergens);

        List<Station> selectedStations = new ArrayList<>(); //Gets the selected stations
        for (var node : stationFlowPane.getChildren()) {
            CheckBox cb = (CheckBox) node;
            if (cb.isSelected()) {
                int index = stationFlowPane.getChildren().indexOf(node);
                selectedStations.add(stations.get(index));
            }
        }

        menuItem.setStations(selectedStations);
        menuItem.setAvailable(availableCheckBox.isSelected());
        menuItem.setPrepTimeMinutes(Integer.parseInt(timeTextField.getText()));
        menuService.saveMenuItem(menuItem);

        ViewHelper.loadView("/fxml/MenuItemConfig.fxml", nameTextField, springContext);
    }
}
