package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.controller.abstractClasses.BaseStationController;
import com.restaurant.KDS.controller.settings.SettingsController;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.service.AiService;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.OrderStationService;
import com.restaurant.KDS.util.ViewHelper;
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
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.Preferences;

@Component
public class ExpoStationController extends BaseStationController {

    public ExpoStationController(OrderService orderService, OrderStationService orderStationService, ConfigurableApplicationContext springContext, AiService aiService) {
        super(orderService, orderStationService, springContext, aiService);
    }

    @Override
    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        orders.addAll(orderService.findByStatus("Open"));
        orders.addAll(orderService.findByStatus("Recalled"));
        return orders;
    }

    @Override
    public void onCardClick(Order order, VBox container) {
        completeOrder(order, container);

    }

    @Override
    public Long getStationId() {
        return 0L; // expo has no station, use 0 as its key
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

    //Completes the order depending on strict expo bump setting
    @FXML
    public void completeOrder(Order order, VBox container) {
        Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
        if (prefs.getBoolean("bump", true)) {
            if (!orderStationService.allStationsCompleted(order)) return;
        } else {
            List<OrderStation> stations = orderStationService.findByOrder(order);
            for (OrderStation station : stations) {
                station.setCompleted(true);
                orderStationService.save(station);
            }
        }

        order.setStatus("Complete");
        order.setCompletedAt(LocalDateTime.now());
        orderService.saveOrder(order);

        if (orderService.isOnTime(order)) {
            onTime++;
        }
        completeOrders++;
        getAnalytics();

        //Add how long it took to complete the order to completedTimes
        long minutes = java.time.Duration.between(order.getOpenedAt(), LocalDateTime.now()).toMinutes();
        completedTimes.add("{ minutes: " + minutes + "completed_at: " + order.getCompletedAt() + "}");

        container.getChildren().clear();
        ((Pane) container.getParent()).getChildren().remove(container);
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
    public void onSettings() throws IOException {
        FXMLLoader loader = new FXMLLoader(ViewHelper.class.getResource("/fxml/SettingsView.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        SettingsController settingsController = loader.getController();
        settingsController.setStationScene(mainFlowPane.getScene());
        settingsController.setStationId(0L);

        Stage stage = (Stage) mainFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void initialize() {
        refresh();
    }

}
