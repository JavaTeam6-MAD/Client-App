package com.mycompany.clientxo;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the HomeScreen FXML view.
 * Handles navigation to different game modes using GameState.
 */
public class HomeScreenController {

    @FXML
    private Button btnPlayVsComputer;

    @FXML
    private Button btnLocalMultiplayer;

    @FXML
    private Button btnNetworkMultiplayer;

    @FXML
    private void onPlayVsComputer() throws IOException {
        System.out.println("Play vs Computer clicked!");
        GameState.getInstance().setCurrentMode(GameState.GameMode.SINGLE_PLAYER);
        App.setRoot("difficulty");
    }

    @FXML
    private void onPlayLocalMultiplayer() throws IOException {
        System.out.println("Local Multiplayer clicked!");
        GameState.getInstance().setCurrentMode(GameState.GameMode.LOCAL_MULTIPLAYER);
        App.setRoot("GameScreen");
    }

    @FXML
    private void onPlayNetworkMultiplayer() throws IOException {
        System.out.println("Network Multiplayer clicked!");
        GameState.getInstance().setCurrentMode(GameState.GameMode.NETWORK_MULTIPLAYER);
        App.setRoot("auth");
    }
}
