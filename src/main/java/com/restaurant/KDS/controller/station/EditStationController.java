package com.restaurant.KDS.controller.station;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.StationService;
import com.restaurant.KDS.util.ViewHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.stereotype.Component;

@Component
public class EditStationController {

    private final StationService stationService;
    private Station station;

    @FXML
    private TextField nameTextField;

    public EditStationController(StationService stationService) {
        this.stationService = stationService;
    }

    public void setStation(Station station) {
        this.station = station;
        nameTextField.setText(station.getName());
    }

    @FXML
    private void onSave() {
        if (nameTextField.getText().trim().isEmpty()) {
            ViewHelper.showAlert("Please enter a name!");
        }
        station.setName(nameTextField.getText());
        stationService.saveStation(station);
        closeModal();
    }

    @FXML
    private void onDelete() {
        stationService.deleteStation(station);

        Stage target = Window.getWindows().stream()
                .filter(w -> w instanceof Stage s && station.getName().equals(s.getTitle()))
                .map(w -> (Stage) w)
                .findFirst()
                .orElse(null);

        target.close();
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.close();
    }
}
