package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.controller.orderEntry.EditOrderItemController;
import com.restaurant.KDS.controller.recall.RecallController;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.OrderItemService;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.OrderStationService;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.awt.SystemColor.text;

@Component
@Scope("prototype")
public class MainStationController {

    private final OrderService orderService;
    private final OrderStationService orderStationService;
    private Station station;
    private final ConfigurableApplicationContext springContext;

    public MainStationController(OrderService orderService, OrderStationService orderStationService, ConfigurableApplicationContext springContext) {
        this.orderService = orderService;
        this.orderStationService = orderStationService;
        this.springContext = springContext;
    }

    @FXML
    private FlowPane mainFlowPane;

    @FXML
    private Label orderAmountLabel;

    @FXML
    public void createOrderCard(Order order) {
        VBox containerVbox = new VBox();
        containerVbox.setUserData(order);

        //Creates the header for the order card
        HBox headerHbox = new HBox();
        headerHbox.setOnMouseClicked((event) -> {
            orderStationService.markComplete(order, station);
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
    //Gets only orders that have at least one item that corresponds to the current station
    public List<Order> openOrdersByStation() {
        return orderService.findByStatus("Open").stream()
                .filter(orders -> orders.getOrderItems().stream()
                        .anyMatch(orderItems -> orderItems.getMenuItem().getStations().stream()
                                .anyMatch(s -> s.getId().equals(station.getId()))))
                .toList();
    }

    @FXML
    //Populates the screen with necessary open orders
    public void populateOpenOrders() {
        mainFlowPane.getChildren().clear();
        //Get orders where this station is marked incomplete
        List<Order> orders = orderService.findAll().stream()
                .filter(order -> order.getStatus().equals("Open") || order.getStatus().equals("Complete"))
                .filter(order -> orderStationService.findByOrderAndStation(order, station)
                .map(os -> !os.isCompleted())
                .orElse(false))
        .toList();
        for (Order order : orders) {
            createOrderCard(order);
        }
    }

    @FXML
    //Sorts orders by recalled first, opened at time second
    public void sortOrders() {
        List<Node> sorted = new ArrayList<>(mainFlowPane.getChildren());
        sorted.sort((a, b) -> {
            Order orderA = (Order) a.getUserData();
            Order orderB = (Order) b.getUserData();

            boolean aRecalled = orderStationService.findByOrderAndStation(orderA, station)
                    .map(OrderStation::isRecalled).orElse(false);
            boolean bRecalled = orderStationService.findByOrderAndStation(orderB, station)
                    .map(OrderStation::isRecalled).orElse(false);

            if (aRecalled && !bRecalled) return -1;
            if (!aRecalled && bRecalled) return 1;
            return orderA.getOpenedAt().compareTo(orderB.getOpenedAt());
        });
        mainFlowPane.getChildren().setAll(sorted);
    }

    public void setStation(Station station) {
        this.station = station;
        populateOpenOrders();
        sortOrders();
        orderAmountLabel.setText(String.valueOf(mainFlowPane.getChildren().size()));

        Timeline refresh = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            populateOpenOrders();
            sortOrders();
            orderAmountLabel.setText(String.valueOf(mainFlowPane.getChildren().size()));
        }));
        refresh.setCycleCount(Timeline.INDEFINITE);
        refresh.play();
    }

    @FXML
    public void onRecall() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecallView.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        RecallController recallController = loader.getController();
        recallController.setStation(station);

        Stage modal = new Stage();
        modal.setTitle("Recall");
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(mainFlowPane.getScene().getWindow());
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }

    @FXML
    public void initialize() {
    }
}
