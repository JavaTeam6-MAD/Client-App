package com.mycompany.clientxo;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the HomeScreen FXML view.
 * Handles navigation to different game modes:
 * - Play vs Computer
 * - Local Multiplayer
 * - Network Multiplayer
 */
public class HomeScreenController {

    @FXML
    private Button btnPlayVsComputer;

    @FXML
    private Button btnLocalMultiplayer;

    @FXML
    private Button btnNetworkMultiplayer;

    /**
     * Called when the user clicks "Play vs Computer" button.
     * Navigates to the single player game screen.
     */
    @FXML
    private void onPlayVsComputer() throws IOException {
        System.out.println("Play vs Computer clicked!");
        // TODO: Navigate to computer game screen
        // App.setRoot("gameScreen");
    }

    /**
     * Called when the user clicks "Local Multiplayer" button.
     * Navigates to the local multiplayer game screen.
     */
    @FXML
    private void onPlayLocalMultiplayer() throws IOException {
        System.out.println("Local Multiplayer clicked!");
        // TODO: Navigate to local multiplayer screen
        // App.setRoot("localMultiplayerScreen");
    }

    /**
     * Called when the user clicks "Network Multiplayer" button.
     * Navigates to the authentication screen.
     */
    @FXML
    private void onPlayNetworkMultiplayer() throws IOException {
        System.out.println("Network Multiplayer clicked!");
        // TODO: Implement network multiplayer authentication
        System.out.println("Network Multiplayer - Coming Soon!");
    }
}
