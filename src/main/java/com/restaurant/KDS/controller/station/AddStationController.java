package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.StationService;
import com.restaurant.KDS.util.ViewHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AddStationController {

    private final StationService stationService;
    private final ConfigurableApplicationContext springContext;

    @FXML
    private TextField nameTextField;

    public AddStationController(StationService stationService, ConfigurableApplicationContext springContext) {
        this.stationService = stationService;
        this.springContext = springContext;
    }

    /**
     * Creates a new {@link Station} entry and opens a new {@link Stage} for that stations
     * view.
     */
    @FXML
    private void onSubmit(ActionEvent event) throws IOException {
        if (nameTextField.getText().trim().isEmpty()) {
            ViewHelper.showAlert("Please enter a name!");
            return;
        }
        String name = nameTextField.getText();
        Station newStation = new Station();
        newStation.setName(name);
        stationService.saveStation(newStation);

        //Opens a new stage for the new station
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/fxml/StationView.fxml"));
        fxmlloader.setControllerFactory(springContext::getBean);
        Parent root = fxmlloader.load();

        MainStationController controller = fxmlloader.getController();
        controller.setStation(newStation);

        Stage stationStage = new Stage();
        stationStage.setTitle(newStation.getName());
        stationStage.setScene(new Scene(root));
        stationStage.show();

        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onClose() throws IOException {
        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.close();
    }
}
