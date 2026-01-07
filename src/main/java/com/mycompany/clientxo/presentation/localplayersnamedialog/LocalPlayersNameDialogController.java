package com.mycompany.clientxo.presentation.localplayersnamedialog;

import com.mycompany.clientxo.core.navigation.Routes;

import com.mycompany.clientxo.App;
import com.mycompany.clientxo.presentation.game.GameController;
import com.mycompany.clientxo.presentation.localmultiplayergame.LocalMultiplayerGameController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LocalPlayersNameDialogController {

    @FXML
    private TextField playerXInput;

    @FXML
    private TextField playerOInput;

    @FXML
    private void onStartGame(ActionEvent event) {
        try {
            // Load the FXML for the localMultiplayerGameScreen
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/com/mycompany/clientxo/LocalMultiplayerGameScreen.fxml"));
            Parent root = loader.load();

            // Access the controller of the localMultiplayerGameScreen
            LocalMultiplayerGameController localGame = loader.getController();

            // Pass names to the localMultiplayerGameScreen
            localGame.setPlayerNames(playerXInput.getText(), playerOInput.getText());

            // Switch the screen using App.setRoot logic to maintain scene settings
            App.setRoot(root);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onBack() {
        try {
            App.setRoot(Routes.HOME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
