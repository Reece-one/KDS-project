package com.restaurant.KDS.ui;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.service.MenuService;
import com.restaurant.KDS.service.StationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
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
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(ApplicationExtension.class)
public class AddMenuItemViewTest {

    @Autowired
    private ConfigurableApplicationContext springContext;
    @Autowired
    private StationService stationService;
    @Autowired
    private MenuService menuService;

    @Start
    private void start(Stage stage) throws Exception {
        Station grill = new Station();
        grill.setName("Grill");
        stationService.saveStation(grill);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddMenuItem.fxml"));
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
    void populateFields(FxRobot robot) {
        robot.clickOn("#nameTextField").write("Burger");
        robot.clickOn("#ingredientTextField").write("Bread");
        robot.clickOn("Add");
        robot.clickOn("#availableCheckBox");

        FxAssert.verifyThat("#nameTextField", TextInputControlMatchers.hasText("Burger"));
        FxAssert.verifyThat("Bread", NodeMatchers.isNotNull());
        CheckBox box = robot.lookup("#availableCheckBox").queryAs(CheckBox.class);
        assertTrue(box.isSelected());
    }

    @Test
    void createMenuItem(FxRobot robot) {
        robot.clickOn("#nameTextField").write("Burger");
        robot.clickOn("#priceTextField").write("10");
        robot.clickOn("#categoryTextField").write("Main");
        robot.clickOn("#ingredientTextField").write("Bread");
        robot.clickOn("Add");
        robot.clickOn("#1checkBox");
        robot.clickOn("#availableCheckBox");
        robot.clickOn("#timeTextField").write("5");
        robot.clickOn("Create");

        assertTrue(menuService.getAllMenuItems().stream()
                .anyMatch(menu -> menu.getName().equals("Burger")));
    }
}
