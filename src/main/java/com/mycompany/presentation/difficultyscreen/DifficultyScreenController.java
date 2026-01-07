package com.mycompany.presentation.difficultyscreen;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;

import java.io.IOException;
import javafx.fxml.FXML;

public class DifficultyScreenController {

    @FXML
    private void onEasy() throws IOException {
        App.setRoot(Routes.GAME);
    }

    @FXML
    private void onMedium() throws IOException {
        App.setRoot(Routes.GAME);
    }

    @FXML
    private void onHard() throws IOException {
        App.setRoot(Routes.GAME);
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot(Routes.HOME);
    }
}
