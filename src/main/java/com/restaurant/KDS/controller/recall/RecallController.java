package com.restaurant.KDS.controller.recall;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.service.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecallController {

    @FXML
    private VBox recallVbox;


    private final OrderService orderService;

    public RecallController(OrderService orderService) {
        this.orderService = orderService;
    }

    @FXML
    public void initialize() {
        List<Order> completeOrders = orderService.findByStatus("Complete");
        for (Order order : completeOrders) {
            Label orderDetails = new Label(order.getId().toString() + "     "
                    + order.getTableOrName() + "     "
                    + order.getOpenedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern("hh:mma"))
            );

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

            orderDetails.setOnMouseClicked(event -> {
                boolean showing = itemVbox.isVisible();
                itemVbox.setVisible(!showing);
                itemVbox.setManaged(!showing);
            });

            recallVbox.getChildren().add(orderDetails);
            recallVbox.getChildren().add(itemVbox);
        }
    }

}

