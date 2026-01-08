package com.mycompany.presentation.lobbyscreen;

import com.mycompany.core.navigation.Routes;

import com.mycompany.App;

import java.net.URL;
import java.util.List;
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
        
        loadFriends();
    }

    private void loadFriends(boolean sortByScore) {
        // Run on background thread to avoid blocking UI
        new Thread(() -> {
            // storage in ArrayList to ensure mutability
            List<Player> friends = new java.util.ArrayList<>(lobbyManager.getFriends());
            
            if (sortByScore) {
                // Add current player for leaderboard
                Player currentPlayer = lobbyManager.getCurrentPlayer();
                if (currentPlayer != null) {
                    friends.add(currentPlayer);
                }
                
                // Sort by score descending (high to low)
                friends.sort((p1, p2) -> Long.compare(p2.getScore(), p1.getScore()));
                currentView = "LEADERBOARD";
            } else {
                 currentView = "FRIENDS";
            }
            
            javafx.application.Platform.runLater(() -> {
                updatePlayerList(friends);
            });
        }).start();
    }
    
    // Default loadFriends for initial load (Friends view)
    private void loadFriends() {
        loadFriends(false);
    }

    private void updatePlayerList(java.util.List<Player> players) {
        if (players == null) return;
        
        int onlineCount = 0;
        int offlineCount = 0;
        
        listContainer.getChildren().clear();
        
        for (Player p : players) {
            // Check if it's the current user
            Player currentUser = lobbyManager.getCurrentPlayer();
            if (currentUser != null && p.getId() == currentUser.getId()) {
                // If in FRIENDS view, skip current user (don't show self in friends list)
                // If in LEADERBOARD view, show current user
                if (!"LEADERBOARD".equals(currentView)) {
                    continue;
                }
            }

            boolean isOnline = p.isIsActive(); // Using isActive as isOnline
            if (isOnline) onlineCount++; else offlineCount++;
            
            listContainer.getChildren().add(createPlayerItem(p));
        }
        
        if (lblOnlineCount != null) lblOnlineCount.setText("Online (" + onlineCount + ")");
        if (lblOfflineCount != null) lblOfflineCount.setText("Offline (" + offlineCount + ")");
    }

    private HBox createPlayerItem(Player player) {
        HBox item = new HBox(10);
        item.getStyleClass().add("player-item");
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Avatar
        javafx.scene.layout.StackPane avatar = new javafx.scene.layout.StackPane();
        avatar.getStyleClass().add("player-avatar");
        Label avatarEmoji = new Label(getCharacterSymbol(player.getAvatar()));
        avatarEmoji.getStyleClass().add("player-avatar-text");
        
        // Status Indicator
        javafx.scene.shape.Circle statusDot = new javafx.scene.shape.Circle(5);
        if (player.isIsActive()) {
            statusDot.setFill(javafx.scene.paint.Color.LIMEGREEN);
        } else {
            statusDot.setFill(javafx.scene.paint.Color.GRAY);
        }
        statusDot.setStroke(javafx.scene.paint.Color.WHITE);
        statusDot.setStrokeWidth(1);
        
        avatar.getChildren().addAll(avatarEmoji, statusDot);
        javafx.scene.layout.StackPane.setAlignment(statusDot, javafx.geometry.Pos.BOTTOM_RIGHT);
        javafx.scene.layout.StackPane.setMargin(statusDot, new javafx.geometry.Insets(0, 0, 4, 4));

        // Info
        VBox info = new VBox(2);
        Label name = new Label(player.getUserName());
        name.getStyleClass().add("player-name");
        Label score = new Label("Score: " + player.getScore());
        score.getStyleClass().add("player-score");
        info.getChildren().addAll(name, score);
        javafx.scene.layout.HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        // Action Button
        Button btnAction = new Button();
        btnAction.getStyleClass().addAll("game-button", "game-button-outline");
        btnAction.setStyle("-fx-font-size: 12px; -fx-padding: 8 16; -fx-min-height: 36px; -fx-pref-height: 36px;");
        btnAction.setText("Challenge");
        
        // "button getChlange do not do any ting in him"
        btnAction.setOnAction(e -> {
            // Do nothing as requested
             System.out.println("Challenge clicked for " + player.getUserName());
        });

        item.getChildren().addAll(avatar, info, btnAction);
        return item;
    }

    @FXML
    private void onTabFriends() {
        loadFriends(false);
    }

    @FXML
    private void onTabLeaderboard() {
         loadFriends(true);
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

            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                 dialogPane.getStylesheets().add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
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
