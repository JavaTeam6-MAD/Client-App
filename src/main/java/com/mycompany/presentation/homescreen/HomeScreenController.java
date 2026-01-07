package com.mycompany.presentation.homescreen;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeScreenController {

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
        App.setRoot(Routes.AUTH);
    }
}
