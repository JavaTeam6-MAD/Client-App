package com.mycompany.presentation.networkgame;

import com.mycompany.App;
import com.mycompany.core.navigation.Routes;
import com.mycompany.core.utils.SoundManager;
import com.mycompany.data.datasource.remote.NetworkCallback;
import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.EndGameSessionRequestModel;
import com.mycompany.model.requestModel.MakeMoveRequestModel;
import com.mycompany.model.requestModel.ReceiveChallengeRequestModel;
import com.mycompany.model.responseModel.MakeMoveResponseModel;
import com.mycompany.model.responseModel.ReceiveChallengeResponseModel;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

public class NetworkGameController implements NetworkCallback {

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

    private RemoteDataSource remoteDataSource; // Ideally use a Manager/Repo
    private SoundManager soundManager;
    private GameContext context;
    private Button[][] buttons = new Button[3][3];

    // SVG Paths
    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    @FXML
    public void initialize() {
        remoteDataSource = RemoteDataSource.getInstance();
        remoteDataSource.startListening(this);
        soundManager = SoundManager.getInstance();
        context = GameContext.getInstance();

        // Init UI
        if ("X".equals(context.getMySymbol())) {
            playerXName.setText("You");
            playerOName.setText(context.getOpponentName());
            scoreX.setText(String.valueOf(context.getMySessionScore())); // Session Score
            scoreO.setText(String.valueOf(context.getOpponentSessionScore()));
        } else {
            playerXName.setText(context.getOpponentName());
            playerOName.setText("You");
            scoreO.setText(String.valueOf(context.getMySessionScore()));
            scoreX.setText(String.valueOf(context.getOpponentSessionScore()));
        }

        updateStatus();
        initGrid();
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
        if (context.isMyTurn()) {
            statusText.setText("Your Turn (" + context.getMySymbol() + ")");
        } else {
            statusText.setText("Opponent's Turn");
        }
    }

    @FXML
    private void handleGridClick(ActionEvent event) {
        if (!context.isMyTurn()) {
            return;
        }

        Button clickedBtn = (Button) event.getSource();
        // Check if button is already occupied (Text or Graphic)
        if (!clickedBtn.getText().isEmpty() || clickedBtn.getGraphic() != null) {
            return;
        }

        // Optimistic UI Update using SVG
        String path = context.getMySymbol().equals("X") ? PATH_X : PATH_O;
        javafx.scene.shape.SVGPath svg = new javafx.scene.shape.SVGPath();
        svg.setContent(path);
        svg.setStyle("-fx-fill: transparent; -fx-stroke: " + (context.getMySymbol().equals("X") ? "#6900ff" : "#00ffcc")
                + "; -fx-stroke-width: 5;");
        svg.setScaleX(0.5);
        svg.setScaleY(0.5);

        clickedBtn.setGraphic(svg);
        clickedBtn.setText("");
        // clickedBtn.getStyleClass().add(context.getMySymbol().equals("X") ? "x-cell" :
        // "o-cell");

        context.setMyTurn(false);
        updateStatus();

        if (soundManager != null)
            soundManager.playSound(SoundManager.PLACE_X); // Generic sound

        Integer index = Integer.parseInt((String) clickedBtn.getUserData());
        int r = index / 3;
        int c = index % 3;

        try {
            MakeMoveRequestModel req = new MakeMoveRequestModel(r, c, context.getGameId(), context.getMySymbol());
            // Need to access connection safely. RemoteDataSource.getInstance()?
            // Assuming RemoteDataSource has static access or we fix it to specific method
            com.mycompany.data.datasource.remote.RemoteServerConnection.getInstance().send(req);
        } catch (Exception e) {
            e.printStackTrace();
            // Revert on error?
        }
    }

    @Override
    public void onMoveReceived(MakeMoveResponseModel move) {
        Platform.runLater(() -> {
            int r = move.getRow();
            int c = move.getCol();
            String symbol = (move.getPlayerId() == context.getMyId()) ? context.getMySymbol()
                    : (context.getMySymbol().equals("X") ? "O" : "X");

            // Check if it's my move response (ACK) or Opponent move
            // Check if it's my move response (ACK) or Opponent move
            String path = symbol.equals("X") ? PATH_X : PATH_O;
            javafx.scene.shape.SVGPath svg = new javafx.scene.shape.SVGPath();
            svg.setContent(path);
            svg.setStyle("-fx-fill: transparent; -fx-stroke: " + (symbol.equals("X") ? "#6900ff" : "#00ffcc")
                    + "; -fx-stroke-width: 5;");

            // Adjust size to fit button (scaling)
            svg.setScaleX(0.5);
            svg.setScaleY(0.5);

            if (move.getPlayerId() == context.getMyId()) {
                // My move confirmed.
                buttons[r][c].setGraphic(svg);
                buttons[r][c].setText(""); // Clear text if any
                // buttons[r][c].getStyleClass().add(context.getMySymbol().equals("X") ?
                // "x-cell" : "o-cell");
            } else {
                // Opponent move
                buttons[r][c].setGraphic(svg);
                buttons[r][c].setText("");
                // buttons[r][c].getStyleClass().add(symbol.equals("X") ? "x-cell" : "o-cell");
                context.setMyTurn(true);
                updateStatus();
                if (soundManager != null)
                    soundManager.playSound(SoundManager.PLACE_O);
            }

            if (move.isGameOver()) {
                handleGameOver(move.getWinner());
            }
        });
    }

