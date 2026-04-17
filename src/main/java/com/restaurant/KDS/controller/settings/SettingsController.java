package com.restaurant.KDS.controller.settings;

import com.restaurant.KDS.entity.Station;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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

    @FXML
    private TextField lateOrderTimeTextField;

    private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);


    public void setStationScene(Scene scene) {
        this.scene = scene;
    }

    public void setStation(Station station) {
        setStationId(station.getId());
    }

    /**
     * Sets the {@link Station} ID and populates the settings fields according to their
     * saves {@link Preferences}.
     *
     * @param stationId the station ID of the station of this instance
     */
    public void setStationId(Long stationId) {
        this.stationId = stationId;
        //Gets saved font preference
        int saved = prefs.getInt("fontSize_" + stationId, 24);  //Gets the font size preferences based on station id as key
        fontSlider.setValue(saved);
        sliderLabel.setText(String.valueOf(saved));

        //Gets the dark mode preference and sets the setting scene to it
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

        //Gets the strict expo bump preference
        boolean strictBump = prefs.getBoolean("bump", false);
        bumpCheckBox.setSelected(strictBump);

        //Gets the late order time preference
        int lateOrderTime = prefs.getInt("lateOrderTime", 7);
        lateOrderTimeTextField.setText(String.valueOf(lateOrderTime));
    }

    /**
     * Creates all the {@link Preferences} from the inputs on all the field in the view.
     * The {@link Station} ID from the instance is used in all the keys.
     */
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

        //Saves the preference for the late order time
        lateOrderTimeTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            prefs.putInt("lateOrderTime", Integer.parseInt(newVal));
        });
    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) fontSlider.getScene().getWindow();
        stage.setScene(scene);
    }

}
