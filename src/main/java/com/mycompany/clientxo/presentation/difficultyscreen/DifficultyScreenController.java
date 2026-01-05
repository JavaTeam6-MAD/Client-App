package com.mycompany.clientxo.presentation.difficultyscreen;

import com.mycompany.clientxo.App;

import java.io.IOException;
import javafx.fxml.FXML;

public class DifficultyScreenController {

    @FXML
    private void onEasy() throws IOException {
        App.setRoot("GameScreen");
    }

    @FXML
    private void onMedium() throws IOException {
        App.setRoot("GameScreen");
    }

    @FXML
    private void onHard() throws IOException {
        App.setRoot("GameScreen");
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot("primary");
    }
}
