package com.mycompany.clientxo;

import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class LobbyScreenController implements Initializable {

    @FXML
    private Label lblPlayersCount;
    @FXML
    private Label lblOnlineCount;
    @FXML
    private Label lblOfflineCount;
    @FXML
    private VBox onlinePlayersList;
    @FXML
    private VBox offlinePlayersList;

    private String currentPlayerId = "currentUser";
    private String pendingChallengeId = null;
    @FXML
    private Button btnBack;

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

    private final List<Player> MOCK_PLAYERS = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize Mock Data
        MOCK_PLAYERS.add(new Player("1", "CyberNinja", true, 15, "robot"));
        MOCK_PLAYERS.add(new Player("2", "PixelMaster", true, 23, "ghost"));
        MOCK_PLAYERS.add(new Player("3", "GameWizard", true, 8, "dragon"));
        MOCK_PLAYERS.add(new Player("4", "NeonKnight", false, 42, "robot"));
        MOCK_PLAYERS.add(new Player("5", "ByteRunner", false, 31, "alien"));
        MOCK_PLAYERS.add(new Player("6", "CodeBreaker", false, 19, "ghost"));

        updatePlayerList();

        // Simulate receiving a challenge
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Platform.runLater(() -> promptChallengeReceived(MOCK_PLAYERS.get(1)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updatePlayerList() {
        List<Player> online = MOCK_PLAYERS.stream().filter(p -> p.online).collect(Collectors.toList());
        List<Player> offline = MOCK_PLAYERS.stream().filter(p -> !p.online).collect(Collectors.toList());

        lblPlayersCount.setText("Players (" + MOCK_PLAYERS.size() + ")");
        lblOnlineCount.setText("Online (" + online.size() + ")");
        lblOfflineCount.setText("Offline (" + offline.size() + ")");

        onlinePlayersList.getChildren().clear();
        for (Player p : online) {
            onlinePlayersList.getChildren().add(createPlayerItem(p));
        }

        offlinePlayersList.getChildren().clear();
        for (Player p : offline) {
            offlinePlayersList.getChildren().add(createPlayerItem(p));
        }
    }

    private HBox createPlayerItem(Player player) {
        HBox item = new HBox(10);
        item.getStyleClass().add("player-item");
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Avatar
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("player-avatar");
        Label avatarEmoji = new Label(getCharacterSymbol(player.characterId));
        avatarEmoji.getStyleClass().add("player-avatar-text");
        avatar.getChildren().add(avatarEmoji);

        // Info
        VBox info = new VBox(2);
        Label name = new Label(player.name);
        name.getStyleClass().add("player-name");
        Label score = new Label("Score: " + player.score);
        score.getStyleClass().add("player-score");
        info.getChildren().addAll(name, score);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Action Button
        Button btnAction = new Button();
        btnAction.getStyleClass().addAll("game-button", "game-button-outline"); // Smaller variant if possible
        btnAction.setStyle("-fx-font-size: 12px; -fx-padding: 8 16; -fx-min-height: 36px; -fx-pref-height: 36px;");

        if (player.online) {
            if (pendingChallengeId != null && pendingChallengeId.equals(player.id)) {
                btnAction.setText("Waiting...");
                btnAction.setDisable(true);
            } else {
                btnAction.setText("Challenge");
                btnAction.setOnAction(e -> handleChallenge(player));
                btnAction.setDisable(pendingChallengeId != null); // Disable if waiting for another
            }
        } else {
            Label offlineLbl = new Label("Offline");
            offlineLbl.getStyleClass().add("player-score");
            item.getChildren().addAll(avatar, info, offlineLbl);
            return item;
        }

        item.getChildren().addAll(avatar, info, btnAction);
        return item;
    }

    private void handleChallenge(Player player) {
        pendingChallengeId = player.id;
        updatePlayerList(); // Refresh UI to show waiting state

        // Simulate network response
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    boolean accepted = Math.random() > 0.3;
                    pendingChallengeId = null;
                    updatePlayerList();

                    if (accepted) {
                        javafx.scene.control.CheckBox chkRecord = new javafx.scene.control.CheckBox("Record Game");
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Challenge Accepted");
                        alert.setHeaderText(player.name + " accepted your challenge!");
                        alert.getDialogPane().setContent(chkRecord);
                        alert.showAndWait();
                        // T0DO: Navigate to Game Screen
                        boolean isRecording = chkRecord.isSelected();
                        System.out.println("Starting Game (Client Initiated). Recording Enabled: " + isRecording);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void promptChallengeReceived(Player challenger) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Challenge Received");
        alert.setHeaderText(challenger.name + " wants to play!");

        javafx.scene.control.CheckBox chkRecord = new javafx.scene.control.CheckBox("Record Game");
        alert.getDialogPane().setContent(chkRecord);

        ButtonType acceptBtn = new ButtonType("Accept");
        ButtonType rejectBtn = new ButtonType("Reject");

        alert.getButtonTypes().setAll(acceptBtn, rejectBtn);

        alert.showAndWait().ifPresent(type -> {
            if (type == acceptBtn) {
                // Start Game
                System.out.println("Challenge accepted");
                boolean isRecording = chkRecord.isSelected();
                System.out.println("Starting Game (Client Accepted). Recording Enabled: " + isRecording);
            } else {
                System.out.println("Challenge rejected");
            }
        });
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
