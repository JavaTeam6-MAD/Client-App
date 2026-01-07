package com.mycompany.clientxo.presentation.game;

import com.mycompany.clientxo.core.navigation.Routes;

import com.mycompany.clientxo.App;
import com.mycompany.clientxo.utils.SoundManager;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javafx.util.Duration;

public class GameController {

    @FXML
    private Label playerXName;
    @FXML
    private Label playerOName;
    @FXML
    private Label scoreX;
    @FXML
    private Label scoreO;
    @FXML
    private Label statusText;
    @FXML
    private GridPane gameGrid;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button homeButton; // New Button for navigation

    @FXML
    private StackPane recordingIndicator;

    // Sound Manager instance
    private SoundManager soundManager;

    public void setPlayerNames(String nameX, String nameO) {
        if (nameX != null && !nameX.isEmpty()) {
            playerXName.setText(nameX);
        }
        if (nameO != null && !nameO.isEmpty()) {
            playerOName.setText(nameO);
        }
    }

    @FXML
    public void initialize() {
        // Initialize sound manager
        soundManager = SoundManager.getInstance();

        // Example: Enable recording indicator when game starts
        // Uncomment the line below to show the recording indicator:
        showRecordingIndicator();
    }

    @FXML
    private void handleGridClick(ActionEvent event) {
        // Play click sound - will later distinguish between X and O
        if (soundManager != null) {
            // Alternate between X and O sounds based on current player
            // For now, playing a generic click sound
            soundManager.playSound(SoundManager.PLACE_X);
        }
    }

    @FXML
    private void resetGame(ActionEvent event) {
        // Play button click sound
        if (soundManager != null) {
            soundManager.playSound(SoundManager.BUTTON_CLICK);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Play button click sound
        if (soundManager != null) {
            soundManager.playSound(SoundManager.BUTTON_CLICK);
        }

        // Show confirmation dialog with custom styling
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave Game?");
        alert.setHeaderText("Are you sure you want to go back?");
        alert.setContentText("The current game will end and you will lose this match.");

        // Apply custom cyberpunk theme styling
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/mycompany/clientxo/styles.css").toExternalForm());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    App.setRoot(Routes.HOME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // If Cancel, do nothing - stay on game screen
        });
    }

    @FXML
    private void onHome(ActionEvent event) {
        // Play button click sound
        if (soundManager != null) {
            soundManager.playSound(SoundManager.BUTTON_CLICK);
        }

        try {
            App.setRoot(Routes.HOME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the recording indicator with pulse animation
     */
    public void showRecordingIndicator() {
        if (recordingIndicator != null) {
            recordingIndicator.setVisible(true);
            recordingIndicator.setManaged(true);
            startRecordingAnimation();
        }
    }

    /**
     * Hide the recording indicator
     */
    public void hideRecordingIndicator() {
        if (recordingIndicator != null) {
            recordingIndicator.setVisible(false);
            recordingIndicator.setManaged(false);
        }
    }

    /**
     * Animate the recording indicator with a pulsing effect
     */
    private void startRecordingAnimation() {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1000), recordingIndicator);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }
}
