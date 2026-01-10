package com.mycompany.presentation.singlegamescreen;

import com.mycompany.App;
import com.mycompany.core.utils.SoundManager;
import javafx.animation.PauseTransition;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.io.IOException;

public class ComputerGameController {

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
    private Stage loseVideoStage;
    private MediaPlayer loseMediaPlayer;

    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    // Static field to receive difficulty from previous screen
    public static int difficulty = 3;

    private boolean isPlayerTurn = true;
    private ComputerGameManager gameManager;
    private SoundManager soundManager;

    @FXML
    public void initialize() {
        soundManager = SoundManager.getInstance();
        gameManager = new ComputerGameManager();
        gameManager.setDifficulty(difficulty);

        statusText.setText("Your Turn (X)");
        setEndGameButtonsVisible(false);
        initGrid();
        updateScoreBoard();
        isPlayerTurn = true;
    }

    private void initGrid() {
        int index = 0;
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setUserData(index);
                // Ensure no graphic is set initially
                btn.setGraphic(null);
                index++;
            }
        }
    }

    private void setEndGameButtonsVisible(boolean visible) {
        playAgainButton.setVisible(visible);
        homeButton.setVisible(visible);
    }

    @FXML
    private void handleGridClick(ActionEvent event) {
        if (!gameManager.isGameActive() || !isPlayerTurn)
            return;

        Button btn = (Button) event.getSource();
        int index = (int) btn.getUserData();

        if (gameManager.mapPlayerMove(index)) {
            // Player Move UI
            if (soundManager != null)
                soundManager.playSound(SoundManager.PLACE_X);
            drawSymbol(btn, 'X');

            if (checkGameOver('X'))
                return;

            // Computer Turn
            statusText.setText("Computer's Turn...");
            isPlayerTurn = false;

            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
            pause.setOnFinished(e -> {
                int aiMoveIndex = gameManager.makeComputerMove();
                if (aiMoveIndex != -1) {
                    Button aiBtn = (Button) gameGrid.getChildren().get(aiMoveIndex); // Assuming order match
                    if (soundManager != null)
                        soundManager.playSound(SoundManager.PLACE_O);
                    drawSymbol(aiBtn, 'O');

                    if (checkGameOver('O'))
                        return;
                }

                statusText.setText("Your Turn (X)");
                isPlayerTurn = true;
            });
            pause.play();
        }
    }

    private boolean checkGameOver(char player) {
        if (gameManager.checkWin(player)) {
            gameManager.setGameActive(false);
            if (player == 'X') {
                statusText.setText("You Win!");
                gameManager.incrementScoreX();
                if (soundManager != null)
                    soundManager.playSound(SoundManager.WIN);
                playWinVideo();
            } else {
                statusText.setText("Computer Wins!");
                gameManager.incrementScoreO();
                if (soundManager != null)
                    soundManager.playSound(SoundManager.LOSE);
                playLoseVideo();
            }
            updateScoreBoard();
            endGame();
            return true;
        } else if (gameManager.isDraw()) {
            gameManager.setGameActive(false);
            statusText.setText("Draw!");
            if (soundManager != null)
                soundManager.playSound(SoundManager.DRAW);
            endGame();
            return true;
        }
        return false;
    }

    private void drawSymbol(Button button, char symbol) {
        SVGPath path = new SVGPath();
        path.setContent(symbol == 'X' ? PATH_X : PATH_O);
        path.getStyleClass().add(symbol == 'X' ? "icon-x" : "icon-o");
        button.setGraphic(path);
    }

    private void updateScoreBoard() {
        scoreX.setText(String.valueOf(gameManager.getScoreX()));
        scoreO.setText(String.valueOf(gameManager.getScoreO()));
    }

    private void endGame() {
        setEndGameButtonsVisible(true);
        // We don't disable buttons visually to keep the glow, logic prevents clicks
    }

    @FXML
    private void resetGame(ActionEvent event) {
        if (soundManager != null)
            soundManager.playSound(SoundManager.BUTTON_CLICK);

        stopWinVideo();
        stopLoseVideo();
        gameManager.resetGame();
        statusText.setText("Your Turn (X)");
        setEndGameButtonsVisible(false);
        isPlayerTurn = true;

        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setGraphic(null);
                btn.setDisable(false);
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (soundManager != null)
            soundManager.playSound(SoundManager.BUTTON_CLICK);

        stopWinVideo();
        stopLoseVideo();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave Game?");
        alert.setHeaderText("Are you sure you want to go back?");
        alert.setContentText("The current game will end.");
        alert.getDialogPane().getStylesheets()
                .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    App.setRoot("primary");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void onHome(ActionEvent event) {
        if (soundManager != null)
            soundManager.playSound(SoundManager.BUTTON_CLICK);

        stopWinVideo();
        stopLoseVideo();
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playWinVideo() {
        try {
            stopWinVideo();

            String path = getClass().getResource("/com/mycompany/winVideo/win.mp4").toExternalForm();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);

            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(800);
            mediaView.setFitHeight(600);
            mediaView.setPreserveRatio(true);

            StackPane root = new StackPane(mediaView);
            root.setStyle("-fx-background-color: black;");

            Scene scene = new Scene(root, 800, 600);

            videoStage = new Stage();
            videoStage.setTitle("You Win!");
            videoStage.setScene(scene);
            videoStage.initModality(Modality.APPLICATION_MODAL);

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

    private void playLoseVideo() {
        try {
            stopLoseVideo();

            String path = getClass().getResource("/com/mycompany/loseVideo/defeat.mp4").toExternalForm();
            Media media = new Media(path);
            loseMediaPlayer = new MediaPlayer(media);

            MediaView mediaView = new MediaView(loseMediaPlayer);
            mediaView.setFitWidth(800);
            mediaView.setFitHeight(600);
            mediaView.setPreserveRatio(true);

            StackPane root = new StackPane(mediaView);
            root.setStyle("-fx-background-color: black;");

            Scene scene = new Scene(root, 800, 600);

            loseVideoStage = new Stage();
            loseVideoStage.setTitle("You Lose!");
            loseVideoStage.setScene(scene);
            loseVideoStage.initModality(Modality.APPLICATION_MODAL);

            loseVideoStage.setOnHidden(e -> stopLoseVideo());

            loseVideoStage.show();

            loseMediaPlayer.setOnEndOfMedia(() -> {
                stopLoseVideo();
            });
            loseMediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Lose video not found or could not be played: " + e.getMessage());
        }
    }

    private void stopLoseVideo() {
        if (loseMediaPlayer != null) {
            loseMediaPlayer.stop();
            loseMediaPlayer.dispose();
            loseMediaPlayer = null;
        }
        if (loseVideoStage != null) {
            if (loseVideoStage.isShowing()) {
                loseVideoStage.close();
            }
            loseVideoStage = null;
        }
    }
}
