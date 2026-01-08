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
            // selectedCharId = player.getAvatar(); // Use this when avatar is synced
        } else {
            usernameField.setText("");
        }
        selectedCharId = "2"; // Default for now until avatar logic is fully implemented
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

        if (newName == null || newName.trim().isEmpty()) {
            showAlert("Invalid Input", "Username cannot be empty.");
            return;
        }

        Player updatedPlayer = playerRepository.changeUserName(newName);

        if (updatedPlayer != null && updatedPlayer.getId() != 0) {
            onBack();
        } else {
            showAlert("Error", "Failed to update profile. Please try again.");
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
