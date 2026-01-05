package com.mycompany.clientxo.presentation.localplayersnamedialog;

import com.mycompany.clientxo.App;
import com.mycompany.clientxo.presentation.game.GameController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LocalPlayersNameDialogController {

    @FXML
    private TextField playerXInput;

    @FXML
    private TextField playerOInput;

    @FXML
    private void onStartGame() {
        try {
            String nameX = playerXInput.getText();
            String nameO = playerOInput.getText();

            // Default names if empty
            if (nameX == null || nameX.trim().isEmpty())
                nameX = "Player X";
            if (nameO == null || nameO.trim().isEmpty())
                nameO = "Player O";

            // Load GameScreen
            FXMLLoader loader = new FXMLLoader(App.class.getResource("GameScreen.fxml"));
            Parent root = loader.load();

            // Get Controller and set names
            GameController gameController = loader.getController();
            gameController.setPlayerNames(nameX, nameO);

            // Navigate
            App.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBack() {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
