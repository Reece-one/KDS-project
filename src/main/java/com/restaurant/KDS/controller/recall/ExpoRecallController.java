package com.restaurant.KDS.controller.recall;

import com.restaurant.KDS.controller.settings.SettingsController;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.service.OrderService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

@Component
public class ExpoRecallController {

    @FXML
    private VBox recallVbox;

    private final OrderService orderService;

    public ExpoRecallController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Gets all complete {@link Order}s and creates a root {@link Node} for each and
     * populates it with the details and a checkbox. Clicking the order node
     * expands it to show more details about the order.
     */
    @FXML
    public void initialize() {
        List<Order> completeOrders = orderService.findByStatus("Complete");
        for (Order order : completeOrders) {
            HBox orderCard = new HBox();
            Label id = new Label(order.getId().toString());
            Label name = new Label(order.getTableOrName());
            Label time = new Label(order.getOpenedAt().format(java.time.format.DateTimeFormatter.ofPattern("hh:mma")));
            Label preview = new Label(order.getOrderItems().stream().findFirst().get().getMenuItem().getName());
            CheckBox select = new CheckBox();
            select.setUserData(order);

            orderCard.getChildren().addAll(id, name, time, preview, select);
            orderCard.setSpacing(20);
            orderCard.setPrefWidth(Double.MAX_VALUE);

            VBox itemVbox = new VBox();
            itemVbox.setVisible(false);
            itemVbox.setManaged(false);

            for (OrderItem orderItem : order.getOrderItems()) {
                HBox nameQuantityHbox = new HBox();
                Label quantityText = new Label(orderItem.getQuantity().toString());
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
        int size = prefs.getInt("fontSize_" + 0L, 24);
        if (size != 24) {
            recallVbox.lookupAll(".label").forEach(label -> {
                label.setStyle("-fx-font-size: " + size + "px;");
            });
        }
        //Dark mode
        boolean isDark = prefs.getBoolean("darkMode_" + 0L, false);
        Platform.runLater(() -> {
            if (isDark) {
                recallVbox.getScene().getRoot().getStylesheets().clear();
                recallVbox.getScene().getRoot().getStylesheets().add(SettingsController.class.getResource("/css/dark-styles.css").toExternalForm());
            }
        });
    }

    /**
     * All {@link Order} nodes that are checked have their status changed to "Recalled"
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
        //Set all selected orders to "Recalled"
        for (Order order : selectedOrders) {
            order.setStatus("Recalled");
            orderService.saveOrder(order);
        }
        recallVbox.getChildren().clear();
        initialize();
    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) recallVbox.getScene().getWindow();
        stage.close();
    }
}

