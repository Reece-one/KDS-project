package com.restaurant.KDS;

import com.restaurant.KDS.controller.station.MainStationController;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.StationService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

public class JavaFxApplication extends javafx.application.Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(KdsfypApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StationService stationService = springContext.getBean(StationService.class);
        List<Station> stations = stationService.getAllStations();

        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/fxml/OrderEntry.fxml"));
        fxmlloader.setControllerFactory(springContext::getBean);
        Parent root = fxmlloader.load();

        primaryStage.setTitle("Order Entry");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        for (Station station : stations) {
            FXMLLoader fxmlloader1 = new FXMLLoader(getClass().getResource("/fxml/StationView.fxml"));
            fxmlloader1.setControllerFactory(springContext::getBean);
            Parent root1 = fxmlloader1.load();

            MainStationController controller = fxmlloader1.getController();
            controller.setStation(station);

            Stage stationStage = new Stage();
            stationStage.setTitle(station.getName());
            stationStage.setScene(new Scene(root1));
            stationStage.show();
        }

        FXMLLoader fxmlloader4 = new FXMLLoader(getClass().getResource("/fxml/ExpoStationView.fxml"));
        fxmlloader4.setControllerFactory(springContext::getBean);
        Parent root4 = fxmlloader4.load();

        Stage expoStage = new Stage();
        expoStage.setTitle("Expo");
        expoStage.setScene(new Scene(root4));
        expoStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}
