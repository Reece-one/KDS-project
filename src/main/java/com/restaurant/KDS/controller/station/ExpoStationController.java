package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.controller.abstractClasses.BaseStationController;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.OrderStationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class ExpoStationController extends  BaseStationController {

    public ExpoStationController(OrderService orderService, OrderStationService orderStationService, ConfigurableApplicationContext springContext) {
        super(orderService, orderStationService, springContext);
    }

    @Override
    public List<Order> getOrders() {
       return orderService.findByStatus("Open");
    }

    @Override
    public void onCardClick(Order order, VBox container) {
        completeOrder(order);
        if (Duration.between(order.getOpenedAt(), LocalDateTime.now()).toMinutes() < 1) {
            onTime ++;
        }
        completeOrders ++;
        getAnalytics();
        container.getChildren().clear();
        ((Pane) container.getParent()).getChildren().remove(container);
    }

    @Override
    public List<OrderItem> getOrderItems(Order order) {
        return order.getOrderItems();
    }

    //Compares orders by status for sorting. Orders by "Recalled" first then openedAt time second
    @Override
    public Comparator<Node> getOrderCardComparator() {
        return (a, b) -> {
            Order orderA = (Order) a.getUserData();
            Order orderB = (Order) b.getUserData();

            boolean aRecalled = orderA.getStatus().equals("Recalled");
            boolean bRecalled = orderB.getStatus().equals("Recalled");

            if (aRecalled && !bRecalled) return -1;
            if (!aRecalled && bRecalled) return 1;
            return orderA.getOpenedAt().compareTo(orderB.getOpenedAt());
        };
    }

    @FXML
    public void completeOrder(Order order) {
        if (orderStationService.allStationsCompleted(order)) {
            order.setStatus("Complete");
            order.setCompletedAt(LocalDateTime.now());
            orderService.saveOrder(order);
        }
    }

    @FXML
    public void onRecall() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpoRecallView.fxml"));
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
        refresh();
    }

}