    private void handleGameOver(String winnerSymbol) {
        // Disable grid
        gameGrid.setDisable(true);

        String msg;
        if (winnerSymbol == null || winnerSymbol.isEmpty()) { // Draw
            msg = "It's a Draw!";
            // Draw: Increment BOTH
            context.incrementMySessionScore();
            context.incrementOpponentSessionScore();
        } else if (winnerSymbol.equals(context.getMySymbol())) {
            msg = "You Won! 🎉";
            context.incrementMySessionScore();
            if (soundManager != null)
                soundManager.playSound(SoundManager.WIN);
        } else {
            msg = "You Lost! 😔";
            context.incrementOpponentSessionScore();
            if (soundManager != null)
                soundManager.playSound(SoundManager.LOSE);
        }

        // Update UI Score immediately
        if ("X".equals(context.getMySymbol())) {
            scoreX.setText(String.valueOf(context.getMySessionScore()));
            scoreO.setText(String.valueOf(context.getOpponentSessionScore()));
        } else {
            scoreO.setText(String.valueOf(context.getMySessionScore()));
            scoreX.setText(String.valueOf(context.getOpponentSessionScore()));
        }

        statusText.setText(msg);

        // Popup 5 seconds
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        if (getClass().getResource("/com/mycompany/styles.css") != null) {
            alert.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
        }
        alert.getDialogPane().getStyleClass().add("dialog-pane");

        // Show buttons
        playAgainButton.setVisible(true);
        homeButton.setVisible(true);

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
    }

    @Override
    public void onFriendsListReceived(List<Player> friends) {
    }

