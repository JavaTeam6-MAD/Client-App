package com.mycompany.clientxo.presentation.difficultyscreen;

import com.mycompany.clientxo.App;

import java.io.IOException;
import javafx.fxml.FXML;

public class DifficultyScreenController {

    @FXML
    private void onEasy() throws IOException {
        ComputerGameController.difficulty = 1;
        App.setRoot("GameScreen");
    }

    @FXML
    private void onMedium() throws IOException {
        ComputerGameController.difficulty = 2;
        App.setRoot("GameScreen");
    }

    @FXML
    private void onHard() throws IOException {
        ComputerGameController.difficulty = 3;
        App.setRoot("GameScreen");
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot("primary");
    }
}
