package com.mycompany.presentation.homescreen;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class HomeScreenController {

    HomeManager homeManager;

    @FXML
    private Button btnPlayVsComputer;

    @FXML
    private Button btnLocalMultiplayer;

    @FXML
    private Button btnNetworkMultiplayer;

    @FXML
    private void onPlayVsComputer() throws IOException {
        App.setRoot(Routes.DIFFICULTY);
    }

    @FXML
    private void onPlayLocalMultiplayer() throws IOException {
        App.setRoot(Routes.LOCAL_PLAYERS_NAME_DIALOG);
    }

    @FXML
    private void onPlayNetworkMultiplayer() throws IOException {
         homeManager = new HomeManager();
        var localPlayer = homeManager.getCurrentPlayer();
        if (localPlayer != null) {
            var player = homeManager.login(localPlayer.getUserName(), localPlayer.getPassword());
            if (player.getId() != 0) {
                App.setRoot(Routes.LOBBY);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Server Connection Error");
                alert.setHeaderText(null);
                alert.setContentText("Could not connect to server. Please try again later.");
                alert.showAndWait();
            }
        } else {
            App.setRoot(Routes.AUTH);
        }
    }
}
