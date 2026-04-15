package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.StationService;
import com.restaurant.KDS.util.ViewHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AddStationController {

    private final StationService stationService;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button addStationButton;

    public AddStationController(StationService stationService) {
        this.stationService = stationService;
    }

    @FXML
    private void onSubmit(ActionEvent event) throws IOException {
        //Get the name from the text field
        String name = nameTextField.getText();

        //Create a new station object and assign it the name from the text field
        Station newStation = new Station();
        newStation.setName(name);
        stationService.saveStation(newStation);

        //Close the view
        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onClose() throws IOException {
        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.close();
    }
}
