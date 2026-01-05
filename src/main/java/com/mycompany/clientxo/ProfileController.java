package com.mycompany.clientxo;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController {

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
        usernameField.setText("Ahmed");
        selectedCharId = "2";
        updateCharSelectionUI();
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot("LobbyScreen");
    }

    @FXML
    private void onSave() throws IOException {
        String newName = usernameField.getText();
        String newPass = passwordField.getText();

        if (newName == null || newName.trim().isEmpty()) {
            showAlert("Invalid Input", "Username cannot be empty.");
            return;
        }

        App.setRoot("LobbyScreen");
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
