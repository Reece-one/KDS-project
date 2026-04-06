package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.Station;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class MainStationController {

    private final OrderService orderService;
    private Station station;

    public MainStationController(OrderService orderService) {
        this.orderService = orderService;
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

        //Gets only the order items that correspond to the station
        List<OrderItem> orderItems = order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getMenuItem().getStations().stream()
                        .anyMatch(s -> s.getId().equals(station.getId())))
                .toList();

        //Creates the main content of the order card (The order items and modifications)
        VBox mainContentVbox = new VBox();
        for (OrderItem orderItem : orderItems) {
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
    //Gets only orders that have at least one item that corresponds to the current station
    public List<Order> openOrdersByStation () {
        List<Order> openOrders = orderService.findByStatus("Open").stream()
                .filter(orders -> orders.getOrderItems().stream()
                        .anyMatch(orderItems -> orderItems.getMenuItem().getStations().stream()
                                .anyMatch(s -> s.getId().equals(station.getId()))))
                .toList();
        return openOrders;
    }

    @FXML
    //Populates the screen with necessary open orders
    public void populateOpenOrders () {
        mainFlowPane.getChildren().clear();
        List<Order> openOrders = openOrdersByStation();
        for (Order order : openOrders) {
            createOrderCard(order);
        }
    }

    public void setStation(Station station) {
        this.station = station;
        populateOpenOrders();
        orderAmountLabel.setText(String.valueOf(openOrdersByStation().size()));
    }

    @FXML
    public void initialize() {
    }
}
