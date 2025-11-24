package com.restaurant.KDS;

import com.restaurant.KDS.config.SpringFXMLLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class KdsfypApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent rootNode;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(KdsfypApplication.class);
        SpringFXMLLoader fxmlLoader = springContext.getBean(SpringFXMLLoader.class);
        rootNode = fxmlLoader.load("/views/MainView.fxml");
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Restaurant Order Management");
        Scene scene = new Scene(rootNode, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }
}
