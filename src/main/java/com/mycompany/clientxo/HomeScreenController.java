package com.mycompany.clientxo;

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
        App.setRoot("difficulty");
    }

    @FXML
    private void onPlayLocalMultiplayer() throws IOException {
        App.setRoot("LocalPlayersNameDialog");
    }

    @FXML
    private void onPlayNetworkMultiplayer() throws IOException {
        App.setRoot("auth");
    }
}
