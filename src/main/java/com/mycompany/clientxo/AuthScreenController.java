package com.mycompany.clientxo;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the AuthScreen FXML view.
 * Handles user authentication (login/register) for network multiplayer.
 */
public class AuthScreenController {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSubmit;

    @FXML
    private Button btnToggleMode;

    @FXML
    private Label lblHeader;

    @FXML
    private Label lblSubheader;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private CheckBox chkRecordGame;

    private boolean isLoginMode = true;

    /**
     * Called when the FXML is loaded.
     */
    @FXML
    public void initialize() {
        updateAuthMode();
    }

    /**
     * Updates the UI based on current auth mode (login/register).
     */
    private void updateAuthMode() {
        if (isLoginMode) {
            lblSubheader.setText("Sign in to play online");
            btnSubmit.setText("Login");
            btnToggleMode.setText("Don't have an account? Register");
        } else {
            lblSubheader.setText("Create your account");
            btnSubmit.setText("Register");
            btnToggleMode.setText("Already have an account? Login");
        }
    }

    /**
     * Called when the user clicks the back button.
     * Returns to the home screen.
     */
    @FXML
    private void onBack() throws IOException {
        App.setRoot("HomeScreen");
    }

    /**
     * Called when the user submits the form (login or register).
     */
    @FXML
    private void onSubmit() throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        boolean recordGame = chkRecordGame.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please enter username and password");
            return;
        }

        if (isLoginMode) {
            System.out.println("Login - Username: " + username + ", Record: " + recordGame);
            // TODO: Implement login logic
            // onLogin(username, password, recordGame);
        } else {
            System.out.println("Register - Username: " + username + ", Record: " + recordGame);
            // TODO: Implement register logic
            // onRegister(username, password, recordGame);
        }
    }

    /**
     * Toggles between login and register mode.
     */
    @FXML
    private void onToggleMode() {
        isLoginMode = !isLoginMode;
        updateAuthMode();
        // Clear fields when switching modes
        txtUsername.clear();
        txtPassword.clear();
        chkRecordGame.setSelected(false);
    }
}
