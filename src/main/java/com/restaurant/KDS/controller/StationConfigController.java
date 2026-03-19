package com.restaurant.KDS.controller;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.StationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import javafx.scene.control.Button;
import java.util.List;

@Component
public class StationConfigController {

    private final StationService stationService;
    private final ConfigurableApplicationContext springContext;

    @FXML
    private FlowPane stationFlowPane;

    public StationConfigController(StationService stationService, ConfigurableApplicationContext springContext) {
        this.stationService = stationService;
        this.springContext = springContext;
    }

    private void resetStation () {
        stationFlowPane.getChildren().clear();
        List<Station> stations = stationService.getAllStations();

        for (Station station : stations) {
            Button button = new Button(station.getName());
            stationFlowPane.getChildren().add(button);
        }

    }

    @FXML
    public void initialize() {
        resetStation();
    }

    @FXML
    private void onAddStation() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddStation.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.setTitle("Add Station");
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setScene(new Scene(root));
        modal.showAndWait();

        resetStation();
    }

}
