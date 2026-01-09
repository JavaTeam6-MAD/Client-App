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
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog("localhost");
        dialog.setTitle("Server Configuration");
        dialog.setHeaderText("Enter Server IP Address");
        dialog.setContentText("IP Address:");

        // Add proper styling
        javafx.scene.control.DialogPane dialogPane = dialog.getDialogPane();
        if (getClass().getResource("/com/mycompany/styles.css") != null) {
            dialogPane.getStylesheets().add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
        }
        dialogPane.getStyleClass().add("dialog-pane");

        java.util.Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String serverIp = result.get();
            if (serverIp != null && !serverIp.trim().isEmpty()) {
                com.mycompany.data.datasource.remote.RemoteDataSource.setServerIp(serverIp.trim());

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
                        alert.setContentText(
                                "Could not connect to server at " + serverIp + ". Please check IP and try again.");
                        if (getClass().getResource("/com/mycompany/styles.css") != null) {
                            alert.getDialogPane().getStylesheets()
                                    .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
                        }
                        alert.getDialogPane().getStyleClass().add("dialog-pane");
                        alert.showAndWait();
                    }
                } else {
                    App.setRoot(Routes.AUTH);
                }
            }
        }
    }
}
