package com.mycompany.presentation.networkgame;

import com.mycompany.App;
import com.mycompany.core.navigation.Routes;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.util.Optional;

public class NetworkGameController { // No Interface!

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
    private Button homeButton;
    @FXML
    private javafx.scene.layout.StackPane recordingIndicator;

    private NetworkGameManager manager;

    private Button[][] buttons = new Button[3][3];

    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    @FXML
    public void initialize() {
        manager = new NetworkGameManager();
        manager.setController(this);

        // Init UI
        if ("X".equals(manager.getMySymbol())) {
            playerXName.setText("You");
            playerOName.setText(manager.getOpponentName());
            scoreX.setText(String.valueOf(manager.getMySessionScore())); // Session Score
            scoreO.setText(String.valueOf(manager.getOpponentSessionScore()));
        } else {
            playerXName.setText(manager.getOpponentName());
            playerOName.setText("You");
            scoreO.setText(String.valueOf(manager.getMySessionScore()));
            scoreX.setText(String.valueOf(manager.getOpponentSessionScore()));
        }

        updateStatus();
        initGrid();

        if (manager.isRecording() && recordingIndicator != null) {
            recordingIndicator.setVisible(true);
            recordingIndicator.setManaged(true);
        }
    }

    private void initGrid() {
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

    private void updateStatus() {
        // Initial setup
        updateTurnStatus(manager.isMyTurn(), manager.getMySymbol());
    }

    public void updateTurnStatus(boolean isMyTurn, String symbol) {
        javafx.application.Platform.runLater(() -> {
            if (isMyTurn) {
                statusText.setText("Your Turn (" + symbol + ")");
            } else {
                statusText.setText("Opponent's Turn");
            }
        });
    }

    @FXML
    private void handleGridClick(ActionEvent event) {
        // Manager checks turn and logic
        Button clickedBtn = (Button) event.getSource();
        if (!clickedBtn.getText().isEmpty() || clickedBtn.getGraphic() != null) {
            return;
        }

        Integer index = Integer.parseInt((String) clickedBtn.getUserData());
        int r = index / 3;
        int c = index % 3;

        manager.makeMove(r, c);
    }

    public void updateBoard(int r, int c, String symbol) {
        Platform.runLater(() -> {
            String path = symbol.equals("X") ? PATH_X : PATH_O;
            javafx.scene.shape.SVGPath svg = new javafx.scene.shape.SVGPath();
            svg.setContent(path);
            svg.getStyleClass().add("move-path-base");
            if (symbol.equals("X")) {
                svg.getStyleClass().add("move-path-x");
            } else {
                svg.getStyleClass().add("move-path-o");
            }

            buttons[r][c].setGraphic(svg);
            buttons[r][c].setText("");
        });
    }


    public void showGameEnd(String msg, boolean isWin) {
        Platform.runLater(() -> {
            gameGrid.setDisable(true);
            statusText.setText(msg);

            // Play victory video if player won
            if (isWin) {
                playVictoryVideo(() -> showGameEndAlert(msg));
            } else {
                showGameEndAlert(msg);
            }
        });
    }

    private void showGameEndAlert(String msg) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        if (getClass().getResource("/com/mycompany/styles.css") != null) {
            alert.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
        }
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (alert.isShowing())
                        alert.close();
                });
            }
        }, 5000);

        alert.show();

        playAgainButton.setVisible(true);
        homeButton.setVisible(true);

    }

    private void playVictoryVideo(Runnable onComplete) {
        try {
            // Try to load video from resources
            String videoPath = getClass().getResource("/com/mycompany/videos/victory.mp4") != null
                    ? getClass().getResource("/com/mycompany/videos/victory.mp4").toExternalForm()
                    : null;

            if (videoPath == null) {
                System.out.println("Victory video not found, skipping video playback");
                if (onComplete != null) onComplete.run();
                return;
            }

            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            // Get the root pane to add video overlay
            StackPane root = (StackPane) gameGrid.getScene().getRoot();

            // Create overlay container
            StackPane videoOverlay = new StackPane();
            videoOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
            videoOverlay.getChildren().add(mediaView);

            // Fit video to scene size
            mediaView.setFitWidth(root.getWidth());
            mediaView.setFitHeight(root.getHeight());
            mediaView.setPreserveRatio(true);

            // Add overlay to root
            root.getChildren().add(videoOverlay);

            // Play video
            mediaPlayer.setOnEndOfMedia(() -> {
                Platform.runLater(() -> {
                    mediaPlayer.dispose();
                    root.getChildren().remove(videoOverlay);
                    if (onComplete != null) onComplete.run();
                });
            });

            mediaPlayer.setOnError(() -> {
                System.err.println("Error playing victory video: " + mediaPlayer.getError());
                Platform.runLater(() -> {
                    mediaPlayer.dispose();
                    root.getChildren().remove(videoOverlay);
                    if (onComplete != null) onComplete.run();
                });
            });

            mediaPlayer.play();

        } catch (Exception e) {
            System.err.println("Failed to play victory video: " + e.getMessage());
            e.printStackTrace();
            if (onComplete != null) onComplete.run();
        }
    }

    public void updateScoreLabels(long myScore, long opponentScore) {
        Platform.runLater(() -> {
            if ("X".equals(manager.getMySymbol())) {
                scoreX.setText(String.valueOf(myScore));
                scoreO.setText(String.valueOf(opponentScore));
            } else {
                scoreO.setText(String.valueOf(myScore));
                scoreX.setText(String.valueOf(opponentScore));
            }
        });
    }

    public void showRematchRequest(String name, int challengerId) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rematch Request");
            alert.setHeaderText("Opponent wants a rematch!");
            alert.setContentText("Do you want to play again?");

            ButtonType acceptBtn = new ButtonType("Accept", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            ButtonType rejectBtn = new ButtonType("Reject", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(acceptBtn, rejectBtn);

            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                alert.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            alert.getDialogPane().getStyleClass().add("dialog-pane");

            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (alert.isShowing()) {
                            alert.close();
                        }
                    });
                }
            }, 10000);

            Optional<ButtonType> result = alert.showAndWait();

            boolean accepted = (result.isPresent() && result.get() == acceptBtn);
            manager.handleRematchResponse(accepted, challengerId);
        });
    }

    public void resetGameUI() {
        Platform.runLater(this::resetInternal);
    }

    public void showRematchRejected() {
        Platform.runLater(() -> {
            statusText.setText("Opponent rejected rematch.");
            playAgainButton.setDisable(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rejected");
            alert.setHeaderText(null);
            alert.setContentText("Opponent rejected rematch.");
            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                alert.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            alert.getDialogPane().getStyleClass().add("dialog-pane");
            alert.showAndWait();

            navigateHome();
        });
    }

    public void showOpponentForfeit() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Opponent Disconnected");
            alert.setContentText("Your opponent has disconnected. You Won! (+30 XP)");
            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                alert.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            alert.getDialogPane().getStyleClass().add("dialog-pane");
            alert.showAndWait();

            navigateHome();
        });
    }

    public void showConnectionError(String errorMessage) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(
                    Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Connection Lost");
            alert.setContentText(errorMessage != null ? errorMessage : "Connection to server lost.");

            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                alert.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            alert.getDialogPane().getStyleClass().add("dialog-pane");

            alert.showAndWait();

            navigateHome();
        });
    }

    @FXML
    private void onHome(ActionEvent event) {
        manager.stopGame();
        navigateHome();
    }

    public void navigateHome() {
        try {
            App.setRoot(Routes.LOBBY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Seamless Forfeit via Manager (no alert)
        manager.forfeitGame();
    }

    @FXML
    private void resetGame(ActionEvent event) {
        manager.requestRematch();
        statusText.setText("Requesting Rematch...");
        playAgainButton.setDisable(true);

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (playAgainButton.isDisabled()) {
                        playAgainButton.setDisable(false);
                        statusText.setText("Rematch timed out.");
                    }
                });
            }
        }, 15000);
    }

    private void resetInternal() {
        gameGrid.setDisable(false);
        for (Button[] row : buttons) {
            for (Button b : row) {
                if (b != null) {
                    b.setText("");
                    b.setGraphic(null);
                    // b.getStyleClass().removeAll("x-cell", "o-cell");
                }
            }
        }
        playAgainButton.setVisible(false);
        homeButton.setVisible(false);
        updateStatus();
    }
}
