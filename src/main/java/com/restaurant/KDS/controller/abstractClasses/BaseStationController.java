package com.restaurant.KDS.controller.abstractClasses;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.AiService;
import com.restaurant.KDS.service.OrderService;
import com.restaurant.KDS.service.OrderStationService;
import com.restaurant.KDS.util.PerfTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
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

import com.restaurant.KDS.controller.settings.SettingsController;

import java.util.prefs.Preferences;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BaseStationController {

    protected final OrderService orderService;
    protected final OrderStationService orderStationService;
    protected final ConfigurableApplicationContext springContext;
    protected final AiService aiService;


    @FXML
    protected FlowPane mainFlowPane;

    @FXML
    protected Label orderAmountLabel;

    @FXML
    protected ProgressBar analyticsBar;

    @FXML
    protected Label estimatedWaitLabel;

    protected Timeline refreshTimeLine, aiRefreshTimeLine;

    protected int onTime, completeOrders;

    protected int lateOrderTime;

    protected int fontSize = 18;

    protected final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);

    protected static List<String> completedTimes = new ArrayList<>();

    private static final Image EAT_IN_ICON = new Image(BaseStationController.class.getResourceAsStream("/images/chair.png"));
    private static final Image TAKEAWAY_ICON = new Image(BaseStationController.class.getResourceAsStream("/images/food-package.png"));


    protected BaseStationController(OrderService orderService, OrderStationService orderStationService, ConfigurableApplicationContext springContext, AiService aiService) {
        this.orderService = orderService;
        this.orderStationService = orderStationService;
        this.springContext = springContext;
        this.aiService = aiService;
    }

    /**
     * Gets the orders that correspond the {@link Station}
     *
     * @return a list of {@link Order}
     */
    public abstract List<Order> getOrders();

    /**
     * Defined the behaviour when the header of the order card is clicked and updates
     * analytics.
     *
     * @param order     the order that the card represents
     * @param container the root node of the order card
     */
    public abstract void onCardClick(Order order, VBox container);

    /**
     * Gets the order items for the {@link Order} that correspond to this {@link Station}.
     *
     * @param order the order to get the order items for
     * @return a list of {@link OrderItem}s for this station
     */
    public abstract List<OrderItem> getOrderItems(Order order);

    /**
     * Gets the {@link Comparator} that defines how order cards are ordered
     * on the {@link Station} {@link FlowPane}
     *
     * @return a {@link Comparator} that orders {@link Node} order cards by priority
     */
    public abstract Comparator<Node> getOrderCardComparator();

    public abstract Long getStationId();

    public FlowPane getMainFlowPane() { return mainFlowPane; }

    /**
     * Builds the order card and adds it to the main {@link FlowPane}
     *
     * @param order the {@link Order} that the card represents
     */
    public void createOrderCard(Order order) {
        VBox containerVbox = new VBox();
        containerVbox.getStyleClass().add("order-card-container");
        containerVbox.setUserData(order);

        //Creates the header for the order card
        HBox headerHbox = new HBox();
        if (orderService.isOnTime(order, lateOrderTime)) {
            headerHbox.setStyle("-fx-background-color: #8de969;"); // green
        } else {
            headerHbox.setStyle("-fx-background-color: #ff6b6b;"); // red
        }
        headerHbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        headerHbox.getStyleClass().add("order-card-header");
        headerHbox.setOnMouseClicked((event) -> {
            onCardClick(order, containerVbox);
        });

        //Sets the order icon depending on eat out or takeaway
        ImageView inOrOutIcon = new ImageView();
        if (order.getEatInOrTakeAway().equals("Eat In")) {
            inOrOutIcon.setImage(EAT_IN_ICON);
        } else {
            inOrOutIcon.setImage(TAKEAWAY_ICON);
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

    /**
     * Runs a loop so every open {@link Order} has an order card
     */
    public void populateOpenOrders() {
        lateOrderTime = prefs.getInt("lateOrderTime", 7);
        Long stationId = getStationId();
        fontSize = stationId == null ? 18 : prefs.getInt("fontSize_" + stationId, 18);
        List<Order> orders = getOrders();
        PerfTimer.time("BaseStation.populateOpenOrders cards=" + orders.size(), () -> {
            mainFlowPane.getChildren().clear();
            for (Order order : orders) {
                createOrderCard(order);
            }
            applyFontSize();
        });
    }

    /**
     * Finds the set {@code fontSize_} for the {@code Station} and applies it to the
     * order card
     */
    private void applyFontSize() {
        Long stationId = getStationId();
        if (stationId == null) return;

        int size = prefs.getInt("fontSize_" + stationId, 18);

        Scene scene = mainFlowPane.getScene();
        if (scene == null) return;

        String css = String.format("""
                    .order-card-container .label {
                        -fx-font-size: %dpx;
                    }
                """, size);

        // Remove old dynamic stylesheet if needed
        scene.getStylesheets().removeIf(s -> s.startsWith("data:text/css"));

        // add new one
        scene.getStylesheets().add("data:text/css," + css.replace("\n", "").replace(" ", "%20"));
    }

    /**
     * Finds the set {@code darkMode_} for the {@code Station} and applies it to the
     * station stage (including the settings and recall screen)
     */
    private void applyColourMode() {
        Long stationId = getStationId();
        boolean isDark = prefs.getBoolean("darkMode_" + stationId, false);
        Platform.runLater(() -> {
            if (isDark) {
                mainFlowPane.getScene().getRoot().getStylesheets().clear();
                mainFlowPane.getScene().getRoot().getStylesheets().add(SettingsController.class.getResource("/css/dark-styles.css").toExternalForm());
            }
        });
    }

    /**
     * Sorts the order cards using the comparator
     */
    public void sortOrders() {
        List<Node> sorted = new ArrayList<>(mainFlowPane.getChildren());
        sorted.sort(getOrderCardComparator());
        mainFlowPane.getChildren().setAll(sorted);
    }


    /**
     * Calculates the ratio based on orders not late/total orders complete and
     * sets the ratio on the analytics {@link ProgressBar}
     */
    public void getAnalytics() {
        double ratio = completeOrders == 0 ? 0.0 : (double) onTime / completeOrders;
        analyticsBar.setProgress(ratio);
    }

    /**
     * Creates a timeline that periodically estimates order wait time using {@link AiService},
     * updating {@code estimatedWaitLabel} every 60 seconds.
     *
     * @return a repeating {@code Timeline} that refreshes the estimated wait time
     */
    private Timeline getEstimatedWaitTime() {
        Timeline aiRefresh = new Timeline(new KeyFrame(Duration.seconds(60), event -> {
            String prompt = "You are given historical order completion data."
                    + "Each entry contains completion time in minutes and timestamp when the order was completed. "
                    + "Estimate a realistic completion time for a new order right now. "
                    + "Do not simply calculate the average. "
                    + "Instead prioritise recent data over older data, detect time of day patterns, "
                    + "reduce the influence of outlier, if recent orders are trending faster "
                    + "or slower, reflect that in the estimate. return a single estimated completion time in minutes"
                    + "DO NOT respond with reasoning, strictly a single whole number. "
                    + "Times: " + completedTimes + "Current time: " + LocalDateTime.now();

            new Thread(() -> {
                try {
                    String result = aiService.askAi(prompt);
                    Platform.runLater(() -> {
                        int minutes = (int) Math.round(Double.parseDouble(result.trim()));
                        estimatedWaitLabel.setText(minutes + " min");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }));
        aiRefresh.setCycleCount(Timeline.INDEFINITE);
        return aiRefresh;
    }

    /**
     * Initializes the main station screen and creates a timeline to refresh it every
     * 2 seconds
     */
    public void refresh() {
        onTime = 0;
        completeOrders = 0;
        if (completedTimes.size() > 200) {
            completedTimes.removeFirst();
        }
        populateOpenOrders();
        sortOrders();
        applyColourMode();
        orderAmountLabel.setText(String.valueOf(mainFlowPane.getChildren().size()));

        //Automatically refreshes the screen
        if (refreshTimeLine != null) {
            return;
        } else {
            refreshTimeLine = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                populateOpenOrders();
                sortOrders();
                orderAmountLabel.setText(String.valueOf(mainFlowPane.getChildren().size()));
            }));
            refreshTimeLine.setCycleCount(Timeline.INDEFINITE);
            refreshTimeLine.play();
        }

        if (aiRefreshTimeLine != null) {
            return;
        }
        aiRefreshTimeLine = getEstimatedWaitTime();
        aiRefreshTimeLine.play();
    }

}
