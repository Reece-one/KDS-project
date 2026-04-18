package com.restaurant.KDS.ui;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.MenuService;
import com.restaurant.KDS.service.OrderItemService;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.StationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;



@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(ApplicationExtension.class)
public class OrderEntryViewTest {

    @Autowired
    private ConfigurableApplicationContext springContext;
    @Autowired
    private StationService stationService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;


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


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrderEntry.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void createANewOrder(FxRobot robot) {
        robot.clickOn("Main");
        robot.clickOn("Burger");
        robot.clickOn("Add to Order");
        robot.clickOn("#tableNameField").write("1");
        robot.clickOn("#eatInCombo");
        robot.clickOn("Eat In");
        robot.clickOn("Submit");

        assertTrue(orderService.findAll().stream()
                .anyMatch(order -> order.getTableOrName().equals("1")));

        Order order = orderService.findAll().get(0);

        assertTrue(order.getOrderItems().stream()
                .anyMatch(orderItem -> orderItem.getOrder().getId()
                        .equals(order.getId())));
        assertTrue(order.getOrderItems().stream()
                .anyMatch(orderItem -> orderItem.getMenuItem().getName()
                    .equals("Burger")));
    }
}
