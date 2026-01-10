package com.mycompany.presentation.gamehistory;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;
import com.mycompany.presentation.gamehistory.ReplayGameController;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

    // Filter buttons
    @FXML
    private Button btnFilterAll;

    @FXML
    private Button btnFilterWin;

    @FXML
    private Button btnFilterLoss;

    @FXML
    private Button btnFilterDraw;

    @FXML
    private Button btnToggleRecorded;

    private List<GameHistory> gameHistoryList;

    // Filter state
    private enum ResultFilter {
        ALL, WIN, LOSS, DRAW
    }

    private ResultFilter currentResultFilter = ResultFilter.ALL;
    private boolean showOnlyRecorded = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadGameHistory();
        updateFilterButtonStates();
        displayGameHistory();
        updateStats();
    }

    private void loadGameHistory() {
        gameHistoryList = new ArrayList<>();
        List<com.mycompany.core.util.GameRecorder.RecordedGame> files = com.mycompany.core.util.GameRecorder
                .getRecordedGames();

        for (com.mycompany.core.util.GameRecorder.RecordedGame rec : files) {
            String gameId = rec.getGameId();
            LocalDateTime date = LocalDateTime.now();
            try {
                date = LocalDateTime.parse(rec.getDate(), java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String p1 = rec.getPlayer1Name();
            String p2 = rec.getPlayer2Name();
            String winner = rec.getWinnerName();

            // Logic: One of the players is "You" (based on NetworkGameController logic)
            // Or if we used real names, we match session name.
            // Current NetworkGameController saves "You" for the local player.

            boolean amIP1 = "You".equals(p1);
            boolean amIP2 = "You".equals(p2);

            // Fallback if "You" logic changes, try to match current player name?
            // But strict "You" check is what we implemented.

            String opponentName = amIP1 ? p2 : (amIP2 ? p1 : "Unknown");
            String opponentChar = "robot"; // Default for now

            boolean isDraw = rec.isDraw();
            boolean playerWon = false;

            if (!isDraw) {
                if ("You".equals(winner)) {
                    playerWon = true;
                } else if (amIP1 && p1.equals(winner)) {
                    playerWon = true;
                } else if (amIP2 && p2.equals(winner)) {
                    playerWon = true;
                }
            }

            gameHistoryList.add(new GameHistory(
                    gameId,
                    date,
                    opponentName,
                    opponentChar,
                    playerWon,
                    isDraw,
                    true // Since we loaded it from recorder, it is available
            ));
        }

        // Mock data can be removed or kept for mixed entries?
        // Instructions: "Implementing Replay... integrating recorded games into game
        // history"
        // Implicitly we should show REAL recorded games.
        // We can keep mock data if no recordings? Or just replace mock data.
        // I will keep only REAL data to prove it works.
        // But if list is empty, user might think it's broken during demo if they don't
        // record.
        // I will add ONE mock entry if list is empty, or just leave it empty.
        // The instructions say "Integration".
        // I'll leave it empty (load ONLY recorded games) for "Real" feeling.
    }

    private void displayGameHistory() {
        // Clear existing items
        gamesListContainer.getChildren().clear();

        // Apply filters
        List<GameHistory> filteredGames = gameHistoryList.stream()
                .filter(game -> {
                    // Apply result filter
                    switch (currentResultFilter) {
                        case WIN:
                            return game.isPlayerWon() && !game.isDraw();
                        case LOSS:
                            return !game.isPlayerWon() && !game.isDraw();
                        case DRAW:
                            return game.isDraw();
                        case ALL:
                        default:
                            return true;
                    }
                })
                .filter(game -> !showOnlyRecorded || game.isRecordedGameAvailable())
                .collect(java.util.stream.Collectors.toList());

        if (filteredGames.isEmpty()) {
            lblEmptyState
                    .setText(gameHistoryList.isEmpty() ? "No games played yet" : "No games match the selected filters");
            lblEmptyState.setVisible(true);
            lblEmptyState.setManaged(true);
        } else {
            lblEmptyState.setVisible(false);
            lblEmptyState.setManaged(false);

            for (GameHistory game : filteredGames) {
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
            App.setRoot(Routes.LOBBY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onWatchReplay(GameHistory game) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    App.class.getResource(Routes.REPLAY_GAME + ".fxml"));
            javafx.scene.Parent root = loader.load();

            com.mycompany.presentation.gamehistory.ReplayGameController controller = loader.getController();

            // Find the full RecordedGame object
            List<com.mycompany.core.util.GameRecorder.RecordedGame> files = com.mycompany.core.util.GameRecorder
                    .getRecordedGames();
            com.mycompany.core.util.GameRecorder.RecordedGame selectedGame = files.stream()
                    .filter(g -> g.getGameId().equals(game.getGameId()))
                    .findFirst()
                    .orElse(null);

            if (selectedGame != null) {
                controller.setRecordedGame(selectedGame);
                App.setRoot(root);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Replay Unavailable");
                alert.setContentText("Could not find recording data for this game.");
                alert.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Navigation Error");
            alert.setContentText("Could not load Replay Screen.");
            alert.show();
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

    // Filter handlers
    @FXML
    private void onFilterAll() {
        currentResultFilter = ResultFilter.ALL;
        updateFilterButtonStates();
        displayGameHistory();
    }

    @FXML
    private void onFilterWin() {
        currentResultFilter = ResultFilter.WIN;
        updateFilterButtonStates();
        displayGameHistory();
    }

    @FXML
    private void onFilterLoss() {
        currentResultFilter = ResultFilter.LOSS;
        updateFilterButtonStates();
        displayGameHistory();
    }

    @FXML
    private void onFilterDraw() {
        currentResultFilter = ResultFilter.DRAW;
        updateFilterButtonStates();
        displayGameHistory();
    }

    @FXML
    private void onToggleRecorded() {
        showOnlyRecorded = !showOnlyRecorded;
        btnToggleRecorded.setText(showOnlyRecorded ? "ON" : "OFF");
        highlightButton(btnToggleRecorded, showOnlyRecorded);
        displayGameHistory();
    }

    private void updateFilterButtonStates() {
        highlightButton(btnFilterAll, currentResultFilter == ResultFilter.ALL);
        highlightButton(btnFilterWin, currentResultFilter == ResultFilter.WIN);
        highlightButton(btnFilterLoss, currentResultFilter == ResultFilter.LOSS);
        highlightButton(btnFilterDraw, currentResultFilter == ResultFilter.DRAW);
        highlightButton(btnToggleRecorded, showOnlyRecorded);
    }

    private void highlightButton(Button btn, boolean selected) {
        if (selected) {
            btn.setStyle(
                    "-fx-border-color: #00FFFF; -fx-background-color: rgba(0, 255, 255, 0.2); -fx-padding: 4 12; -fx-font-size: 11px; -fx-min-width: 50;");
        } else {
            btn.setStyle("-fx-padding: 4 12; -fx-font-size: 11px; -fx-min-width: 50;");
        }
    }
}
