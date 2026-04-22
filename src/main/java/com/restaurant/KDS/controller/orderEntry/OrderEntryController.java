package com.restaurant.KDS.controller.orderEntry;

import com.restaurant.KDS.controller.station.EditStationController;
import com.restaurant.KDS.entity.*;
import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.service.MenuService;
import com.restaurant.KDS.service.OrderItemService;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.OrderStationService;
import com.restaurant.KDS.util.PerfTimer;
import com.restaurant.KDS.util.ViewHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.restaurant.KDS.util.ViewHelper.loadView;

@Component
public class OrderEntryController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;
    private final MenuService menuService;
    private final OrderStationService orderStationService;
    private Order order;
    private final ConfigurableApplicationContext springContext;

    public OrderEntryController(OrderItemService orderItemService, OrderService orderService, MenuService menuService, ConfigurableApplicationContext springContext, OrderStationService orderStationService, OrderStationService orderStationService1) {
        this.orderItemService = orderItemService;
        this.orderService = orderService;
        this.menuService = menuService;
        this.springContext = springContext;
        this.orderStationService = orderStationService1;
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

    /**
     * Creates a blank {@link Order}
     */
    public void createBlankOrder() {
        if (order.getId() == null) {
            String tableName = tableNameField.getText();
            if (tableName == null || tableName.trim().isEmpty()) {
                tableName = "Unassigned";
            }
            order.setTableOrName(tableName);
            order.setEatInOrTakeAway(eatInCombo.getValue() != null ? eatInCombo.getValue() : "Eat In");
            order.setStatus("Pending");
            order.setTotal(BigDecimal.ZERO);
            orderService.saveOrder(order);
        }
    }

    /**
     * Creates a modification and adds it to {@code modificationsVbox}. Clicking the
     * modification removes it.
     *
     * @param prefix     the modification prefix, should be either 'Extra' or 'No'
     * @param ingredient the ingredient that should be added or removed
     */
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

    /**
     * Populates the {@code modificationVbox} with all the {@link MenuItem}s ingredients.
     * Each ingredient gets two {@link Button}s to add an extra/no modification.
     *
     * @param menuItem the menu item to get the ingredients from
     */
    public void populateIngredients(MenuItem menuItem) {
        selectedItemLabel.setText(menuItem.getName());

        modificationsVbox.getChildren().clear();
        Set<String> ingredients = menuItem.getIngredients();

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
            hBox.getStyleClass().add("order-entry-modifications");

            hBox.getChildren().addAll(name, extra, remove);
            modificationsVbox.getChildren().add(hBox);
        }

        modificationsVbox.getChildren().add(new Separator());
    }

    /**
     * Populates {menuItemsByCategory} depending on the category of {@link MenuItem}s.
     *
     * @param category the item's category
     */
    public void showByCategory(String category) {
        List<MenuItem> items = menuService.findByCategory(category);
        menuItemsByCategory.getChildren().clear();

        for (MenuItem item : items) {
            Button button = new Button();
            button.setWrapText(true);
            if (!item.getAvailable()) {
                button.setDisable(true);
            }
            if (item.getAllergens().isEmpty()) {
                button.setText(item.getName());
            } else {
                button.setText(item.getName() + "\nContains: " + item.getAllergens());
            }
            button.setOnAction(event -> {
                menuItem = item;
                populateIngredients(item);
            });
            button.getStyleClass().addAll("quaternary-button", "menu-item-card-button");
            menuItemsByCategory.getChildren().add(button);
        }
    }

    /**
     * Populates {@code categoryHbox} with buttons corresponding to every category
     */
    public void populateCategories() {
        List<String> categories = menuService.getCategories();
        categoryHbox.getChildren().clear();

        for (String category : categories) {
            Button button = new Button(category);
            button.setOnAction(event -> {
                showByCategory(category);
            });
            button.getStyleClass().add("tertiary-button");
            categoryHbox.getChildren().add(button);
        }
    }


    public void loadEditOrderView(ConfigurableApplicationContext springContext, Node node, OrderItem orderItem) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditOrderItem.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        EditOrderItemController controller = loader.getController();
        controller.setOrderItem(orderItem);

        Stage modal = new Stage();
        modal.setTitle("Edit order item");
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(node.getScene().getWindow());
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }

    /**
     * Populates {@code currentOrderVbox} with the list of {@link OrderItem}s added to
     * the order. Clicking the item loads the view to edit that order item.
     */
    public void populateCurrentOrder() {
        currentOrderVbox.getChildren().clear();
        List<OrderItem> items = order.getOrderItems();

        for (OrderItem item : items) {
            VBox vbox = new VBox();
            Label name = new Label(item.getMenuItem().getName());
            Label modification = new Label(item.getModifications());
            Label quantity = new Label("X " + item.getQuantity().toString());
            vbox.getChildren().addAll(name, modification, quantity);
            vbox.setOnMouseClicked(event -> {
                try {
                    loadEditOrderView(springContext, vbox, item);
                    populateCurrentOrder();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            currentOrderVbox.getChildren().add(vbox);
        }
    }

    @FXML
    public void addExtra() {
        if (!selectedItemLabel.getText().equals("No item selected")) {
            addModification("Add ", modificationTextField.getText());
        }
    }

    /**
     * Creates an {@link OrderStation} entry for every unique {@link Station} that exists
     * among the {@link OrderItem}s
     */
    @FXML
    public void createOrderStations() {
        Set<Station> stations = new HashSet<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            stations.addAll(orderItem.getMenuItem().getStations());
        }
        for (Station station : stations) {
            OrderStation orderStation = new OrderStation();
            orderStation.setOrder(order);
            orderStation.setStation(station);
            orderStationService.save(orderStation);
        }
    }

    /**
     * Updates the blank {@link Order} with the fields from the view and sets the status
     * to "Open". Resets the view so another order can be made.
     */
    @FXML
    public void onSubmit() {
        PerfTimer.time("OrderEntry.onSubmit", () -> {
            if (!order.getOrderItems().isEmpty() && !tableNameField.getText().isEmpty() && eatInCombo.getValue() != null) {
                order.setTableOrName(tableNameField.getText());
                order.setEatInOrTakeAway(eatInCombo.getValue());
                order.setStatus("Open");
                order.setTotal(orderItemService.getTotalByOrder(order));
                order.setOpenedAt(LocalDateTime.now());
                orderService.saveOrder(order);
                createOrderStations();

                //Reset so another order can be made
                order = new Order();
                currentOrderVbox.getChildren().clear();
                tableNameField.clear();
                eatInCombo.setValue(null);
                totalLabel.setText("£0.00");
                modificationsVbox.getChildren().clear();
                selectedItemLabel.setText("No item selected");
            } else {
                ViewHelper.showAlert("Please fill all the fields!");
            }
        });
    }

    /**
     * Creates an {@link OrderItem} from fields and adds it to the {@link Order}
     */
    @FXML
    public void addToOrder() {
        PerfTimer.time("OrderEntry.addToOrder", () -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);

            String mods = modificationsVbox.getChildren().stream()
                    .filter(node -> node instanceof Label)
                    .map(node -> ((Label) node).getText())
                    .collect(Collectors.joining(", ")); //Concatenates the modification and separates them with a comma
            orderItem.setModifications(mods);

            orderItem.setQuantity(quantitySpinner.getValue());
            createBlankOrder();
            orderItemService.saveOrderItem(orderItem);
            order = orderService.findById(order.getId()).get();
            populateCurrentOrder();
            totalLabel.setText("£ " + orderItemService.getTotalByOrder(order));
        });
    }

    /**
     * Deletes all {@link OrderItem}s from the current order.
     */
    @FXML
    public void onClear() {
        orderItemService.deleteByOrder(order);
        currentOrderVbox.getChildren().clear();
        totalLabel.setText("£0.00");
    }

    @FXML
    public void initialize() {
        populateCategories();
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));
        eatInCombo.getItems().addAll("Eat In", "Takeaway");
    }

}
