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

import java.io.IOException;
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

    @FXML
    public void initialize() {
        remoteDataSource = new RemoteDataSource(); // Or reuse singleton logic if available
        remoteDataSource.startListening(this);
        soundManager = SoundManager.getInstance();
        context = GameContext.getInstance();

        // Init UI
        if ("X".equals(context.getMySymbol())) {
            playerXName.setText("You");
            playerOName.setText(context.getOpponentName());
        } else {
            playerXName.setText(context.getOpponentName());
            playerOName.setText("You");
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
        if (!clickedBtn.getText().isEmpty()) {
            return;
        }

        // Optimistic UI Update? No, wait for server response safely?
        // Or at least disable button.
        // Let's Optimistic:
        clickedBtn.setText(context.getMySymbol());
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
            if (move.getPlayerId() == context.getMyId()) {
                // My move confirmed.
                // If I already updated UI optimistically, check consistency?
                // For simplicity, ensure UI is set.
                buttons[r][c].setText(context.getMySymbol());
            } else {
                // Opponent move
                buttons[r][c].setText(symbol);
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
        if (winnerSymbol == null || winnerSymbol.isEmpty()) { // Draw (Server might send Empty?)
            msg = "It's a Draw!";
        } else if (winnerSymbol.equals(context.getMySymbol())) {
            msg = "You Won! 🎉";
            if (soundManager != null)
                soundManager.playSound(SoundManager.WIN);
        } else {
            msg = "You Lost! 😔";
            if (soundManager != null)
                soundManager.playSound(SoundManager.LOSE);
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
        // Handle "Play Again" challenge?
        // Reuse Lobby logic or implemented here?
        // User said: "handle the play again to make this cycle again"
        // Same as Lobby logic basically.
    }

    @Override
    public void onChallengeResponse(ReceiveChallengeResponseModel response) {
        // Handle Play Again response
        Platform.runLater(() -> {
            if (response.isAccepted()) {
                // Reset board
                resetInternal();
                // Update Game ID if changed?
                context.setGameSession(response.getGameIdUuid(), context.getMyId(), context.getOpponentId(),
                        context.getMySymbol(), context.getOpponentName(), context.isMyTurn());
                // Note: Logic for who starts next? Server decides? For tic tac toe usually
                // loser starts or alternate.
                // Assuming context update is enough.
            } else {
                statusText.setText("Opponent rejected rematch.");
            }
        });
    }

    private void resetInternal() {
        gameGrid.setDisable(false);
        for (Button[] row : buttons) {
            for (Button b : row) {
                if (b != null)
                    b.setText("");
            }
        }
        playAgainButton.setVisible(false);
        homeButton.setVisible(false);
        updateStatus();
    }

    @Override
    public void onGameEnd(EndGameSessionRequestModel endRequest) {
        // Opponent left?
        Platform.runLater(() -> {
            statusText.setText("Opponent left the game.");
            gameGrid.setDisable(true);
            homeButton.setVisible(true);
        });
    }

    @Override
    public void onFailure(String errorMessage) {
    }

    @FXML
    private void onHome(ActionEvent event) {
        try {
            remoteDataSource.stopListening();
            App.setRoot(Routes.HOME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Confirm forfeit
        onHome(event);
    }

    @FXML
    private void resetGame(ActionEvent event) {
        // Send Challenge (Play Again)
        // Similar to Lobby
    }
}
