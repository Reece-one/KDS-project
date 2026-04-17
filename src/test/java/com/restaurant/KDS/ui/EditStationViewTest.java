package com.restaurant.KDS.ui;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.StationService;
import jakarta.validation.constraints.AssertTrue;
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
public class EditStationViewTest {

    @Autowired
    private ConfigurableApplicationContext springContext;
    @Autowired
    private StationService stationService;

    @Start
    private void start(Stage stage) throws Exception {
        Station grill = new Station();
        grill.setName("Grill");
        stationService.saveStation(grill);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StationConfig.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void viewLoadsWithoutErrors(FxRobot robot) {
        robot.clickOn("Grill");
        FxAssert.verifyThat("#nameTextField", TextInputControlMatchers.hasText("Grill"));
    }

    @Test
    void changeStationName(FxRobot robot) {
        robot.clickOn("Grill");
        robot.doubleClickOn("#nameTextField").write("Fry");
        robot.clickOn("Save");

        assertTrue(stationService.getAllStations().stream()
                .anyMatch(s -> s.getName().equals("Fry")));
        assertFalse(stationService.getAllStations().stream()
                .anyMatch(s -> s.getName().equals("Grill")));
    }
}
