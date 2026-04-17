package com.restaurant.KDS.controller.recall;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.OrderStationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import com.restaurant.KDS.controller.settings.SettingsController;

@Component
@Scope("prototype")
public class RecallController {

    private final OrderStationService orderStationService;
    @FXML
    private VBox recallVbox;


    private final OrderService orderService;
    private Station station;

    public RecallController(OrderService orderService, OrderStationService orderStationService) {
        this.orderService = orderService;
        this.orderStationService = orderStationService;
    }

    /*
    @FXML
    //Gets only orders that have at least one item that corresponds to the current station
    public List<Order> completeOrdersByStation() {
        return orderService.findByStatus("Complete").stream()
                .filter(orders -> orders.getOrderItems().stream()
                        .anyMatch(orderItems -> orderItems.getMenuItem().getStations().stream()
                                .anyMatch(s -> s.getId().equals(station.getId()))))
                .toList();
    }
     */

    /**
     *  Gets only the {@link OrderItem}s that correspond to the {@link Station}.
     *
     * @param order the {@link Order} to get the order items come from
     * @return
     */
    public List<OrderItem> completeOrderItemsByOrder(Order order) {
        return order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getMenuItem().getStations().stream()
                        .anyMatch(s -> s.getId().equals(station.getId())))
                .toList();
    }

    /**
     * The initializing method. Gets all {@link Order}s where the {@link OrderStation}
     * linked to this {@link Station} "is completed" column is set to {@code true}.
     * creates a root {@link Node} for each and populates it with the details
     * and a checkbox. Clicking the order node expands it to show more details
     * about the order.
     *
     * @param station the station linked to this instance
     */
    @FXML
    public void setStation(Station station) {
        this.station = station;

        for (Order order : orderStationService.findCompletedOrdersByStation(station)) {
            List<OrderItem> orderItems = completeOrderItemsByOrder(order);
            HBox orderCard = new HBox();
            Label id = new Label(order.getId().toString());
            Label name = new Label(order.getTableOrName());
            Label time = new Label(order.getOpenedAt().format(java.time.format.DateTimeFormatter.ofPattern("hh:mma")));
            Label preview = new Label(orderItems.getFirst().getMenuItem().getName());
            CheckBox select = new CheckBox();
            select.setUserData(order);

            orderCard.getChildren().addAll(id, name, time, preview, select);
            orderCard.setSpacing(20);
            orderCard.setPrefWidth(Double.MAX_VALUE);

            VBox itemVbox = new VBox();
            itemVbox.setVisible(false);
            itemVbox.setManaged(false);

            for (OrderItem orderItem : orderItems) {
                HBox nameQuantityHbox = new HBox();
                Label quantityText = new Label(orderItem.getQuantity().toString() + " ");
                Label nameText = new Label(orderItem.getMenuItem().getName());
                nameQuantityHbox.getChildren().addAll(quantityText, nameText);
                itemVbox.getChildren().add(nameQuantityHbox);
                if (orderItem.getModifications() != null && !orderItem.getModifications().isEmpty()) {
                    String[] modifications = orderItem.getModifications().split(", ");
                    for (String modification : modifications) {
                        Label modificationText = new Label(modification);
                        itemVbox.getChildren().add(modificationText);
                    }
                }
            }

            orderCard.setOnMouseClicked(event -> {
                boolean showing = itemVbox.isVisible();
                itemVbox.setVisible(!showing);
                itemVbox.setManaged(!showing);
            });

            recallVbox.getChildren().add(orderCard);
            recallVbox.getChildren().add(itemVbox);
        }

        // Apply saved preferences
        Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
        //Font size
        int size = prefs.getInt("fontSize_" + station.getId(), 24);
        if (size != 24) {
            recallVbox.lookupAll(".label").forEach(label -> {
                label.setStyle("-fx-font-size: " + size + "px;");
            });
        }
        //Dark mode
        boolean isDark = prefs.getBoolean("darkMode_" + station.getId(), false);
        Platform.runLater(() -> {
        if (isDark) {
            recallVbox.getScene().getRoot().getStylesheets().clear();
            recallVbox.getScene().getRoot().getStylesheets().add(SettingsController.class.getResource("/css/dark-styles.css").toExternalForm());
        }
        });
    }

    @FXML
    public void initialize() {
    }

    /**
     * All {@link Order} nodes that are checked have their corresponding {@link OrderStation}
     * "is completed" column set to {@code false} and "is recalled" column set to
     * {@code false}.
     */
    @FXML
    public void onRecall() {
        //Get the checked orders from the checkbox data
        List<Order> selectedOrders = new ArrayList<>();
        for (Node node : recallVbox.getChildren()) {
            if (node instanceof HBox hBox) {
                for (Node child : hBox.getChildren())
                    if (child instanceof CheckBox checkbox && checkbox.isSelected()) {
                        selectedOrders.add((Order) checkbox.getUserData());
                    }
            }
        }
        //Set orderStation completed to false
        for (Order order : selectedOrders) {
            orderStationService.findByOrderAndStation(order, station).ifPresent(orderStation -> {
                orderStation.setCompleted(false);
                orderStation.setRecalled(true);
                orderStationService.save(orderStation);
            });
            orderService.saveOrder(order);
        }
        recallVbox.getChildren().clear();
        setStation(station);
    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) recallVbox.getScene().getWindow();
        stage.close();
    }
}

