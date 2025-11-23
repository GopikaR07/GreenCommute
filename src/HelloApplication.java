package com.example.demo4;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        System.out.println("Launching JavaFX Application");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Button btn = new Button("OK");
        Label l1 = new Label("II");
        Label l2 = new Label("CSE");
        Label l3 = new Label("A");

        // Position labels
        l1.setTranslateY(-60);
        l2.setTranslateY(-30);
        l3.setTranslateY(0);

        btn.setTranslateX(-214);
        btn.setTranslateY(-85);

        Button btn1 = new Button("Click me");
        btn1.setOnAction(e -> System.out.println("Click me is pressed"));

        Button btn2 = new Button("exit");
        btn2.setOnAction(e -> {
            System.out.println("App will now exit");
            System.exit(0);
        });
        btn2.setTranslateX(228);
        btn2.setTranslateY(85);

        StackPane layout1 = new StackPane();
        layout1.getChildren().addAll(btn, btn1, btn2, l1, l2, l3);

        Scene scene1 = new Scene(layout1, 500, 200);
        stage.setTitle("App Title bar text here");
        stage.setScene(scene1);
        stage.show();
    }

    @Override
    public void init() {
        System.out.println("Inside Init Method");
    }

    @Override
    public void stop() {
        System.out.println("Inside Stop Method");
    }
}
