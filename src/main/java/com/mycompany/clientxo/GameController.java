package com.mycompany.clientxo;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameController {

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
    private Button playAgainButton;
    @FXML
    private Button homeButton; // New Button for navigation

    private Button[] buttons = new Button[9];
    private String[] board = new String[9];
    private boolean isGameActive = true;
    private String currentPlayer = "X"; // X is usually the human
    private int scoreXVal = 0;
    private int scoreOVal = 0;
    private Random random = new Random();

    private final int[][] WINNING_COMBINATIONS = {
            { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, // Rows
            { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, // Cols
            { 0, 4, 8 }, { 2, 4, 6 } // Diagonals
    };

    // SVG Paths
    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    @FXML
    public void initialize() {
        // Init Players based on Mode
        GameState.GameMode mode = GameState.getInstance().getCurrentMode();
        if (mode == GameState.GameMode.SINGLE_PLAYER) {
            playerXName.setText("You");
            playerOName.setText("Computer (" + GameState.getInstance().getCurrentDifficulty() + ")");
        } else if (mode == GameState.GameMode.LOCAL_MULTIPLAYER) {
            playerXName.setText("Player X");
            playerOName.setText("Player O");
        } else if (mode == GameState.GameMode.NETWORK_MULTIPLAYER) {
            playerXName.setText(GameState.getInstance().getUsername());
            playerOName.setText("Opponent"); // In real app, pass opponent name
        }

        int i = 0;
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                // Assign index if not already set (fallback) or rely on fxml order/userData
                if (btn.getUserData() != null) {
                    try {
                        int index = Integer.parseInt((String) btn.getUserData());
                        buttons[index] = btn;
                    } catch (NumberFormatException e) {
                        buttons[i] = btn; // Fallback
                    }
                } else {
                    buttons[i] = btn;
                }
                i++;
            }
        }
        resetGame();
    }

    @FXML
    private void handleGridClick(ActionEvent event) {
        if (!isGameActive)
            return;

        // In Network mode, check turn logic properly (omitted for local sim)

        Button clickedButton = (Button) event.getSource();
        int index = Integer.parseInt((String) clickedButton.getUserData());

        if (board[index] == null) {
            // Human move (X)
            makeMove(index, currentPlayer);

            GameState.GameMode mode = GameState.getInstance().getCurrentMode();

            if (isGameActive) {
                if (mode == GameState.GameMode.SINGLE_PLAYER && currentPlayer.equals("O")) { // Computer's turn after
                                                                                             // human X
                    // Computer Turn
                    statusText.setText("Computer is thinking...");
                    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                    pause.setOnFinished(e -> makeComputerMove());
                    pause.play();
                } else if (mode == GameState.GameMode.LOCAL_MULTIPLAYER) {
                    // Switch turn is handled in makeMove, just update text
                    // already updated in makeMove
                }
            }
        }
    }

    private void makeMove(int index, String player) {
        board[index] = player;

        // Create SVG Icon
        SVGPath icon = new SVGPath();
        icon.setContent(player.equals("X") ? PATH_X : PATH_O);
        icon.getStyleClass().add(player.equals("X") ? "icon-x" : "icon-o");

        buttons[index].setGraphic(icon);
        buttons[index].setText(""); // Ensure no text
        buttons[index].getStyleClass().add(player.equals("X") ? "cell-x-container" : "cell-o-container");

        animateButton(buttons[index]);

        checkGameState();

        if (isGameActive) {
            if (player.equals("X")) {
                currentPlayer = "O";
                statusText.setText(playerOName.getText() + "'s Turn");
            } else {
                currentPlayer = "X";
                statusText.setText(playerXName.getText() + "'s Turn");
            }
        }
    }

    private void makeComputerMove() {
        if (!isGameActive)
            return;

        // Simple AI: Random available spot
        // In real app, use Minimax for HARD difficulty based on GameState
        List<Integer> available = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i] == null) {
                available.add(i);
            }
        }

        if (!available.isEmpty()) {
            int move = available.get(random.nextInt(available.size()));
            makeMove(move, "O");
        }
    }

    private void checkGameState() {
        // Check winner
        for (int[] line : WINNING_COMBINATIONS) {
            String a = board[line[0]];
            String b = board[line[1]];
            String c = board[line[2]];

            if (a != null && a.equals(b) && b.equals(c)) {
                endGame(a, line);
                return;
            }
        }

        // Check draw
        boolean isDraw = true;
        for (String s : board) {
            if (s == null) {
                isDraw = false;
                break;
            }
        }

        if (isDraw) {
            isGameActive = false;
            statusText.setText("It's a Draw!");
            showEndGameOptions();
        }
    }

    private void endGame(String winner, int[] line) {
        isGameActive = false;
        highlightWinningLine(line);

        if (winner.equals("X")) {
            statusText.setText(playerXName.getText() + " Wins!");
            scoreXVal++;
            scoreX.setText(String.valueOf(scoreXVal));
        } else {
            statusText.setText(playerOName.getText() + " Wins!");
            scoreOVal++;
            scoreO.setText(String.valueOf(scoreOVal));
        }
        showEndGameOptions();
    }

    private void showEndGameOptions() {
        playAgainButton.setVisible(true);
        playAgainButton.setDisable(false);
        playAgainButton.setText("PLAY AGAIN");

        GameState.GameMode mode = GameState.getInstance().getCurrentMode();

        if (mode == GameState.GameMode.NETWORK_MULTIPLAYER) {
            // For Network, Show Play Again (Request Rematch) AND Home (Back to Lobby)
            homeButton.setVisible(true);
            homeButton.setText("BACK TO LOBBY");
        } else {
            // Local/Single
            homeButton.setVisible(true);
            homeButton.setText("HOME");
        }

        animateButton(playAgainButton);
    }

    @FXML
    private void resetGame(ActionEvent event) {
        resetGame();
    }

    public void resetGame() {
        GameState.GameMode mode = GameState.getInstance().getCurrentMode();

        if (mode == GameState.GameMode.NETWORK_MULTIPLAYER) {
            // Send Rematch Request
            playAgainButton.setText("Requesting...");
            playAgainButton.setDisable(true);

            // Mock Network Response (Delay)
            new Thread(() -> {
                try {
                    Thread.sleep(1500); // Wait 1.5s
                    // 80% chance opponent accepts
                    if (Math.random() > 0.2) {
                        Platform.runLater(() -> {
                            startNewGame();
                        });
                    } else {
                        Platform.runLater(() -> {
                            playAgainButton.setText("Opponent Left");
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            // Local / Single
            startNewGame();
        }
    }

    private void startNewGame() {
        isGameActive = true;
        currentPlayer = "X";
        Arrays.fill(board, null);

        GameState.GameMode mode = GameState.getInstance().getCurrentMode();
        if (mode == GameState.GameMode.SINGLE_PLAYER) {
            statusText.setText("Your Turn");
        } else {
            statusText.setText(playerXName.getText() + "'s Turn");
        }

        playAgainButton.setVisible(false);
        if (homeButton != null)
            homeButton.setVisible(false);

        for (Button btn : buttons) {
            if (btn != null) {
                btn.setText("");
                btn.setGraphic(null);
                btn.getStyleClass().removeAll("cell-x-container", "cell-o-container", "winning-cell");
            }
        }
    }

    private void highlightWinningLine(int[] line) {
        for (int index : line) {
            if (buttons[index] != null) {
                buttons[index].getStyleClass().add("winning-cell");
                animateButton(buttons[index]);
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            if (GameState.getInstance().getCurrentMode() == GameState.GameMode.NETWORK_MULTIPLAYER) {
                App.setRoot("LobbyScreen");
            } else {
                App.setRoot("primary");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHome(ActionEvent event) {
        try {
            App.setRoot("primary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateButton(Button btn) {
        if (btn.getGraphic() != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(300), btn.getGraphic());
            st.setFromX(0.1);
            st.setFromY(0.1);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        }
    }
}
