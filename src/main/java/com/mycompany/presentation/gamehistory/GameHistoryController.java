package com.mycompany.presentation.gamehistory;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;
import com.mycompany.model.app.Game;
import com.mycompany.model.app.Player;
import com.mycompany.model.utils.GameStatus;
import com.mycompany.presentation.networkgame.GameRecorder;
import com.mycompany.presentation.replay.ReplayManager;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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

public class GameHistoryController implements Initializable, GameHistoryListener {

    @FXML
    private VBox gamesListContainer;
    @FXML
    private Label lblEmptyState, lblTotalGames, lblWins, lblLosses, lblDraws;
    @FXML
    private Button btnFilterAll, btnFilterWin, btnFilterLoss, btnFilterDraw, btnToggleRecorded;

    private List<Game> gameHistoryList = new ArrayList<>();
    private GameHistoryManager manager;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    private enum ResultFilter {
        ALL, WIN, LOSS, DRAW
    }

    private ResultFilter currentResultFilter = ResultFilter.ALL;
    private boolean showOnlyRecorded = false;
    private java.util.Map<Integer, GameRecorder.RecordedGame> localRecordingsMap = new java.util.HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // LINKING: Initialize Manager and attach this Controller as the Listener
        this.manager = new GameHistoryManager();
        this.manager.setListener(this);

        // Initial UI State
        updateFilterButtonStates();

