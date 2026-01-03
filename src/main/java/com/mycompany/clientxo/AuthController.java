package com.mycompany.clientxo;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class AuthController {

    @FXML
    private Label headerLabel;

    @FXML
    private Label subHeaderLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button actionButton;

    @FXML
    private Button toggleButton;

    private boolean isLogin = true;

    @FXML
    private void initialize() {
        updateUI();
    }

    @FXML
    private void onBack(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void onAction(ActionEvent event) throws IOException {
        String name = usernameField.getText();
        String password = passwordField.getText();

        if (name == null || name.trim().isEmpty()) {
            showAlert("Error", "Please enter a username.");
            return;
        }

        // Mock Authentication
        System.out.println((isLogin ? "Login" : "Register") + " with: " + name);

        // Save to GameState
        GameState.getInstance().setUsername(name);

        // Navigate to Lobby
        App.setRoot("LobbyScreen");
    }

    @FXML
    private void onToggle(ActionEvent event) {
        isLogin = !isLogin;
        updateUI();
    }

    private void updateUI() {
        if (isLogin) {
            headerLabel.setText("Network Play");
            subHeaderLabel.setText("Sign in to play online");
            actionButton.setText("Login");
            toggleButton.setText("Don't have an account? Register");
        } else {
            headerLabel.setText("Create Account");
            subHeaderLabel.setText("Create your account");
            actionButton.setText("Register");
            toggleButton.setText("Already have an account? Login");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
