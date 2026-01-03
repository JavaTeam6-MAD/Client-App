package com.mycompany.clientxo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class LobbyScreenController implements Initializable {

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

    // Data Models
    public static class Player {
        String id;
        String name;
        boolean online;
        int score;
        String characterId;

        public Player(String id, String name, boolean online, int score, String characterId) {
            this.id = id;
            this.name = name;
            this.online = online;
            this.score = score;
            this.characterId = characterId;
        }
    }

    private final List<Player> ALL_PLAYERS = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load User Data
        String currentUser = GameState.getInstance().getUsername();
        String currentIcon = GameState.getInstance().getCharacterId();
        int score = GameState.getInstance().getScore();

        lblUserName.setText(currentUser);
        lblUserScore.setText("Score: " + score);
        lblUserChar.setText(getCharacterSymbol(currentIcon));

        // Initialize Mock Data
        ALL_PLAYERS.add(new Player("1", "CyberNinja", true, 150, "robot"));
        ALL_PLAYERS.add(new Player("2", "PixelMaster", true, 230, "ghost"));
        ALL_PLAYERS.add(new Player("3", "GameWizard", true, 80, "dragon"));
        ALL_PLAYERS.add(new Player("4", "NeonKnight", false, 420, "robot"));
        ALL_PLAYERS.add(new Player("5", "ByteRunner", false, 310, "alien"));
        ALL_PLAYERS.add(new Player("6", "CodeBreaker", false, 190, "ghost"));
        // Add self to list for leaderboard context
        ALL_PLAYERS.add(new Player("me", currentUser, true, score, currentIcon));

        // Default View
        onTabFriends();

        // Simulate receiving a challenge
        new Thread(() -> {
            try {
                Thread.sleep(8000);
                Platform.runLater(() -> promptChallengeReceived(ALL_PLAYERS.get(0)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onTabFriends() {
        currentView = "FRIENDS";
        updateTabs();
        updateList();
    }

    @FXML
    private void onTabLeaderboard() {
        currentView = "LEADERBOARD";
        updateTabs();
        updateList();
    }

    private void updateTabs() {
        boolean isFriends = "FRIENDS".equals(currentView);

        // Active Style Logic (Mock visual toggle)
        btnTabFriends
                .setStyle(isFriends ? "-fx-background-color: rgba(233, 69, 96, 0.2); -fx-border-color: #e94560;" : "");
        btnTabLeaderboard
                .setStyle(!isFriends ? "-fx-background-color: rgba(233, 69, 96, 0.2); -fx-border-color: #e94560;" : "");

        friendsHeader.setVisible(isFriends);
        friendsHeader.setManaged(isFriends);
    }

    private void updateList() {
        listContainer.getChildren().clear();

        if ("FRIENDS".equals(currentView)) {
            // Filter friends (everyone else for demo)
            List<Player> friends = ALL_PLAYERS.stream()
                    .filter(p -> !p.id.equals("me"))
                    .collect(Collectors.toList());

            // Sort by Online then Name
            friends.sort((p1, p2) -> {
                if (p1.online != p2.online)
                    return p1.online ? -1 : 1;
                return p1.name.compareTo(p2.name);
            });

            long onlineCount = friends.stream().filter(p -> p.online).count();
            lblOnlineCount.setText("Online (" + onlineCount + ")");
            lblOfflineCount.setText("Offline (" + (friends.size() - onlineCount) + ")");

            for (Player p : friends) {
                listContainer.getChildren().add(createFriendItem(p));
            }

        } else {
            // Leaderboard
            List<Player> leaderboard = new ArrayList<>(ALL_PLAYERS);
            leaderboard.sort(Comparator.comparingInt((Player p) -> p.score).reversed());

            int rank = 1;
            for (Player p : leaderboard) {
                listContainer.getChildren().add(createLeaderboardItem(p, rank++));
            }
        }
    }

    private HBox createFriendItem(Player player) {
        HBox item = new HBox(15);
        item.getStyleClass().add("player-item");
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        item.setStyle("-fx-padding: 12 16;");

        // Avatar
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("player-avatar");
        Label avatarEmoji = new Label(getCharacterSymbol(player.characterId));
        avatarEmoji.getStyleClass().add("player-avatar-text");
        avatar.getChildren().add(avatarEmoji);

        // Status Dot
        SVGPath statusDot = new SVGPath();
        statusDot.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z");
        statusDot.setScaleX(0.4);
        statusDot.setScaleY(0.4);
        statusDot.getStyleClass().add(player.online ? "status-indicator-online" : "status-indicator-offline");

        StackPane.setAlignment(statusDot, javafx.geometry.Pos.BOTTOM_RIGHT);
        avatar.getChildren().add(statusDot);

        // Info
        VBox info = new VBox(2);
        Label name = new Label(player.name);
        name.getStyleClass().add("player-name");
        Label status = new Label(player.online ? "Online" : "Offline");
        status.getStyleClass().add("player-score"); // Reuse style for subtlety
        info.getChildren().addAll(name, status);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Action Button
        Button btnAction = new Button();
        btnAction.getStyleClass().addAll("game-button", "game-button-outline"); // Smaller variant if possible
        btnAction.setStyle("-fx-font-size: 12px; -fx-padding: 6 12; -fx-min-height: 32px; -fx-pref-height: 32px;");

        if (player.online) {
            if (pendingChallengeId != null && pendingChallengeId.equals(player.id)) {
                btnAction.setText("Request Sent...");
                btnAction.setDisable(true);
            } else {
                btnAction.setText("Challenge");
                btnAction.setOnAction(e -> handleChallenge(player));
                btnAction.setDisable(pendingChallengeId != null); // Disable if waiting for another
            }
            item.getChildren().addAll(avatar, info, btnAction);
        } else {
            // Cannot challenge offline
            item.setOpacity(0.6);
            item.getChildren().addAll(avatar, info);
        }

        return item;
    }

    private HBox createLeaderboardItem(Player player, int rank) {
        boolean isMe = player.id.equals("me");

        HBox item = new HBox(15);
        item.getStyleClass().add("player-item");
        // Highlight self
        if (isMe) {
            item.setStyle(
                    "-fx-border-color: #e94560; -fx-background-color: rgba(233, 69, 96, 0.1); -fx-padding: 12 16;");
        } else {
            item.setStyle("-fx-padding: 12 16;");
        }
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Rank
        Label lblRank = new Label("#" + rank);
        lblRank.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-min-width: 30;");

        // Avatar
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("player-avatar");
        Label avatarEmoji = new Label(getCharacterSymbol(player.characterId));
        avatarEmoji.getStyleClass().add("player-avatar-text");
        avatar.getChildren().add(avatarEmoji);

        // Name
        Label name = new Label(player.name + (isMe ? " (You)" : ""));
        name.getStyleClass().add("player-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Score
        Label score = new Label(String.valueOf(player.score));
        score.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e94560;");
        item.getChildren().addAll(lblRank, avatar, name, spacer, score);
        return item;
    }

    private void styleAlert(Alert alert) {
        alert.initStyle(javafx.stage.StageStyle.UNDECORATED);
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        dialogPane.getStyleClass().add("my-dialog");

        // Make draggable
        final Delta dragDelta = new Delta();
        dialogPane.setOnMousePressed(e -> {
            dragDelta.x = alert.getX() - e.getScreenX();
            dragDelta.y = alert.getY() - e.getScreenY();
        });
        dialogPane.setOnMouseDragged(e -> {
            alert.setX(e.getScreenX() + dragDelta.x);
            alert.setY(e.getScreenY() + dragDelta.y);
        });
    }

    // Helper for dragging
    class Delta {
        double x, y;
    }

    private void handleChallenge(Player player) {
        pendingChallengeId = player.id;
        updateList(); // Refresh UI to show waiting state

        // Simulate network
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Platform.runLater(() -> {
                    boolean accepted = Math.random() > 0.3; // 70% chance accept
                    pendingChallengeId = null;
                    updateList();

                    if (accepted) {
                        showChallengeAcceptedDialog(player);
                    } else {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Challenge Rejected");
                        alert.setHeaderText(player.name + " is busy or rejected your invite.");
                        styleAlert(alert);
                        alert.show();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showChallengeAcceptedDialog(Player player) {
        javafx.scene.control.CheckBox chkRecord = new javafx.scene.control.CheckBox("Record Game");
        chkRecord.setStyle("-fx-text-fill: white;"); // Fix text color for dark theme

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Challenge Accepted");
        alert.setHeaderText(player.name + " Accepted!");
        alert.setContentText("Do you want to start now?");
        alert.getDialogPane().setExpandableContent(chkRecord);
        styleAlert(alert);

        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                boolean isRecording = chkRecord.isSelected();
                System.out.println("Starting Game vs " + player.name + ". Recording: " + isRecording);

                // Navigate to Game
                try {
                    // Set opponent in GameState if needed (mocking for now navigation)
                    App.setRoot("GameScreen");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void promptChallengeReceived(Player challenger) {
        javafx.scene.control.CheckBox chkRecord = new javafx.scene.control.CheckBox("Record Game");
        chkRecord.setStyle("-fx-text-fill: white;"); // Fix text color for dark theme

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Incoming Challenge");
        alert.setHeaderText(challenger.name + " wants to play!");
        alert.getDialogPane().setExpandableContent(chkRecord);
        styleAlert(alert);

        ButtonType acceptBtn = new ButtonType("Accept");
        ButtonType rejectBtn = new ButtonType("Reject");

        alert.getButtonTypes().setAll(acceptBtn, rejectBtn);

        alert.showAndWait().ifPresent(type -> {
            if (type == acceptBtn) {
                System.out.println(
                        "Accepted challenge from " + challenger.name + ". Recording: " + chkRecord.isSelected());
                try {
                    App.setRoot("GameScreen");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void onProfile() {
        try {
            App.setRoot("ProfileScreen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordedGames() {
        // Mock Recorded Games List
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Recorded Games");
        alert.setHeaderText("Your Match History");
        alert.setContentText("1. vs CyberNinja (Win)\n2. vs PixelMaster (Loss)\n(No other games found)");
        styleAlert(alert);
        alert.show();
    }

    @FXML
    private void onBack() {
        try {
            App.setRoot("primary");
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
