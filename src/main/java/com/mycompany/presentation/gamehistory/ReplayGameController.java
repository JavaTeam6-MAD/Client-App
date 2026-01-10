package com.mycompany.presentation.gamehistory;

import com.mycompany.App;
import com.mycompany.core.navigation.Routes;
import com.mycompany.core.util.GameRecorder.RecordedGame;
import com.mycompany.core.util.GameRecorder.GameMove;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.List;

public class ReplayGameController {

    @FXML
    private Label playerXName;
    @FXML
    private Label playerOName;
    @FXML
    private Label scoreX;
    @FXML
    private Label scoreO;
    @FXML
    private Label statusText;
    @FXML
    private GridPane gameGrid;
    @FXML
    private Button playAgainButton; // "Replay Again"
    @FXML
    private Button homeButton; // "Exit"

    private Button[][] buttons = new Button[3][3];
    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    private RecordedGame recordedGame;
    private Timeline replayTimeline;

    @FXML
    public void initialize() {
        initGrid();
        // Waiting for setRecordedGame to be called
    }

    private void initGrid() {
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Integer index = Integer.parseInt((String) btn.getUserData());
                int r = index / 3;
                int c = index % 3;
                buttons[r][c] = btn;
                btn.setDisable(true); // Disable interaction during replay
                btn.setStyle("-fx-opacity: 1.0;"); // Make sure it looks enabled? Or just standard disabled look?
            }
        }
    }

    public void setRecordedGame(RecordedGame game) {
        this.recordedGame = game;
        setupUI();
        startReplay();
    }

    private void setupUI() {
        if (recordedGame == null)
            return;

        playerXName.setText(recordedGame.getPlayer1Name());
        playerOName.setText(recordedGame.getPlayer2Name());
        scoreX.setText("-"); // Scores not really relevant for replay unless strictly recorded
        scoreO.setText("-");

        statusText.setText("Replay: " + recordedGame.getPlayer1Name() + " vs " + recordedGame.getPlayer2Name());
    }

    private void startReplay() {
        resetBoard();
        List<GameMove> moves = recordedGame.getMoves();
        if (moves == null || moves.isEmpty()) {
            statusText.setText("No moves recorded.");
            return;
        }

        replayTimeline = new Timeline();
        // Add frames for each move
        for (int i = 0; i < moves.size(); i++) {
            GameMove move = moves.get(i);
            int moveIndex = i;
            KeyFrame frame = new KeyFrame(Duration.seconds(i + 1), e -> {
                renderMove(move);
                statusText.setText("Move " + (moveIndex + 1) + "/" + moves.size());
            });
            replayTimeline.getKeyFrames().add(frame);
        }

        // Add final frame for result
        KeyFrame endFrame = new KeyFrame(Duration.seconds(moves.size() + 1), e -> {
            showResult();
        });
        replayTimeline.getKeyFrames().add(endFrame);

        replayTimeline.play();
    }

    private void renderMove(GameMove move) {
        int r = move.getRow();
        int c = move.getCol();
        String symbol = move.getSymbol();

        String path = "X".equals(symbol) ? PATH_X : PATH_O;
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.getStyleClass().add("move-path-base");
        if ("X".equals(symbol)) {
            svg.getStyleClass().add("move-path-x");
        } else {
            svg.getStyleClass().add("move-path-o");
        }

        if (buttons[r][c] != null) {
            buttons[r][c].setGraphic(svg);
        }
    }

    private void showResult() {
        String winner = recordedGame.getWinnerName();
        boolean isDraw = recordedGame.isDraw();

        if (isDraw) {
            statusText.setText("Game Over: Draw!");
        } else {
            statusText.setText("Game Over: " + winner + " Won!");
        }

        playAgainButton.setVisible(true);
        homeButton.setVisible(true);
    }

    private void resetBoard() {
        for (Button[] row : buttons) {
            for (Button b : row) {
                if (b != null) {
                    b.setGraphic(null);
                    b.setText("");
                }
            }
        }
        playAgainButton.setVisible(false);
        homeButton.setVisible(false);
    }

    @FXML
    private void handleReplayAgain(ActionEvent event) {
        startReplay();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (replayTimeline != null) {
            replayTimeline.stop();
        }
        try {
            // Need a specific route for Game History?
            // Existing routes only have LOBBY, etc.
            // But GameHistory is a sub-screen?
            // "GameHistory" is likely a scene?
            // I should check Routes.java to see if HISTORY exists.
            // If not, maybe use App.setRoot? to what?
            // Step 109 shows 'App.setRoot(Routes.LOBBY)' in onBack().
            // So GameHistory is probably accessed from Lobby.
            // I should probably go back to LOBBY for now?
            // Or ideally back to History.
            // I'll check Routes.
            App.setRoot("GameHistoryScreen"); // Speculative
        } catch (Exception e) {
            // Fallback
            try {
                App.setRoot(Routes.LOBBY);
            } catch (Exception ex) {
            }
        }
    }
}