        // Trigger data load (Local Only)
        loadLocalGames();
    }

    // Implementation of GameHistoryListener (Unused for local mostly)
    @Override
    public void onDataLoaded(List<Game> games) {
        this.gameHistoryList = games;
        displayGameHistory();
        updateStats();
    }

    @Override
    public void onError(String message) {
        System.err.println("Network Error: " + message);
    }

    private void displayGameHistory() {
        gamesListContainer.getChildren().clear();
        Player currentPlayer = manager.getCurrentPlayer();

        List<Game> filteredGames = gameHistoryList.stream()
                .filter(game -> {
                    boolean isP1 = game.getPlayer1().getId() == currentPlayer.getId();
                    boolean won = (isP1 && game.getStatus() == GameStatus.WIN) ||
                            (!isP1 && game.getStatus() == GameStatus.LOSE);
                    boolean isDraw = game.getStatus() == GameStatus.DRAW;

                    switch (currentResultFilter) {
                        case WIN:
                            return won && !isDraw;
                        case LOSS:
                            return !won && !isDraw;
                        case DRAW:
                            return isDraw;
                        default:
                            return true;
                    }
                })
                .filter(game -> !showOnlyRecorded || game.isIsRecorded())
                .collect(Collectors.toList());

        if (filteredGames.isEmpty()) {
            lblEmptyState.setVisible(true);
            lblEmptyState.setManaged(true);
            lblEmptyState.setText(gameHistoryList.isEmpty() ? "No games played yet" : "No matches found");
        } else {
            lblEmptyState.setVisible(false);
            lblEmptyState.setManaged(false);
            for (Game game : filteredGames) {
                gamesListContainer.getChildren().add(createGameCard(game));
            }
        }
    }

    private VBox createGameCard(Game game) {
        Player currentPlayer = manager.getCurrentPlayer();
        boolean isP1 = game.getPlayer1().getId() == currentPlayer.getId();
        Player opponent = isP1 ? game.getPlayer2() : game.getPlayer1();

        VBox card = new VBox(12);
        card.setStyle("-fx-padding: 16; -fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 12;");

        // Logic for Badge
        String resultText = "LOSS";
        String resultColor = "#e94560";
        if (game.getStatus() == GameStatus.DRAW) {
            resultText = "DRAW";
            resultColor = "#9ca3af";
        } else if ((isP1 && game.getStatus() == GameStatus.WIN) || (!isP1 && game.getStatus() == GameStatus.LOSE)) {
            resultText = "WIN";
            resultColor = "#00d2ff";
        }

        // Top Row: Date and Badge
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label dateLbl = new Label(dateFormatter.format(game.getDate()));
        dateLbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(resultText);
        badge.setStyle("-fx-background-color: " + resultColor
                + "; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 20;");
        topRow.getChildren().addAll(dateLbl, spacer, badge);

        // Bottom Row: Opponent and Replay
        HBox bottomRow = new HBox(12);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label oppLbl = new Label("VS " + opponent.getUserName());
        oppLbl.setStyle("-fx-text-fill: white;");
        bottomRow.getChildren().add(oppLbl);

        if (game.isIsRecorded()) {
            // Make card clickable
            card.setStyle(
                    "-fx-padding: 16; -fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 12; -fx-cursor: hand;");
            card.setOnMouseClicked(e -> onWatchReplay(game));

            // Add icon or label?
            Label recLbl = new Label("▶ Watch Replay");
            recLbl.setStyle("-fx-text-fill: #00FFFF; -fx-font-size: 10px;");
            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            bottomRow.getChildren().addAll(spacer2, recLbl);
        } else {
            card.setStyle(
                    "-fx-padding: 16; -fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 12;");
        }

        card.getChildren().addAll(topRow, bottomRow);
        return card;
    }

    private void updateStats() {
        Player p = manager.getCurrentPlayer();
        long total = gameHistoryList.size();
        long wins = gameHistoryList.stream().filter(g -> {
            boolean isP1 = g.getPlayer1().getId() == p.getId();
            return (isP1 && g.getStatus() == GameStatus.WIN) || (!isP1 && g.getStatus() == GameStatus.LOSE);
        }).count();
        long draws = gameHistoryList.stream().filter(g -> g.getStatus() == GameStatus.DRAW).count();

        lblTotalGames.setText(String.valueOf(total));
        lblWins.setText(String.valueOf(wins));
        lblDraws.setText(String.valueOf(draws));
        lblLosses.setText(String.valueOf(total - wins - draws));
    }

    @FXML
    private void onFilterAll() {
        currentResultFilter = ResultFilter.ALL;
        updateUI();
    }

    @FXML
    private void onFilterWin() {
        currentResultFilter = ResultFilter.WIN;
        updateUI();
    }

    @FXML
    private void onFilterLoss() {
        currentResultFilter = ResultFilter.LOSS;
        updateUI();
    }

    @FXML
    private void onFilterDraw() {
        currentResultFilter = ResultFilter.DRAW;
        updateUI();
    }

    @FXML
    private void onToggleRecorded() {
        showOnlyRecorded = !showOnlyRecorded;
        updateUI();

        if (showOnlyRecorded) {
            loadLocalGames();
        } else {
            manager.loadGameHistory(); // Reload server history
        }
    }

    private void loadLocalGames() {
        localRecordingsMap.clear();
        gameHistoryList.clear(); // Clear server games for this view
        List<GameRecorder.RecordedGame> recs = GameRecorder.getInstance().listRecordings();

        int tempId = -1;
        for (GameRecorder.RecordedGame rg : recs) {
            Game g = new Game();
            g.setId(tempId);
            g.setDate(new java.util.Date(rg.date));
            g.setIsRecorded(true);

            Player p1 = new Player();
            p1.setUserName(rg.player1);
            p1.setId(100); // Mock
            Player p2 = new Player();
            p2.setUserName(rg.player2);
            p2.setId(101); // Mock
            g.setPlayer1(p1);
            g.setPlayer2(p2);

            // Determine status for "Current Player" perspective?
            // "Current Player" in app context is manager.getCurrentPlayer().
            // Ideally we should match names.
            Player me = manager.getCurrentPlayer();
            boolean amIP1 = me.getUserName().equals(rg.player1);
            boolean amIP2 = me.getUserName().equals(rg.player2);

            if (rg.winner == null || rg.winner.equals("DRAW")) {
                g.setStatus(GameStatus.DRAW);
            } else if ((amIP1 && rg.winner.equals("WIN")) || (amIP2 && rg.winner.equals("LOSE"))) {
                // Heuristic: winner string in file is simple "WIN"/"LOSE" relative to... whom?
                // In GameRecorder.saveGame(winner, status), we passed 'winnerSymbol' name, and
                // status.
                // Wait, saveGame(String winner, String status)
                // calls: saveGame(session.getMyName(), "WIN")
                // So 'winner' field in JSON is the NAME of the winner.
                g.setStatus(rg.winner.equals(me.getUserName()) ? GameStatus.WIN : GameStatus.LOSE);
            } else {
                g.setStatus(GameStatus.LOSE); // Default fallback
            }

            localRecordingsMap.put(tempId, rg);
            gameHistoryList.add(g);
            tempId--;
        }
        displayGameHistory();
        updateStats(); // Update stats based on loaded recordings?
    }

    private void updateUI() {
        updateFilterButtonStates();
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
        btn.setStyle(selected ? "-fx-border-color: #00FFFF; -fx-background-color: rgba(0, 255, 255, 0.2);" : "");
    }

    @FXML
    private void onBack() {
        manager.detach(); // Important to clean up listeners
        try {
            App.setRoot(Routes.LOBBY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onWatchReplay(Game game) {
        GameRecorder.RecordedGame rg = localRecordingsMap.get(game.getId());
        if (rg != null) {
            ReplayManager.getInstance().setGameToReplay(rg);
            try {
                App.setRoot(Routes.REPLAY_GAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Replay file not found locally.").show();
        }
    }
}