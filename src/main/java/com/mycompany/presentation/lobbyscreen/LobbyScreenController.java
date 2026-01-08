package com.mycompany.presentation.lobbyscreen;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.mycompany.model.app.Player;

public class LobbyScreenController implements Initializable {

    LobbyManager lobbyManager;

    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserScore;
    @FXML
    private Label lblUserChar;
    @FXML
    private Button btnTabFriends;
    @FXML
    private Button btnTabLeaderboard;
    @FXML
    private HBox friendsHeader;
    @FXML
    private Label lblOnlineCount;
    @FXML
    private Label lblOfflineCount;
    @FXML
    private VBox listContainer;

    private String currentView = "FRIENDS"; // FRIENDS or LEADERBOARD
    private String pendingChallengeId = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lobbyManager = new LobbyManager();
        Player player = lobbyManager.getCurrentPlayer();
        if (player != null) {
            lblUserName.setText(player.getUserName());
            if (lblUserScore != null) {
                lblUserScore.setText(String.valueOf(player.getScore()));
            }
            if (lblUserChar != null) {
                lblUserChar.setText(getCharacterSymbol(player.getAvatar()));
            }
        }
    }

    @FXML
    private void onTabFriends() {

    }

    @FXML
    private void onTabLeaderboard() {

    }

    @FXML
    private void onProfile() {
        try {
            App.setRoot(Routes.PROFILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordedGames() {
        try {
            App.setRoot(Routes.GAME_HISTORY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBack() {
        try {
            lobbyManager.leaveLobby();
            App.setRoot(Routes.HOME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            // Apply custom styling to the dialog pane if possible, or rely on global CSS
            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                lobbyManager.logout();
                App.setRoot(Routes.AUTH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCharacterSymbol(String charId) {
        if (charId == null)
            return "👤";
        switch (charId) {
            case "dragon":
                return "🐲";
            case "robot":
                return "🤖";
            case "alien":
                return "👽";
            case "ghost":
                return "👻";
            default:
                return "👤";
        }
    }
}
