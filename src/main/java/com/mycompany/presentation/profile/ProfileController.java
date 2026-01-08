package com.mycompany.presentation.profile;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;

public class ProfileController {

    private final ProfileManager profileManager = new ProfileManager();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Button charRobot;
    @FXML
    private Button charGhost;
    @FXML
    private Button charDragon;
    @FXML
    private Button charAlien;

    @FXML
    public void initialize() {
        Player player = profileManager.getCurrentPlayer();
        if (player != null) {
            usernameField.setText(player.getUserName());
        } else {
            usernameField.setText("");
        }
        updateCharSelectionUI();
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot(Routes.LOBBY);
    }

    @FXML
    private void onSave() throws IOException {
        String newName = usernameField.getText();
        String newPass = passwordField.getText();

        String error = profileManager.saveProfile(newName, newPass);

        if (error == null) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            onBack();
        } else {
            if (error.equals("Username cannot be empty.")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", error);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", error);
            }
        }
    }

    @FXML
    private void onCharRobot() {
        profileManager.selectChar("robot");
        updateCharSelectionUI();
    }

    @FXML
    private void onCharGhost() {
        profileManager.selectChar("ghost");
        updateCharSelectionUI();
    }

    @FXML
    private void onCharDragon() {
        profileManager.selectChar("dragon");
        updateCharSelectionUI();
    }

    @FXML
    private void onCharAlien() {
        profileManager.selectChar("alien");
        updateCharSelectionUI();
    }

    private void updateCharSelectionUI() {
        String selectedId = profileManager.getSelectedCharId();
        highlightButton(charRobot, "robot".equals(selectedId));
        highlightButton(charGhost, "ghost".equals(selectedId));
        highlightButton(charDragon, "dragon".equals(selectedId));
        highlightButton(charAlien, "alien".equals(selectedId));
    }

    private void highlightButton(Button btn, boolean selected) {
        if (selected) {
            btn.setStyle("-fx-border-color: #00FFFF; -fx-background-color: rgba(0, 255, 255, 0.1);");
        } else {
            btn.setStyle(""); // Revert to stylesheet default
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);

        // Apply CSS
        if (alert.getDialogPane().getScene().getWindow() != null) {
            alert.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
        }

        alert.showAndWait();
    }
}
