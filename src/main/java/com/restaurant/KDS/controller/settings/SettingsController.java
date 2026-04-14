package com.restaurant.KDS.controller.settings;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.util.ViewHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
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

    @FXML
    private CheckBox darkModeCheckBox;

    @FXML
    private CheckBox bumpCheckBox;

    private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);


    public void setStationScene(Scene scene) {
        this.scene = scene;
    }

    public void setStation(Station station) {
        setStationId(station.getId());
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
        //Gets saved font preferences
        int saved = prefs.getInt("fontSize_" + stationId, 24);  //Gets the font size preferences based on station id as key
        fontSlider.setValue(saved);
        sliderLabel.setText(String.valueOf(saved));

        //Gets the dark mode preferences and sets the setting scene to it
        boolean isDark = prefs.getBoolean("darkMode_" + stationId, false);
        darkModeCheckBox.setSelected(isDark);
        if (isDark) {
            Platform.runLater(() -> {
                Scene settingsScene = fontSlider.getScene();
                if (settingsScene != null) {
                    settingsScene.getRoot().getStylesheets().clear();
                    settingsScene.getRoot().getStylesheets().add(SettingsController.class.getResource("/css/dark-styles.css").toExternalForm());
                }
            });
        }

        boolean strictBump = prefs.getBoolean("bump", false);
        bumpCheckBox.setSelected(strictBump);
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

        //Saves the preference for dark mode
        darkModeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            String css = newVal ? "/css/dark-styles.css" : "/css/styles.css";
            prefs.putBoolean("darkMode_" + stationId, newVal);

            if (scene != null) {
                scene.getRoot().getStylesheets().clear();
                scene.getRoot().getStylesheets().add(SettingsController.class.getResource(css).toExternalForm());
            }
            Scene settingsScene = fontSlider.getScene();
            if (settingsScene != null) {
                settingsScene.getRoot().getStylesheets().clear();
                settingsScene.getRoot().getStylesheets().add(SettingsController.class.getResource(css).toExternalForm());
            }
        });

        //Saves the preference for Strict Expo Bump
        bumpCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            prefs.putBoolean("bump", newVal);
        });
    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) fontSlider.getScene().getWindow();
        stage.setScene(scene);
    }

}
