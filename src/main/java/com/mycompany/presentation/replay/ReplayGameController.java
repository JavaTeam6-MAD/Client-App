package com.mycompany.presentation.replay;

import com.mycompany.App;
import com.mycompany.core.navigation.Routes;
import com.mycompany.presentation.networkgame.GameRecorder;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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

    private Button[][] buttons = new Button[3][3];
    private GameRecorder.RecordedGame game;
    private Timer replayTimer;
    private Button lastMoveButton = null;

    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    @FXML
    public void initialize() {
        game = ReplayManager.getInstance().getGameToReplay();
        if (game == null) {
            statusText.setText("No replay loaded.");
            return;
        }

        initGridButtons();

        // Setup Player Names
        playerXName.setText(game.player1);
        playerOName.setText(game.player2);

        // Show symbols in score labels
        scoreX.setText("X");
        scoreO.setText("O");

        startReplay();
    }

    private void initGridButtons() {
        // Use existing grid from FXML
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Integer index = Integer.parseInt((String) btn.getUserData());
                int r = index / 3;
                int c = index % 3;
                buttons[r][c] = btn;
            }
        }
    }

    private void startReplay() {
        statusText.setText("Replaying game...");
        replayTimer = new Timer();
        final int[] moveIndex = { 0 };

        replayTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (game == null || moveIndex[0] >= game.moves.size()) {
                    Platform.runLater(() -> finishReplay());
                    cancel();
                    return;
                }

                GameRecorder.Move move = game.moves.get(moveIndex[0]);
                Platform.runLater(() -> playMove(move));
                moveIndex[0]++;
            }
        }, 1000, 2000); // 2 second delay between moves
    }

    private void playMove(GameRecorder.Move move) {
        Button btn = buttons[move.r][move.c];

        // Remove highlight from previous move
        if (lastMoveButton != null) {
            lastMoveButton.setStyle("");
        }

        // Create and add the symbol
        SVGPath svg = new SVGPath();
        svg.setContent(move.s.equals("X") ? PATH_X : PATH_O);
        svg.getStyleClass().add("move-path-base");
        svg.getStyleClass().add(move.s.equals("X") ? "move-path-x" : "move-path-o");

        btn.setGraphic(svg);

        // Highlight this move with a glow effect
        String highlightColor = move.s.equals("X") ? "#00FFFF" : "#FF1493";
        btn.setStyle("-fx-background-color: rgba(" +
                (move.s.equals("X") ? "0, 255, 255" : "255, 20, 147") +
                ", 0.3); -fx-border-color: " + highlightColor +
                "; -fx-border-width: 3px;");

        // Add scale animation to make the move pop
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), btn);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.play();

        // Pulse the symbol
        FadeTransition fade = new FadeTransition(Duration.millis(300), svg);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.play();

        lastMoveButton = btn;

        // Show whose turn it is
        String playerName = move.s.equals("X") ? game.player1 : game.player2;
        statusText.setText("▶ " + playerName + " (" + move.s + ") played at [" +
                (move.r + 1) + "," + (move.c + 1) + "]");
    }

    private void finishReplay() {
        if (replayTimer != null)
            replayTimer.cancel();

        // Remove last move highlight
        if (lastMoveButton != null) {
            lastMoveButton.setStyle("");
        }

        String resultMsg;
        if (game.winner == null || game.winner.equals("DRAW")) {
            resultMsg = "🤝 Game ended in a DRAW";
        } else {
            resultMsg = "🎉 " + game.winner + " Won the game!";
        }
        statusText.setText(resultMsg);
    }

    @FXML
    private void handleBack() {
        if (replayTimer != null) {
            replayTimer.cancel();
        }
        try {
            App.setRoot(Routes.GAME_HISTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
