package com.restaurant.KDS.ui;

import com.restaurant.KDS.service.StationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.TextInputControlMatchers;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(ApplicationExtension.class)
public class AddStationViewTest {

    @Autowired
    private ConfigurableApplicationContext springContext;
    @Autowired
    private StationService stationService;

    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddStation.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void viewLoadsWithoutErrors(FxRobot robot) {
        FxAssert.verifyThat("#nameTextField", TextInputControlMatchers.hasText(""));
    }

    @Test
    void typingNamePopulatesField(FxRobot robot) {
        robot.clickOn("#nameTextField").write("Grill");
        FxAssert.verifyThat("#nameTextField", TextInputControlMatchers.hasText("Grill"));
    }

    @Test
    void submitButtonCreatesNewStation(FxRobot robot) {
        robot.clickOn("#nameTextField").write("Grill");
        robot.clickOn("Submit");

        assertTrue(stationService.getAllStations().stream()
                .anyMatch(station -> station.getName().equals("Grill")));
    }

    @Test
    void submitWithNoName(FxRobot robot) {
        int countBefore = stationService.getAllStations().size();
        robot.clickOn("Submit");

        assertEquals(stationService.getAllStations().size(), countBefore);
    }

}
