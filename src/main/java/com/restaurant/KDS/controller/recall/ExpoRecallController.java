package com.restaurant.KDS.controller.recall;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.service.OrderService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExpoRecallController {

    @FXML
    private VBox recallVbox;


    private final OrderService orderService;

    public ExpoRecallController(OrderService orderService) {
        this.orderService = orderService;
    }

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
    }

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

