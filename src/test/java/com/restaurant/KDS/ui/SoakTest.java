package com.restaurant.KDS.ui;

import com.restaurant.KDS.controller.station.ExpoStationController;
import com.restaurant.KDS.controller.station.MainStationController;
import com.restaurant.KDS.entity.*;
import com.restaurant.KDS.repository.*;
import com.restaurant.KDS.service.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Soak test for NFR responsiveness. Runs for 4 hours.
 *
 * Loops the full order lifecycle "create order → main refresh → main bump →
 * expo refresh → expo complete" for a configured duration and records the
 * response time of each action to a CSV. Fails if any action exceeds the SLA.
 *
 * Disabled by default. To run:
 *   mvn -Dtest=SoakTest -Dsoak.minutes=240 test
 */
@Disabled("Long-running soak test — enable manually for NFR evidence runs")
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(ApplicationExtension.class)
public class SoakTest {

    private static final long SLA_MS = 1000;
    private static final Path CSV_PATH = Path.of("target/soak-results.csv");

    @Autowired private ConfigurableApplicationContext springContext;
    @Autowired private MenuService menuService;
    @Autowired private StationService stationService;
    @Autowired private OrderService orderService;
    @Autowired private OrderItemService orderItemService;
    @Autowired private OrderStationService orderStationService;

    @Autowired private OrderStationRepository orderStationRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private StationRepository stationRepository;

    private Station station;
    private MenuItem menuItem;
    private MainStationController mainController;
    private ExpoStationController expoController;
    private Parent expoRoot;

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
        station = new Station();
        station.setName("Grill");
        stationService.saveStation(station);

        menuItem = new MenuItem();
        menuItem.setName("Burger");
        menuItem.setPrice(BigDecimal.valueOf(10.00));
        menuItem.setCategory("Main");
        menuItem.setAvailable(true);
        menuItem.setPrepTimeMinutes(3);
        menuItem.setIngredients(new HashSet<>(List.of("bread")));
        menuItem.setStations(new ArrayList<>(List.of(station)));
        menuService.saveMenuItem(menuItem);

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/StationView.fxml"));
        mainLoader.setControllerFactory(springContext::getBean);
        Parent mainRoot = mainLoader.load();
        mainController = mainLoader.getController();
        mainController.setStation(station);

        FXMLLoader expoLoader = new FXMLLoader(getClass().getResource("/fxml/ExpoStationView.fxml"));
        expoLoader.setControllerFactory(springContext::getBean);
        expoRoot = expoLoader.load();
        expoController = expoLoader.getController();

        stage.setScene(new Scene(mainRoot));
        stage.show();
    }

    @Test
    void soak(FxRobot robot) throws Exception {
        long durationMinutes = Long.parseLong(System.getProperty("soak.minutes", "240"));
        Instant deadline = Instant.now().plus(Duration.ofMinutes(durationMinutes));

        Files.createDirectories(CSV_PATH.getParent());
        long maxObserved = 0;
        long totalIterations = 0;
        boolean slaBreached = false;

        try (PrintWriter csv = new PrintWriter(new FileWriter(CSV_PATH.toFile()))) {
            csv.println("iteration,timestamp_ms,action,duration_ms");

            while (Instant.now().isBefore(deadline)) {
                totalIterations++;

                // Creates the order
                long createMs = measure("createOrder", csv, totalIterations, this::createOrder);

                // Refreshes the main station view
                long mainRefreshMs = measureOnFx("mainRefresh", csv, totalIterations, () -> mainController.populateOpenOrders());

                // Bump the order at the main station
                long mainBumpMs = measureOnFx("mainBump", csv, totalIterations, this::bumpFirstMainCard);

                // Refreshes the expo station view
                long expoRefreshMs = measureOnFx("expoRefresh", csv, totalIterations, () -> expoController.populateOpenOrders());

                // Bump the order from expo
                long expoCompleteMs = measureOnFx("expoComplete", csv, totalIterations, this::bumpFirstExpoCard);

                long worst = Math.max(Math.max(createMs, mainRefreshMs),
                        Math.max(Math.max(mainBumpMs, expoRefreshMs), expoCompleteMs));
                if (worst > maxObserved) maxObserved = worst;
                if (worst > SLA_MS) slaBreached = true;

                //Small pause
                Thread.sleep(500);
            }
        }

        System.out.printf("Soak complete: %d iterations, max %dms, SLA breached=%s%n",
                totalIterations, maxObserved, slaBreached);
        assertTrue(!slaBreached, "At least one action exceeded " + SLA_MS + "ms SLA");
    }


    private void createOrder() {
        Order order = new Order();
        order.setTableOrName("T" + System.nanoTime());
        order.setStatus("Open");
        order.setEatInOrTakeAway("eat_in");
        order.setTotal(BigDecimal.valueOf(10.00));
        order.setOpenedAt(LocalDateTime.now());
        orderService.saveOrder(order);

        OrderItem oi = new OrderItem();
        oi.setOrder(order);
        oi.setMenuItem(menuItem);
        oi.setQuantity(1);
        oi.setStatus("incomplete");
        orderItemService.saveOrderItem(oi);

        OrderStation os = new OrderStation();
        os.setStation(station);
        os.setOrder(order);
        os.setCompleted(false);
        os.setRecalled(false);
        orderStationService.save(os);
    }

    private void bumpFirstMainCard() {
        FlowPane pane = mainController.getMainFlowPane();
        if (pane == null || pane.getChildren().isEmpty()) return;
        VBox card = (VBox) pane.getChildren().get(0);
        Order order = (Order) card.getUserData();
        mainController.onCardClick(order, card);
    }

    private void bumpFirstExpoCard() {
        FlowPane pane = expoController.getMainFlowPane();
        if (pane == null || pane.getChildren().isEmpty()) return;
        VBox card = (VBox) pane.getChildren().get(0);
        Order order = (Order) card.getUserData();
        expoController.onCardClick(order, card);
    }

    private long measure(String label, PrintWriter csv, long iter, Runnable action) {
        long start = System.nanoTime();
        action.run();
        long ms = (System.nanoTime() - start) / 1_000_000;
        csv.printf("%d,%d,%s,%d%n", iter, System.currentTimeMillis(), label, ms);
        csv.flush();
        return ms;
    }

    private long measureOnFx(String label, PrintWriter csv, long iter, Runnable action) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        long[] ms = new long[1];
        Platform.runLater(() -> {
            long start = System.nanoTime();
            try {
                action.run();
            } finally {
                ms[0] = (System.nanoTime() - start) / 1_000_000;
                done.countDown();
            }
        });
        done.await();
        csv.printf("%d,%d,%s,%d%n", iter, System.currentTimeMillis(), label, ms[0]);
        csv.flush();
        return ms[0];
    }
}
