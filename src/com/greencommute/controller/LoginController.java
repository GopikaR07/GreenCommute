package com.greencommute.controller;

import com.greencommute.dao.UserDAO;
import com.greencommute.model.User;
import com.greencommute.util.SessionManager;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Hyperlink registerLink;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        statusLabel.setText("");
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }

        Task<User> task = new Task<>() {
            @Override
            protected User call() throws Exception {
                try {
                    return UserDAO.authenticate(email, password);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        task.setOnSucceeded(t -> {
            User user = task.getValue();
            if (user != null) {
                SessionManager.setCurrentUser(user);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/greencommute/fxml/dashboard.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root, 1400, 900);
                    scene.getStylesheets().add(getClass().getResource("/com/greencommute/styles/style.css").toExternalForm());
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Failed to open dashboard.");
                }
            } else {
                statusLabel.setText("Invalid email or password.");
            }
        });

        task.setOnFailed(t -> {
            Throwable ex = task.getException();
            statusLabel.setText("Login error: " + (ex != null ? ex.getMessage() : "unknown"));
        });

        new Thread(task).start();
    }

    @FXML
    private void onRegister(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Quick Register");
        dialog.setHeaderText("Enter: fullname,email,password (comma separated)");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length < 3) {
                statusLabel.setText("Invalid format. Use: fullname,email,password");
                return;
            }

            String fullname = parts[0].trim();
            String email = parts[1].trim();
            String password = parts[2].trim();

            Task<Boolean> regTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {
                        return UserDAO.register(fullname, email, password);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            regTask.setOnSucceeded(ev -> {
                if (regTask.getValue()) {
                    statusLabel.setText("Registration successful. Please login.");
                } else {
                    statusLabel.setText("Registration failed (email exists?).");
                }
            });

            regTask.setOnFailed(ev -> {
                statusLabel.setText("Registration error.");
            });

            new Thread(regTask).start();
        });
    }
}