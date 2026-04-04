package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.service.OrderItemService;
import com.restaurant.KDS.service.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainStationController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public MainStationController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @FXML
    private FlowPane mainFlowPane;

    @FXML
    private Label orderAmountLabel;

    @FXML
    public void createOrderCard (Order order) {
        VBox containerVbox = new VBox();

        //Creates the header for the order card
        HBox headerHbox = new HBox();
        ImageView inOrOutIcon = new ImageView();
        if (order.getEatInOrTakeAway().equals("Eat In")) {
            inOrOutIcon.setImage(new Image(getClass().getResourceAsStream("/images/chair.png")));
        } else {
            inOrOutIcon.setImage(new Image(getClass().getResourceAsStream("/images/food-package.png")));
        }
        inOrOutIcon.setFitHeight(20);
        inOrOutIcon.setFitWidth(20);
        inOrOutIcon.setPreserveRatio(true);
        Label titleLabel = new Label(order.getTableOrName());
        headerHbox.getChildren().addAll(inOrOutIcon, titleLabel);
        containerVbox.getChildren().add(headerHbox);

        //Creates the main content of the order card
        VBox mainContentVbox = new VBox();
        for (OrderItem orderItem : order.getOrderItems()) {
            VBox itemVbox = new VBox();
            HBox nameQuanitityHbox = new HBox();
            Text quantityText = new Text(orderItem.getQuantity().toString());
            Text nameText = new Text(orderItem.getMenuItem().getName());
            nameQuanitityHbox.getChildren().addAll(quantityText, nameText);
            itemVbox.getChildren().add(nameQuanitityHbox);
            if (orderItem.getModifications() != null && !orderItem.getModifications().isEmpty()) {
                String[] modifications = orderItem.getModifications().split(", ");
                for (String modification : modifications) {
                    Text modificationText = new Text(modification);
                    itemVbox.getChildren().add(modificationText);
                }
            }
            mainContentVbox.getChildren().add(itemVbox);
        }
        containerVbox.getChildren().add(mainContentVbox);
        mainFlowPane.getChildren().add(containerVbox);
    }

    @FXML
    public void populateOpenOrders () {
        mainFlowPane.getChildren().clear();
        List<Order> openOrders = orderService.findByStatus("Open");
        for (Order order : openOrders) {
            createOrderCard(order);
        }
    }

    @FXML
    public void initialize() {
        populateOpenOrders();
        int count = orderService.findByStatus("Open").size();
        orderAmountLabel.setText(String.valueOf(count));
    }
}
