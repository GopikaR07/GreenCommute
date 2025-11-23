package com.example.demo4;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Hello extends Application {

    public static void main(String[] args) {
        System.out.println("Launching JavaFX Application");
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button btn = new Button("OK");

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World");
            }
        });

        StackPane layout = new StackPane();
        layout.getChildren().add(btn);

        Scene scene = new Scene(layout, 400, 200);

        stage.setTitle("My JavaFX App");
        stage.setScene(scene);
        stage.show();
    }
}
