package com.mycompany.presentation.profile;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.mycompany.data.repo_impl.PlayerRepositoryImpl;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;

public class ProfileController {

    private PlayerRepository playerRepository = new PlayerRepositoryImpl();

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

    private String selectedCharId;

    @FXML
    public void initialize() {
        Player player = playerRepository.getCurrentPlayer();
        if (player != null) {
            usernameField.setText(player.getUserName());
            selectedCharId = player.getAvatar();
        } else {
            usernameField.setText("");
        }
        // selectedCharId = "2"; // Default for now until avatar logic is fully
        // implemented
        if (selectedCharId == null)
            selectedCharId = "robot"; // Default fallback
        updateCharSelectionUI();
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot(Routes.LOBBY);
    }

    @FXML
    private void onSave() throws IOException {
        String newName = usernameField.getText();
        if (newName != null) {
            newName = newName.trim();
        }
        String newPass = passwordField.getText();

        boolean success = true;
        StringBuilder errorMessage = new StringBuilder();

        Player currentPlayer = playerRepository.getCurrentPlayer();

        // Update Username
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(currentPlayer.getUserName())) {
            Player res = playerRepository.changeUserName(newName);
            if (res == null || res.getId() == 0) {
                success = false;
                errorMessage.append("Failed to update username.\n");
            }
        } else if (newName == null || newName.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Username cannot be empty.");
            return;
        }

        // Update Password
        if (newPass != null && !newPass.trim().isEmpty()) {
            Player res = playerRepository.changePassword(newPass);
            if (res == null || res.getId() == 0) {
                success = false;
                errorMessage.append("Failed to update password.\n");
            }
        }

        // Update Avatar
        String currentAvatar = currentPlayer.getAvatar();
        if (selectedCharId != null && !selectedCharId.equals(currentAvatar)) {
            Player res = playerRepository.changeAvatar(selectedCharId);
            if (res == null || res.getId() == 0) {
                success = false;
                errorMessage.append("Failed to update avatar.\n");
            }
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            onBack();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error",
                    errorMessage.length() == 0 ? "Update failed" : errorMessage.toString());
        }
    }

    @FXML
    private void onCharRobot() {
        selectChar("robot");
    }

    @FXML
    private void onCharGhost() {
        selectChar("ghost");
    }

    @FXML
    private void onCharDragon() {
        selectChar("dragon");
    }

    @FXML
    private void onCharAlien() {
        selectChar("alien");
    }

    private void selectChar(String id) {
        selectedCharId = id;
        updateCharSelectionUI();
    }

    private void updateCharSelectionUI() {
        highlightButton(charRobot, "robot".equals(selectedCharId));
        highlightButton(charGhost, "ghost".equals(selectedCharId));
        highlightButton(charDragon, "dragon".equals(selectedCharId));
        highlightButton(charAlien, "alien".equals(selectedCharId));
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
