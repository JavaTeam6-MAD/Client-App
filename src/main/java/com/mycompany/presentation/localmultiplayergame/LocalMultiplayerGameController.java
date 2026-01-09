package com.mycompany.presentation.localmultiplayergame;

import com.mycompany.core.navigation.Routes;
import com.mycompany.App;
import com.mycompany.core.utils.SoundManager;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Duration;

public class LocalMultiplayerGameController {

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

    private Stage videoStage;
    private MediaPlayer mediaPlayer;

    // SVG Paths
    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    // Sound Manager instance
    private SoundManager soundManager;

    private LocalGameManager game = new LocalGameManager();

    public void setPlayerNames(String nameX, String nameO) {
        playerXName.setText(nameX.isEmpty() ? "Player X" : nameX);
        playerOName.setText(nameO.isEmpty() ? "Player O" : nameO);
    }

    private void setEndGameButtonsVisible(boolean visible) {
        playAgainButton.setVisible(visible);
        homeButton.setVisible(visible);
    }

    @FXML
    public void initialize() {
        // Initialize sound manager
        soundManager = SoundManager.getInstance();

        // just to make sure that the buttons is inVisible
        setEndGameButtonsVisible(false);
    }

    @FXML
    private void handleGridClick(ActionEvent event) {
        Button button = (Button) event.getSource();

        Integer r = GridPane.getRowIndex(button);
        Integer c = GridPane.getColumnIndex(button);
        int row, col;
        if (r == null) {
            row = 0;
        } else {
            row = r;
        }
        if (c == null) {
            col = 0;
        } else {
            col = c;
        }

        if (game.makeMove(row, col)) {
            // Play click sound
            if (soundManager != null) {
                soundManager.playSound(SoundManager.PLACE_X);
            }
            char currentPlayer = game.getCurrentPlayer();
            drawSymbol(button, currentPlayer);

            if (game.checkWinner()) {
                game.setGameActive(false);
                game.incrementScore();
                updateUI();
                statusText.setText("Winner: " + (currentPlayer == 'X' ? playerXName.getText() : playerOName.getText()));
                setEndGameButtonsVisible(true);
                playWinVideo();
            } else if (game.isBoardFull()) {
                game.setGameActive(false);
                statusText.setText("It's a Draw!");
                setEndGameButtonsVisible(true);
            } else {
                game.nextTurn();
                statusText.setText(
                        "Turn: " + (game.getCurrentPlayer() == 'X' ? playerXName.getText() : playerOName.getText()));
            }
        }
    }

    private void drawSymbol(Button button, char symbol) {
        SVGPath path = new SVGPath();
        path.setContent(symbol == 'X' ? PATH_X : PATH_O);
        path.getStyleClass().add(symbol == 'X' ? "icon-x" : "icon-o");
        button.setGraphic(path);
    }

    private void updateUI() {
        scoreX.setText(String.valueOf(game.getScoreX()));
        scoreO.setText(String.valueOf(game.getScoreO()));
    }

    @FXML
    private void resetGame(ActionEvent event) {
        // Play button click sound
        if (soundManager != null) {
            soundManager.playSound(SoundManager.BUTTON_CLICK);
        }

        stopWinVideo();
        game.resetGame();
        statusText.setText("Turn: " + playerXName.getText());
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setGraphic(null);
            }
        }
        setEndGameButtonsVisible(false);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Play button click sound
        if (soundManager != null) {
            soundManager.playSound(SoundManager.BUTTON_CLICK);
        }

        stopWinVideo();

        // Show confirmation dialog with custom styling
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave Game?");
        alert.setHeaderText("Are you sure you want to go back?");
        alert.setContentText("The current game will end and you will lose this match.");

        // Apply custom cyberpunk theme styling
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/mycompany/styles.css").toExternalForm());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    App.setRoot(Routes.HOME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // If Cancel, do nothing - stay on game screen
        });
    }

    @FXML
    private void onHome(ActionEvent event) {
        // Play button click sound
        if (soundManager != null) {
            soundManager.playSound(SoundManager.BUTTON_CLICK);
        }

        stopWinVideo();
        try {
            App.setRoot(Routes.HOME);
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

    private void playWinVideo() {
        try {
            stopWinVideo(); // Ensure any previous video/stage is closed

            String path = getClass().getResource("/com/mycompany/winVideo/win.mp4").toExternalForm();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);

            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(800);
            mediaView.setFitHeight(600);
            mediaView.setPreserveRatio(true);

            StackPane root = new StackPane(mediaView);
            // Optional: Black background
            root.setStyle("-fx-background-color: black;");

            Scene scene = new Scene(root, 800, 600);

            videoStage = new Stage();
            videoStage.setTitle("Winner!");
            videoStage.setScene(scene);
            videoStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with game window

            // Handle user closing the window manually
            videoStage.setOnHidden(e -> stopWinVideo());

            videoStage.show();

            mediaPlayer.setOnEndOfMedia(() -> {
                stopWinVideo();
            });
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Win video not found or could not be played: " + e.getMessage());
        }
    }

    private void stopWinVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        if (videoStage != null) {
            if (videoStage.isShowing()) {
                videoStage.close();
            }
            videoStage = null;
        }
    }
}
