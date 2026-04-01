package com.restaurant.KDS.controller.menuItem;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.MenuService;
import com.restaurant.KDS.service.StationService;
import com.restaurant.KDS.util.ViewHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class EditMenuItemController {

    private final MenuService menuService;
    private final StationService stationService;
    private final ConfigurableApplicationContext springContext;

    private MenuItem menuItem;
    private List<String> ingredients;
    private List<String> allergens;
    private List<Station> allStations;

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

    public EditMenuItemController(MenuService menuService, StationService stationService, ConfigurableApplicationContext springContext) {
        this.menuService = menuService;
        this.stationService = stationService;
        this.springContext = springContext;
        this.allStations = stationService.getAllStations();
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        this.ingredients = new ArrayList<>(menuItem.getIngredients());
        this.allergens = new ArrayList<>(menuItem.getAllergens());

        nameTextField.setText(menuItem.getName());
        priceTextField.setText(menuItem.getPrice().toString());
        categoryTextField.setText(menuItem.getCategory());
        timeTextField.setText(menuItem.getPrepTimeMinutes().toString());
        availableCheckBox.setSelected(menuItem.getAvailable());

        refreshTags(ingredients, ingredientsFlowPane);
        refreshTags(allergens, allergensFlowPane);
        loadStationCheckboxes();
    }

    private void refreshTags(List<String> list, FlowPane flowPane) {
        flowPane.getChildren().clear();
        for (String item : list) {
            Button button = new Button(item);
            button.setOnAction(event -> {
                list.remove(item);
                flowPane.getChildren().remove(button);
            });
            flowPane.getChildren().add(button);
        }
    }

    private void loadStationCheckboxes() {
        stationFlowPane.getChildren().clear();
        List<Station> itemStations = menuItem.getStations();
        for (Station station : allStations) {
            CheckBox checkBox = new CheckBox(station.getName());
            checkBox.setUserData(station);
            checkBox.setSelected(itemStations.stream()
                    .anyMatch(s -> s.getId().equals(station.getId())));
            stationFlowPane.getChildren().add(checkBox);
        }
    }

    private void addTag(TextField textField, List<String> list, FlowPane flowPane) {
        String value = textField.getText();
        if (value == null || value.trim().isEmpty()) return;
        list.add(value);
        textField.clear();
        refreshTags(list, flowPane);
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
    private void onSave() throws Exception {
        menuItem.setName(nameTextField.getText());
        menuItem.setPrice(new BigDecimal(priceTextField.getText()));
        menuItem.setCategory(categoryTextField.getText());
        menuItem.setIngredients(ingredients);
        menuItem.setAllergens(allergens);

        List<Station> selectedStations = new ArrayList<>();
        for (var node : stationFlowPane.getChildren()) {
            CheckBox cb = (CheckBox) node;
            if (cb.isSelected()) {
                selectedStations.add((Station) cb.getUserData());
            }
        }

        menuItem.setStations(selectedStations);
        menuItem.setAvailable(availableCheckBox.isSelected());
        menuItem.setPrepTimeMinutes(Integer.parseInt(timeTextField.getText()));
        menuService.saveMenuItem(menuItem);

        ViewHelper.loadView("/fxml/MenuItemConfig.fxml", nameTextField, springContext);
    }

    @FXML
    private void onDelete() throws Exception {
        menuService.deleteMenuItem(menuItem);
        ViewHelper.loadView("/fxml/MenuItemConfig.fxml", nameTextField, springContext);
    }
}
