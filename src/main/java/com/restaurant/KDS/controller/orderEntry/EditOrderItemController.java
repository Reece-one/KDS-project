package com.restaurant.KDS.controller.orderEntry;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.MenuService;
import com.restaurant.KDS.service.OrderItemService;
import com.restaurant.KDS.service.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EditOrderItemController {
    private final OrderItemService orderItemService;
    private final OrderService orderService;
    private final MenuService menuService;
    private Order order;
    private final ConfigurableApplicationContext springContext;

    public EditOrderItemController(OrderItemService orderItemService, OrderService orderService, MenuService menuService, ConfigurableApplicationContext springContext) {
        this.orderItemService = orderItemService;
        this.orderService = orderService;
        this.menuService = menuService;
        this.springContext = springContext;
        this.order = new Order();
    }

    @FXML
    private HBox categoryHbox;

    @FXML
    private FlowPane menuItemsByCategory;

    @FXML
    private Label selectedItemLabel;

    @FXML
    private VBox modificationsVbox;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private TextField modificationTextField;

    @FXML
    private VBox currentOrderVbox;

    @FXML
    private TextField tableNameField;

    @FXML
    private ComboBox<String> eatInCombo;

    @FXML
    private Label totalLabel;

    private MenuItem menuItem;

    private OrderItem orderItem;

    public void populateModifications(OrderItem orderItem) {
        String[] items = orderItem.getModifications().split(", ");
        for (String item : items) {
            Label label = new Label(item);
            label.setOnMouseClicked(event -> {
                ((VBox) label.getParent()).getChildren().remove(label); //Can remove modification by clicking it
            });
            modificationsVbox.getChildren().add(label);
        }
    }

    public void setOrderItem(OrderItem item) {
        this.orderItem = item;
        selectedItemLabel.setText(item.getMenuItem().getName());
        populateIngredients(item.getMenuItem());
        populateModifications(item);
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, orderItem.getQuantity()));
    }


    //Creates the modification, prefix should be either 'Extra' or 'No'
    public void addModification(String prefix, String ingredient) {
        boolean exists = modificationsVbox.getChildren().stream() //Checks if the modification already exists
                .filter(node -> node instanceof Label)
                .map(node -> ((Label) node).getText())
                .anyMatch(text -> text.equals(prefix + ingredient));

        if (!exists) {
            Label label = new Label(prefix + ingredient);
            label.setOnMouseClicked(event -> {
                ((VBox) label.getParent()).getChildren().remove(label); //Can remove modification by clicking it
            });
            modificationsVbox.getChildren().add(label);
        }
    }

    public void populateIngredients(MenuItem menuItem) {
        selectedItemLabel.setText(menuItem.getName());

        modificationsVbox.getChildren().clear();
        List<String> ingredients = menuItem.getIngredients();

        for (String ingredient : ingredients) {
            HBox hBox = new HBox();
            Label name = new Label(ingredient);
            Button extra = new Button("+");
            extra.setOnAction(extraEvent -> {
                addModification("Extra ", ingredient);
            });
            Button remove = new Button("-");
            remove.setOnAction(extraEvent -> {
                addModification("No ", ingredient);
            });

            hBox.getChildren().addAll(name, extra, remove);
            modificationsVbox.getChildren().add(hBox);
        }

        modificationsVbox.getChildren().add(new Separator());
    }

    public void showByCategory(String category) {
    }

    public void populateCategories() {
    }

    public void populateCurrentOrder() {
    }

    @FXML
    public void addExtra() {
        if (!selectedItemLabel.getText().equals("No item selected")) {
            addModification("Add ", modificationTextField.getText());
        }
    }

    @FXML
    public void onSubmit() {
    }

    @FXML
    public void editOrderItem() {
        String mods = modificationsVbox.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> ((Label) node).getText())
                .collect(Collectors.joining(", ")); //Concatenates the modification and separates them with a comma
        orderItem.setModifications(mods);

        orderItem.setQuantity(quantitySpinner.getValue());
        orderItemService.saveOrderItem(orderItem);
        Stage stage = (Stage) selectedItemLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onClear() {
    }

    @FXML
    public void initialize() {

    }
}
