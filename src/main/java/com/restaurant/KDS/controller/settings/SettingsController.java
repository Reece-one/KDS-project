package com.restaurant.KDS.controller.settings;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.util.ViewHelper;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import java.util.prefs.Preferences;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SettingsController {

    @FXML
    private Slider fontSlider;

    @FXML
    private Label sliderLabel;

    private Long stationId;

    @FXML
    private Scene scene;

    public void setStationScene(Scene scene) {
        this.scene = scene;
    }

    public void setStation(Station station) {
        setStationId(station.getId());
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
        Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
        int saved = prefs.getInt("fontSize_" + stationId, 24);
        fontSlider.setValue(saved);
        sliderLabel.setText(String.valueOf(saved));
    }

    public void initialize() {
        fontSlider.setMin(12);
        fontSlider.setMax(32);
        fontSlider.setBlockIncrement(1);

        //Gets the value from the slider and makes it an int
        fontSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int value = newValue.intValue();
            sliderLabel.setText(String.valueOf(value));

            //Saves the preferred font size for this station
            if (stationId != null) {
                Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
                prefs.putInt("fontSize_" + stationId, value);
            }

            //Changes the font size to the preferred
            if (scene != null) {
                scene.getRoot().lookupAll(".order-card-container").forEach(container -> {
                    container.lookupAll(".label").forEach(label -> {
                        label.setStyle("-fx-font-size: " + value + "px;");
                    });
                });

            }
        });
    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) fontSlider.getScene().getWindow();
        stage.setScene(scene);
    }


}
