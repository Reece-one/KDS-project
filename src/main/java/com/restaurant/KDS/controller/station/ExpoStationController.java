package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.OrderStationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExpoStationController {

    private final OrderService orderService;
    private final OrderStationService orderStationService;
    private final ConfigurableApplicationContext springContext;

    public ExpoStationController(OrderService orderService, OrderStationService orderStationService, ConfigurableApplicationContext springContext) {
        this.orderService = orderService;
        this.orderStationService = orderStationService;
        this.springContext = springContext;
    }

    @FXML
    private FlowPane mainFlowPane;

    @FXML
    private Label orderAmountLabel;

    @FXML
    public void completeOrder(Order order) {
        if (orderStationService.allStationsCompleted(order)) {
            order.setStatus("Complete");
            order.setCompletedAt(LocalDateTime.now());
            orderService.saveOrder(order);
        }
    }

    @FXML
    public void createOrderCard (Order order) {
        VBox containerVbox = new VBox();

        //Creates the header for the order card
        HBox headerHbox = new HBox();
        headerHbox.setOnMouseClicked((event) -> {
            completeOrder(order);
            containerVbox.getChildren().clear();
            ((Pane) containerVbox.getParent()).getChildren().remove(containerVbox);
        });
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


        //Creates the main content of the order card (The order items and modifications)
        List<OrderItem> orderItems = order.getOrderItems();
        VBox mainContentVbox = new VBox();
        for (OrderItem orderItem : orderItems) {
            VBox itemVbox = new VBox();
            HBox nameQuanitityHbox = new HBox();
            Label quantityText = new Label(orderItem.getQuantity().toString());
            Label nameText = new Label(orderItem.getMenuItem().getName());
            nameQuanitityHbox.getChildren().addAll(quantityText, nameText);
            itemVbox.getChildren().add(nameQuanitityHbox);
            if (orderItem.getModifications() != null && !orderItem.getModifications().isEmpty()) {
                String[] modifications = orderItem.getModifications().split(", ");
                for (String modification : modifications) {
                    Label modificationText = new Label(modification);
                    itemVbox.getChildren().add(modificationText);
                }
            }
            mainContentVbox.getChildren().add(itemVbox);
        }
        containerVbox.getChildren().add(mainContentVbox);
        mainFlowPane.getChildren().add(containerVbox);
    }


    @FXML
    //Populates the screen with all open orders
    public void populateOpenOrders () {
        mainFlowPane.getChildren().clear();
        List<Order> openOrders = orderService.findByStatus("Open");
        for (Order order : openOrders) {
            createOrderCard(order);
        }
    }

    @FXML
    public void onRecall() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecallView.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.setTitle("Recall");
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(mainFlowPane.getScene().getWindow());
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }

    @FXML
    public void initialize() {
        populateOpenOrders();
        orderAmountLabel.setText(String.valueOf(orderService.findByStatus("Open").size()));
    }
}
