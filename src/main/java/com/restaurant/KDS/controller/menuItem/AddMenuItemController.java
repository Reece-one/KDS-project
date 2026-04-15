package com.restaurant.KDS.controller.menuItem;

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
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.restaurant.KDS.util.ViewHelper.showAlert;

@Component
public class AddMenuItemController {

    private final MenuService menuService;
    private final ConfigurableApplicationContext springContext;
    private final StationService stationService;


    private Set<String> ingredients, allergens;

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
        ingredients = new HashSet<>();
        allergens = new HashSet<>();
        this.stationService = stationService;
    }


    private void addTag(TextField textField, Set<String> set, FlowPane flowPane) {
        String value = textField.getText();
        if (value == null || value.trim().isEmpty()) return;
        set.add(value);
        textField.clear();

        flowPane.getChildren().clear();
        for (String item : set) {
            Button button = new Button(item);
            button.setOnAction(buttonEvent -> {
                set.remove(item);
                flowPane.getChildren().remove(button); //Clicking the button removes the item from the list
            });
            button.getStyleClass().add("tertiary-button");
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
        ingredients.clear();
        allergens.clear();
        stations = stationService.getAllStations();
        addStations();
    }

    @FXML
    private void onSubmit(ActionEvent event) throws Exception {
        if (nameTextField.getText().trim().isEmpty()
                || priceTextField.getText().trim().isEmpty()
                || categoryTextField.getText().trim().isEmpty()
                || timeTextField.getText().trim().isEmpty()) {
            showAlert("Please fill in all required fields");
            return;
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(nameTextField.getText());
        menuItem.setPrice(new BigDecimal(priceTextField.getText()));
        menuItem.setCategory(categoryTextField.getText());
        menuItem.setIngredients(ingredients);
        menuItem.setAllergens(allergens);

        //Gets the selected stations
        List<Station> selectedStations = new ArrayList<>();
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

    @FXML
    private void onClose(ActionEvent event) throws Exception {
        ViewHelper.loadView("/fxml/MenuItemConfig.fxml", nameTextField, springContext);
    }
}
