package com.mycompany.clientxo;

import java.io.IOException;
import javafx.fxml.FXML;

public class DifficultyScreenController {

    @FXML
    private void onEasy() throws IOException {
        System.out.println("Selected Difficulty: Easy");
        GameState.getInstance().setCurrentDifficulty(GameState.Difficulty.EASY);
        App.setRoot("GameScreen");
    }

    @FXML
    private void onMedium() throws IOException {
        System.out.println("Selected Difficulty: Medium");
        GameState.getInstance().setCurrentDifficulty(GameState.Difficulty.MEDIUM);
        App.setRoot("GameScreen");
    }

    @FXML
    private void onHard() throws IOException {
        System.out.println("Selected Difficulty: Hard");
        GameState.getInstance().setCurrentDifficulty(GameState.Difficulty.HARD);
        App.setRoot("GameScreen");
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot("primary");
    }
}
