package com.mycompany.clientxo;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class GameHistoryController implements Initializable {

    @FXML
    private VBox gamesListContainer;

    @FXML
    private Label lblEmptyState;

    @FXML
    private Label lblTotalGames;

    @FXML
    private Label lblWins;

    @FXML
    private Label lblLosses;

    @FXML
    private Label lblDraws;

    private List<GameHistory> gameHistoryList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadGameHistory();
        displayGameHistory();
        updateStats();
    }

    private void loadGameHistory() {
        // Mock data - In a real application, this would fetch from a server/database
        gameHistoryList = new ArrayList<>();

        gameHistoryList.add(new GameHistory(
                "game001",
                LocalDateTime.now().minusDays(1).minusHours(3),
                "CyberNinja",
                "robot",
                true,
                false,
                true));

        gameHistoryList.add(new GameHistory(
                "game002",
                LocalDateTime.now().minusDays(2).minusHours(5),
                "PixelMaster",
                "alien",
                false,
                false,
                true));

        gameHistoryList.add(new GameHistory(
                "game003",
                LocalDateTime.now().minusDays(3).minusHours(7),
                "GhostKing",
                "ghost",
                true,
                false,
                false));

        gameHistoryList.add(new GameHistory(
                "game004",
                LocalDateTime.now().minusDays(5).minusHours(2),
                "DragonSlayer",
                "dragon",
                false,
                true,
                true));

        gameHistoryList.add(new GameHistory(
                "game005",
                LocalDateTime.now().minusDays(7).minusHours(4),
                "NeonWarrior",
                "robot",
                true,
                false,
                true));
    }

    private void displayGameHistory() {
        if (gameHistoryList.isEmpty()) {
            lblEmptyState.setVisible(true);
            lblEmptyState.setManaged(true);
        } else {
            lblEmptyState.setVisible(false);
            lblEmptyState.setManaged(false);

            for (GameHistory game : gameHistoryList) {
                VBox gameCard = createGameCard(game);
                gamesListContainer.getChildren().add(gameCard);
            }
        }
    }

    private VBox createGameCard(GameHistory game) {
        VBox card = new VBox(12);
        card.getStyleClass().add("player-item");
        card.setStyle("-fx-padding: 16; -fx-background-color: rgba(255, 255, 255, 0.05); " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-radius: 12; -fx-background-radius: 12;");

        // Top row: Date/Time and Result badge
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Date and Time
        VBox dateTimeBox = new VBox(2);
        Label dateLabel = new Label(game.getFormattedDate());
        dateLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label timeLabel = new Label(game.getFormattedTime());
        timeLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");
        dateTimeBox.getChildren().addAll(dateLabel, timeLabel);

        // Spacer
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // Result Badge
        Label resultBadge = new Label(game.getResultText());
        resultBadge.setStyle("-fx-background-color: " + game.getResultColor() + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; " +
                "-fx-padding: 6 12; -fx-background-radius: 20;");

        topRow.getChildren().addAll(dateTimeBox, spacer1, resultBadge);

        // Bottom row: Opponent info and replay button
        HBox bottomRow = new HBox(12);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        // Opponent Avatar
        StackPane avatar = new StackPane();
        avatar.setStyle("-fx-background-color: rgba(233, 69, 96, 0.2); " +
                "-fx-background-radius: 50%; -fx-min-width: 40; -fx-min-height: 40;");
        Label avatarLabel = new Label(getCharacterSymbol(game.getOpponentCharacter()));
        avatarLabel.setStyle("-fx-font-size: 20px;");
        avatar.getChildren().add(avatarLabel);

        // Opponent Name
        VBox opponentInfo = new VBox(2);
        Label vsLabel = new Label("VS");
        vsLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 10px;");
        Label opponentName = new Label(game.getOpponentName());
        opponentName.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold; -fx-font-size: 14px;");
        opponentInfo.getChildren().addAll(vsLabel, opponentName);

        // Spacer
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        bottomRow.getChildren().addAll(avatar, opponentInfo, spacer2);

        // Watch Replay Button
        if (game.isRecordedGameAvailable()) {
            Button replayBtn = new Button("Watch Replay");
            replayBtn.getStyleClass().addAll("game-button", "game-button-outline");
            replayBtn.setStyle("-fx-padding: 8 16; -fx-font-size: 12px;");

            // Add play icon
            SVGPath playIcon = new SVGPath();
            playIcon.setContent("M8 5v14l11-7z");
            playIcon.getStyleClass().add("button-icon");
            playIcon.setStyle("-fx-scale-x: 0.7; -fx-scale-y: 0.7;");
            replayBtn.setGraphic(playIcon);

            replayBtn.setOnAction(e -> onWatchReplay(game));
            bottomRow.getChildren().add(replayBtn);
        }

        card.getChildren().addAll(topRow, bottomRow);
        return card;
    }

    private void updateStats() {
        int total = gameHistoryList.size();
        int wins = (int) gameHistoryList.stream().filter(GameHistory::isPlayerWon).count();
        int draws = (int) gameHistoryList.stream().filter(GameHistory::isDraw).count();
        int losses = total - wins - draws;

        lblTotalGames.setText(String.valueOf(total));
        lblWins.setText(String.valueOf(wins));
        lblLosses.setText(String.valueOf(losses));
        lblDraws.setText(String.valueOf(draws));
    }

    @FXML
    private void onBack() {
        try {
            App.setRoot("LobbyScreen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onWatchReplay(GameHistory game) {
        // In a real application, this would load and display the recorded game
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Watch Replay");
        alert.setHeaderText("Game #" + game.getGameId());
        alert.setContentText("Loading replay vs " + game.getOpponentName() + "...\n\n" +
                "Date: " + game.getFormattedDate() + " at " + game.getFormattedTime() + "\n" +
                "Result: " + game.getResultText() + "\n\n" +
                "(Replay functionality will be implemented here)");
        alert.show();
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
