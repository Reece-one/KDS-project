package com.restaurant.KDS.ui;

import com.restaurant.KDS.controller.station.MainStationController;
import com.restaurant.KDS.entity.*;
import com.restaurant.KDS.repository.MenuItemRepository;
import com.restaurant.KDS.repository.OrderItemRepository;
import com.restaurant.KDS.repository.OrderRepository;
import com.restaurant.KDS.repository.OrderStationRepository;
import com.restaurant.KDS.repository.StationRepository;
import com.restaurant.KDS.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(ApplicationExtension.class)

public class StationViewTest {

    @Autowired
    private ConfigurableApplicationContext springContext;
    @Autowired
    private MenuService menuService;
    @Autowired
    private StationService stationService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderStationService orderStationService;
    @Autowired
    private OrderStationRepository orderStationRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private StationRepository stationRepository;

    @AfterEach
    void cleanup() {
        orderStationRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        menuItemRepository.deleteAll();
        stationRepository.deleteAll();
    }

    @Start
    public void start(Stage stage) throws Exception {
        Station grill = new Station();
        grill.setName("Grill");
        stationService.saveStation(grill);

        MenuItem item = new MenuItem();
        item.setName("Burger");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setCategory("Main");
        item.setAvailable(true);
        item.setPrepTimeMinutes(3);
        item.setIngredients(new HashSet<>(List.of("bread")));
        item.setStations(new ArrayList<>());
        menuService.saveMenuItem(item);

        Order order = new Order();
        order.setTableOrName("1");
        order.setStatus("Open");
        order.setEatInOrTakeAway("eat_in");
        order.setTotal(BigDecimal.valueOf(10.00));
        order.setOpenedAt(LocalDateTime.now());
        orderService.saveOrder(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(item);
        orderItem.setQuantity(1);
        orderItem.setStatus("incomplete");
        orderItemService.saveOrderItem(orderItem);
        order.setTotal(orderItemService.getTotalByOrder(order));

        OrderStation orderStation = new OrderStation();
        orderStation.setStation(grill);
        orderStation.setOrder(order);
        orderStation.setCompleted(false);
        orderStation.setRecalled(false);
        orderStationService.save(orderStation);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StationView.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        MainStationController controller = loader.getController();
        controller.setStation(grill);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void viewLoadsWithoutErrors(FxRobot robot) throws Exception {
        FlowPane pane = robot.lookup("#mainFlowPane").queryAs(FlowPane.class);
        assertFalse(pane.getChildren().isEmpty());
    }

    @Test
    void bumpOrderOff(FxRobot robot) throws Exception {
        robot.clickOn(".order-card-header");

        Order order = orderService.findAll().get(0);
        Station grill = stationService.getAllStations().get(0);
        assertTrue(orderStationService.findByOrderAndStation(order, grill)
                .orElseThrow().isCompleted());

        FlowPane pane = robot.lookup("#mainFlowPane").queryAs(FlowPane.class);
        assertTrue(pane.getChildren().isEmpty());
    }
}

