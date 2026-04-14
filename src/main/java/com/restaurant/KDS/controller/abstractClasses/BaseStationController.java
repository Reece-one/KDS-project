package com.restaurant.KDS.controller.abstractClasses;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.OrderStationService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BaseStationController {

    protected final OrderService orderService;
    protected final OrderStationService orderStationService;
    protected final ConfigurableApplicationContext springContext;


    @FXML
    protected FlowPane mainFlowPane;

    @FXML
    protected Label orderAmountLabel;

    @FXML
    protected ProgressBar analyticsBar;

    protected int onTime, completeOrders;

    protected BaseStationController(OrderService orderService, OrderStationService orderStationService, ConfigurableApplicationContext springContext) {
        this.orderService = orderService;
        this.orderStationService = orderStationService;
        this.springContext = springContext;
    }

    public abstract List<Order> getOrders();

    public abstract void onCardClick(Order order, VBox container);

    public abstract List<OrderItem> getOrderItems(Order order);

    public abstract Comparator<Node> getOrderCardComparator();

    public void createOrderCard(Order order) {
        VBox containerVbox = new VBox();
        containerVbox.getStyleClass().add("order-card-container");
        containerVbox.setUserData(order);

        //Creates the header for the order card
        HBox headerHbox = new HBox();
        if (orderService.isOnTime(order)) {
            headerHbox.setStyle("-fx-background-color: #8de969;"); // green
        } else {
            headerHbox.setStyle("-fx-background-color: #ff6b6b;"); // red
        }
        headerHbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        headerHbox.getStyleClass().add("order-card-header");
        headerHbox.setOnMouseClicked((event) -> {
            onCardClick(order,  containerVbox);
            if (java.time.Duration.between(order.getOpenedAt(), LocalDateTime.now()).toMinutes() < 1) {
                onTime++;
            }
            completeOrders++;
            getAnalytics();
        });

        //Sets the order icon depending on eat out or takeaway
        ImageView inOrOutIcon = new ImageView();
        if (order.getEatInOrTakeAway().equals("Eat In")) {
            inOrOutIcon.setImage(new Image(getClass().getResourceAsStream("/images/chair.png")));
        } else {
            inOrOutIcon.setImage(new Image(getClass().getResourceAsStream("/images/food-package.png")));
        }
        inOrOutIcon.setFitHeight(30);
        inOrOutIcon.setFitWidth(30);
        inOrOutIcon.setPreserveRatio(true);
        Label titleLabel = new Label(order.getTableOrName());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerHbox.getChildren().addAll(inOrOutIcon, spacer, titleLabel);
        containerVbox.getChildren().add(headerHbox);


        List<OrderItem> orderItems = getOrderItems(order);

        //Creates the main content of the order card (The order items and modifications)
        VBox mainContentVbox = new VBox();
        mainContentVbox.getStyleClass().add("order-card-main-content");

        //Creates a timer for every order card
        java.time.Duration elapsed = java.time.Duration.between(order.getOpenedAt(), java.time.LocalDateTime.now());
        long hours = elapsed.toHours();
        long minutes = elapsed.toMinutesPart();
        long seconds = elapsed.toSecondsPart();
        Label timeElapsedLabel = new Label(String.valueOf(elapsed));
        timeElapsedLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        timeElapsedLabel.getStyleClass().add("timer-label");

        for (OrderItem orderItem : orderItems) {
            VBox itemVbox = new VBox();
            HBox nameQuanitityHbox = new HBox();
            nameQuanitityHbox.getStyleClass().add("order-card-name-quantity");
            Label quantityText = new Label(orderItem.getQuantity().toString());
            quantityText.getStyleClass().add("order-card-quantity-text");
            Label nameText = new Label(orderItem.getMenuItem().getName());
            nameText.setWrapText(true);
            nameQuanitityHbox.getChildren().addAll(quantityText, nameText);
            itemVbox.getChildren().add(nameQuanitityHbox);
            if (orderItem.getModifications() != null && !orderItem.getModifications().isEmpty()) {
                String[] modifications = orderItem.getModifications().split(", ");
                for (String modification : modifications) {
                    Label modificationText = new Label(modification);
                    modificationText.getStyleClass().add("order-card-modification-text");
                    modificationText.setWrapText(true);
                    itemVbox.getChildren().add(modificationText);
                }
            }
            mainContentVbox.getChildren().add(itemVbox);
        }
        containerVbox.getChildren().add(timeElapsedLabel);
        containerVbox.getChildren().add(mainContentVbox);
        mainFlowPane.getChildren().add(containerVbox);
    }

    public void populateOpenOrders() {
        mainFlowPane.getChildren().clear();
        List<Order> openOrders = getOrders();
        for (Order order : openOrders) {
            createOrderCard(order);
        }
    }

    public void sortOrders() {
        List<Node> sorted = new ArrayList<>(mainFlowPane.getChildren());
        sorted.sort(getOrderCardComparator());
        mainFlowPane.getChildren().setAll(sorted);
    }


    public void getAnalytics() {
        double ratio = completeOrders == 0 ? 0.0 : (double) onTime / completeOrders;
        analyticsBar.setProgress(ratio);
    }

    public void refresh() {
        onTime = 0;
        completeOrders = 0;
        populateOpenOrders();
        sortOrders();
        orderAmountLabel.setText(String.valueOf(mainFlowPane.getChildren().size()));
        //Automatically refreshes the screen
        Timeline refresh = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            populateOpenOrders();
            sortOrders();
            orderAmountLabel.setText(String.valueOf(mainFlowPane.getChildren().size()));
        }));
        refresh.setCycleCount(Timeline.INDEFINITE);
        refresh.play();
    }

}
