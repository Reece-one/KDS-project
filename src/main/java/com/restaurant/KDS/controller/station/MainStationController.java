package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.controller.abstractClasses.BaseStationController;
import com.restaurant.KDS.controller.recall.RecallController;
import com.restaurant.KDS.controller.settings.SettingsController;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.entity.Station;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@Component
@Scope("prototype")
public class MainStationController extends BaseStationController {


    private Station station;

    public MainStationController(OrderService orderService, OrderStationService orderStationService, ConfigurableApplicationContext springContext, AiService aiService) {
        super(orderService, orderStationService, springContext, aiService);
    }

    //Gets only orders that have at least one item that corresponds to the current station
    @Override
    public List<Order> getOrders() {
        return orderService.findAll().stream()
                .filter(order -> order.getStatus().equals("Open") || order.getStatus().equals("Complete"))
                .filter(order -> orderStationService.findByOrderAndStation(order, station)
                        .map(os -> !os.isCompleted())
                        .orElse(false))
                .toList();
    }

    @Override
    public void onCardClick(Order order, VBox container) {
        orderStationService.markComplete(order, station);
        container.getChildren().clear();
        ((Pane) container.getParent()).getChildren().remove(container);

        if (orderService.isOnTime(order)) {
            onTime++;
        }
        completeOrders++;
        getAnalytics();
    }

    //Gets only the order items that correspond to the station
    @Override
    public List<OrderItem> getOrderItems(Order order) {
        return order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getMenuItem().getStations().stream()
                        .anyMatch(s -> s.getId().equals(station.getId())))
                .toList();
    }

    //Compares orders stations by status for sorting
    @Override
    public Comparator<Node> getOrderCardComparator() {
        return (a, b) -> {
            Order orderA = (Order) a.getUserData();
            Order orderB = (Order) b.getUserData();

            boolean aRecalled = orderStationService.findByOrderAndStation(orderA, station)
                    .map(OrderStation::isRecalled).orElse(false);
            boolean bRecalled = orderStationService.findByOrderAndStation(orderB, station)
                    .map(OrderStation::isRecalled).orElse(false);

            if (aRecalled && !bRecalled) return -1;
            if (!aRecalled && bRecalled) return 1;
            return orderA.getOpenedAt().compareTo(orderB.getOpenedAt());
        };
    }

    @Override
    public Long getStationId() {
        return station != null ? station.getId() : null;
    }

    public void setStation(Station station) {
        this.station = station;
        refresh();
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
    public void onSettings() throws IOException {
        FXMLLoader loader = new FXMLLoader(ViewHelper.class.getResource("/fxml/SettingsView.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        SettingsController settingsController = loader.getController();
        settingsController.setStationScene(mainFlowPane.getScene());
        settingsController.setStation(station);

        Stage stage = (Stage) mainFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void initialize() {
    }
}
