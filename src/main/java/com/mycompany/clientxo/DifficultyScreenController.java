package com.mycompany.clientxo;

import java.io.IOException;
import javafx.fxml.FXML;

public class DifficultyScreenController {

    @FXML
    private void onEasy() throws IOException {
        System.out.println("Selected Difficulty: Easy");
        // TODO: Pass difficulty to game screen
        // App.setRoot("game");
    }

    @FXML
    private void onMedium() throws IOException {
        System.out.println("Selected Difficulty: Medium");
        // TODO: Pass difficulty to game screen
        // App.setRoot("game");
    }

    @FXML
    private void onHard() throws IOException {
        System.out.println("Selected Difficulty: Hard");
        // TODO: Pass difficulty to game screen
        // App.setRoot("game");
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot("primary");
    }
}
