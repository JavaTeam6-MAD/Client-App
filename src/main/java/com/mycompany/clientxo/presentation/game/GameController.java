package com.mycompany.clientxo.presentation.game;

import com.mycompany.clientxo.App;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    public void setPlayerNames(String nameX, String nameO) {
        if (nameX != null && !nameX.isEmpty()) {
            playerXName.setText(nameX);
        }
        if (nameO != null && !nameO.isEmpty()) {
            playerOName.setText(nameO);
        }
    }

    // SVG Paths
    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    @FXML
    public void initialize() {
        // Example: Enable recording indicator when game starts
        // Uncomment the line below to show the recording indicator:
        showRecordingIndicator();
    }

    @FXML
    private void handleGridClick(ActionEvent event) {

    }

    @FXML
    private void resetGame(ActionEvent event) {

    }

    @FXML
    private void handleBack(ActionEvent event) {
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
                    App.setRoot("primary");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // If Cancel, do nothing - stay on game screen
        });
    }

    @FXML
    private void onHome(ActionEvent event) {
        try {
            App.setRoot("primary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateButton(Button btn) {
        if (btn.getGraphic() != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(300), btn.getGraphic());
            st.setFromX(0.1);
            st.setFromY(0.1);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
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
