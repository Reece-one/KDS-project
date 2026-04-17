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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.restaurant.KDS.util.ViewHelper.showAlert;

@Component
public class EditMenuItemController {

    private final MenuService menuService;
    private final StationService stationService;
    private final ConfigurableApplicationContext springContext;

    private MenuItem menuItem;
    private Set<String> ingredients;
    private Set<String> allergens;
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
    }

    /**
     * Populates all fields with {@link MenuItem} details.
     *
     * @param menuItem the menu item that is represented
     */
    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        this.allStations = stationService.getAllStations();
        this.ingredients = new HashSet<>(menuItem.getIngredients());
        this.allergens = new HashSet<>(menuItem.getAllergens());

        nameTextField.setText(menuItem.getName());
        priceTextField.setText(menuItem.getPrice().toString());
        categoryTextField.setText(menuItem.getCategory());
        timeTextField.setText(menuItem.getPrepTimeMinutes().toString());
        availableCheckBox.setSelected(menuItem.getAvailable());

        refreshTags(ingredients, ingredientsFlowPane);
        refreshTags(allergens, allergensFlowPane);
        loadStationCheckboxes();
    }

    /**
     * Adds tags to the {@link FlowPane} for every {@link String} in the {@link Set}.
     *
     * @param set the set which the tags are composed of
     * @param flowPane the flow pane where the tags are added
     */
    private void refreshTags(Set<String> set, FlowPane flowPane) {
        flowPane.getChildren().clear();
        for (String item : set) {
            Button button = new Button(item);
            button.setOnAction(event -> {
                set.remove(item);
                flowPane.getChildren().remove(button);
            });
            button.getStyleClass().add("tertiary-button");
            flowPane.getChildren().add(button);
        }
    }

    /**
     * Gets all {@link Station}s and creates a {@link CheckBox} for each and injects them
     * into the {@code StationFlowPane}. Any stations that this {@link MenuItem}
     * is linked to are checked by default
     */
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

    /**
     * an entry into the {@link TextField} adds the item into a {@link Set} and creates a
     * and refreshed the {@link FlowPane}
     *
     * @param textField the name of the tag
     * @param set       the set of tags
     * @param flowPane  the flow pane where the tags are injected
     */
    private void addTag(TextField textField, Set<String> set, FlowPane flowPane) {
        String value = textField.getText();
        if (value == null || value.trim().isEmpty()) return;
        set.add(value);
        textField.clear();
        refreshTags(set, flowPane);
    }

    @FXML
    private void addIngredient() {
        addTag(ingredientTextField, ingredients, ingredientsFlowPane);
    }

    @FXML
    private void addAllergen() {
        addTag(allergenTextField, allergens, allergensFlowPane);
    }

    /**
     * Saves the changes to the current {@link MenuItem} and closes the stage
     */
    @FXML
    private void onSave() throws Exception {
        if (nameTextField.getText().trim().isEmpty()
                || priceTextField.getText().trim().isEmpty()
                || categoryTextField.getText().trim().isEmpty()
                || timeTextField.getText().trim().isEmpty()) {
            showAlert("Please fill in all required fields");
            return;
        }

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