    @Override
    public void onChallengeReceived(ReceiveChallengeRequestModel challenge) {
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

            // Auto-reject/close after timeout matches sender timeout?
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (alert.isShowing()) {
                            alert.close();
                        }
                    });
                }
            }, 10000); // 10s

            Optional<ButtonType> result = alert.showAndWait();

            boolean accepted = (result.isPresent() && result.get() == acceptBtn);

            try {
                // Response
                // We need challenger ID. The request has player1Id (Sender).
                // Logic: Sender sent challenge. We respond to Sender.
                int challengerId = challenge.getPlayer1Id();
                com.mycompany.model.responseModel.SendChallengeResponseModel resp = new com.mycompany.model.responseModel.SendChallengeResponseModel(
                        accepted, challengerId);

                com.mycompany.data.datasource.remote.RemoteServerConnection.getInstance().send(resp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onChallengeResponse(ReceiveChallengeResponseModel response) {
        // Handle Play Again response
        Platform.runLater(() -> {
            if (response.isAccepted()) {
                // Determine new Game Session details
                String myName = context.getMyName();
                // Note: If context is cleared, myName is lost?
                // We should ensure context retains myName or we fetch it.
                // Assuming internal reset doesn't clear context entirely or we get it before
                // reset.

                String challengerName = response.getChallengerName();
                String opponentNameResp = response.getOpponentName();
                boolean amIChallenger = myName.equals(challengerName);
                String mySymbol = amIChallenger ? "X" : "O";
                boolean isMyTurn = amIChallenger;
                String opponentName = amIChallenger ? opponentNameResp : challengerName;

                // Logic for opponent ID might need verification if IDs swap?
                // Sender ID in response is capable of being either P1 or P2 depending on who
                // accepted.
                // We should keep myId constant. Opponent ID is the "other" ID in the message.
                // Actually, simpler: context.getOpponentId() should remain same if playing same
                // person.
                // But let's be robust using response IDs.
                int myId = context.getMyId();
                int opponentId = (myId == response.getSenderPlayerId()) ? response.getReceiverPlayerId()
                        : response.getSenderPlayerId();

                long myScore = amIChallenger ? response.getChallengerScore() : response.getOpponentScore();
                long opponentScore = amIChallenger ? response.getOpponentScore() : response.getChallengerScore();

                // Reset board
                resetInternal();

                // Update Game ID if changed?
                context.setGameSession(response.getGameIdUuid(), myId, myName, opponentId,
                        mySymbol, opponentName, isMyTurn, myScore, opponentScore);

                // Re-init UI names and status
                if ("X".equals(context.getMySymbol())) {
                    playerXName.setText("You");
                    playerOName.setText(context.getOpponentName());
                    scoreX.setText(String.valueOf(context.getMyScore()));
                    scoreO.setText(String.valueOf(context.getOpponentScore()));
                } else {
                    playerXName.setText(context.getOpponentName());
                    playerOName.setText("You");
                    scoreO.setText(String.valueOf(context.getMyScore()));
                    scoreX.setText(String.valueOf(context.getOpponentScore()));
                }
                updateStatus();

            } else {
                statusText.setText("Opponent rejected rematch.");
                // Re-enable play again button?
                playAgainButton.setDisable(true); // Or leave disabled.
                // Should show alert?
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rejected");
                alert.setHeaderText(null);
                alert.setContentText("Opponent rejected rematch.");
                if (getClass().getResource("/com/mycompany/styles.css") != null) {
                    alert.getDialogPane().getStylesheets()
                            .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
                }
                alert.getDialogPane().getStyleClass().add("dialog-pane");
                alert.showAndWait(); // Block to let user see it

                try {
                    remoteDataSource.stopListening();
                    App.setRoot(Routes.LOBBY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resetInternal() {
        gameGrid.setDisable(false);
        for (Button[] row : buttons) {
            for (Button b : row) {
                if (b != null) {
                    b.setText("");
                    b.setGraphic(null);
                    b.getStyleClass().removeAll("x-cell", "o-cell");
                }
            }
        }
        playAgainButton.setVisible(false);
        homeButton.setVisible(false);
        updateStatus();
    }

    @Override
    public void onGameEnd(EndGameSessionRequestModel endRequest) {
        // Opponent left / Forfeit
        Platform.runLater(() -> {
            // Show Alert
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

            // Redirect to Lobby
            onHome(null);
        });
    }

    @Override
    public void onFailure(String errorMessage) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Connection Lost");
            alert.setContentText(errorMessage != null ? errorMessage : "Connection to server lost.");

            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                alert.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            alert.getDialogPane().getStyleClass().add("dialog-pane");

            alert.showAndWait();

            // Redirect
            onHome(null);
        });
    }

    @FXML
    private void onHome(ActionEvent event) {
        try {
            remoteDataSource.stopListening();
            App.setRoot(Routes.LOBBY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Confirm forfeit
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Forfeit Game");
        alert.setHeaderText("Warning: Forfeit");
        alert.setContentText("If you leave now, you will lose the game and 30 points. Are you sure?");

        if (getClass().getResource("/com/mycompany/styles.css") != null) {
            alert.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
        }
        alert.getDialogPane().getStyleClass().add("dialog-pane");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Send Forfeit Request
            try {
                // Using EndGameSessionRequestModel with arbitrary status to signal forfeit
                // Server interprets EndGameSessionRequestModel as forfeit from sender
                com.mycompany.model.utils.GameStatus status = com.mycompany.model.utils.GameStatus.LOSE; // I lose
                com.mycompany.model.requestModel.EndGameSessionRequestModel req = new com.mycompany.model.requestModel.EndGameSessionRequestModel(
                        context.getMyId(), context.getOpponentId(), status);

                com.mycompany.data.datasource.remote.RemoteServerConnection.getInstance().send(req);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Navigate away
            onHome(event);
        }
    }

    @FXML
    private void resetGame(ActionEvent event) {
        // Send Challenge (Play Again) is basically sending a new challenge to the same
        // opponent.
        try {
            int myId = context.getMyId();
            int opponentId = context.getOpponentId();

            com.mycompany.model.requestModel.SendChallengeRequestModel req = new com.mycompany.model.requestModel.SendChallengeRequestModel(
                    myId, opponentId);

            com.mycompany.data.datasource.remote.RemoteServerConnection.getInstance().send(req);

            // UI Feedback
            statusText.setText("Requesting Rematch...");
            playAgainButton.setDisable(true);

            // Timeout reset/enable?
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        // If still disabled (no response), re-enable or show timeout
                        if (playAgainButton.isDisabled()) {
                            playAgainButton.setDisable(false);
                            statusText.setText("Rematch timed out.");
                        }
                    });
                }
            }, 15000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
