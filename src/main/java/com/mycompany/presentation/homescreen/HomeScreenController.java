package com.mycompany.presentation.homescreen;

import com.mycompany.core.navigation.Routes;
import com.mycompany.core.utils.SoundManager;
import com.mycompany.App;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;

public class HomeScreenController {

    HomeManager homeManager;

    @FXML
    private Button btnPlayVsComputer;

    @FXML
    private Button btnLocalMultiplayer;

    @FXML
    private Button btnNetworkMultiplayer;

    @FXML
    private Button musicToggleButton;

    @FXML
    private SVGPath musicIcon;

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

    @FXML
    private void onToggleMusic() {
        SoundManager.getInstance().toggleBackgroundMusic();
        updateMusicIcon();
    }

    private void updateMusicIcon() {
        if (musicIcon != null) {
            if (SoundManager.getInstance().isBackgroundMusicEnabled()) {
                // Music playing icon
                musicIcon.setContent(
                        "M11 5L6 9H2v6h4l5 4V5zM15.54 8.46a5 5 0 0 1 0 7.07M19.07 4.93a10 10 0 0 1 0 14.14");
            } else {
                // Music muted icon
                musicIcon.setContent("M11 5L6 9H2v6h4l5 4V5zM23 9l-6 6M17 9l6 6");
            }
        }
    }
}
