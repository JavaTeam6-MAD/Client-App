package com.mycompany.presentation.lobbyscreen;

import com.mycompany.core.navigation.Routes;

import com.mycompany.App;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.mycompany.model.app.Player;

public class LobbyScreenController implements Initializable, com.mycompany.data.datasource.remote.NetworkCallback {

    LobbyManager lobbyManager;

    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserScore;
    @FXML
    private Label lblUserChar;
    @FXML
    private Button btnTabFriends;
    @FXML
    private Button btnTabLeaderboard;
    @FXML
    private HBox friendsHeader;
    @FXML
    private Label lblOnlineCount;
    @FXML
    private Label lblOfflineCount;
    @FXML
    private VBox listContainer;

    private String currentView = "FRIENDS"; // FRIENDS or LEADERBOARD
    private String pendingChallengeId = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lobbyManager = new LobbyManager();
        Player player = lobbyManager.getCurrentPlayer();
        if (player != null) {
            lblUserName.setText(player.getUserName());
            if (lblUserScore != null) {
                lblUserScore.setText(String.valueOf(player.getScore()));
            }
            if (lblUserChar != null) {
                lblUserChar.setText(getCharacterSymbol(player.getAvatar()));
            }
        }

        // Start Listening for Server Events
        lobbyManager.startListening(this);

        loadFriends();

        // Auto-refresh friends list every 5 seconds
        new java.util.Timer().scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                // Determine sort based on current view/tab
                boolean sortByScore = "LEADERBOARD".equals(currentView);
                loadFriends(sortByScore);
            }
        }, 5000, 5000);
    }

    // ... Existing methods (loadFriends, etc.) ...

    // Network Callback Implementation
    @Override
    public void onFriendsListReceived(List<Player> friends) {
        javafx.application.Platform.runLater(() -> {
            updatePlayerList(friends);
        });
    }

    @Override
    public void onChallengeReceived(com.mycompany.model.requestModel.ReceiveChallengeRequestModel challenge) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Incoming Challenge");
            alert.setHeaderText("Challenge from Player ID " + challenge.getPlayer1Id()); // Using ID as name is not in
                                                                                         // request
            alert.setContentText("Do you want to accept?");

            javafx.scene.control.ButtonType acceptBtn = new javafx.scene.control.ButtonType("Accept",
                    javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType rejectBtn = new javafx.scene.control.ButtonType("Reject",
                    javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(acceptBtn, rejectBtn);

            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                alert.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            alert.getDialogPane().getStyleClass().add("dialog-pane");

            // Auto-reject after 5 seconds if no action?
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> {
                        if (alert.isShowing()) {
                            alert.close();
                            // Can't easily force result here, but closing usually returns null/cancel
                        }
                    });
                }
            }, 5000);

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

            boolean accepted = false;
            if (result.isPresent() && result.get() == acceptBtn) {
                accepted = true;
            }

            if (accepted) {
                // Show "Record?" Dialog
                javafx.scene.control.Alert recordAlert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.CONFIRMATION);
                recordAlert.setTitle("Record Game?");
                recordAlert.setContentText("Do you want to record this game?");
                javafx.scene.control.ButtonType yesBtn = new javafx.scene.control.ButtonType("Yes",
                        javafx.scene.control.ButtonBar.ButtonData.YES);
                javafx.scene.control.ButtonType noBtn = new javafx.scene.control.ButtonType("No",
                        javafx.scene.control.ButtonBar.ButtonData.NO);
                recordAlert.getButtonTypes().setAll(yesBtn, noBtn);

                if (getClass().getResource("/com/mycompany/styles.css") != null) {
                    recordAlert.getDialogPane().getStylesheets()
                            .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
                }
                recordAlert.getDialogPane().getStyleClass().add("dialog-pane");

                // 5 sec timer for record dialog
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> {
                            if (recordAlert.isShowing()) {
                                recordAlert.close();
                            }
                        });
                    }
                }, 5000);

                recordAlert.showAndWait();
                // Logic for recording preference? Model doesn't support it properly in response
                // yet.
                // Just send response.

                try {
                    com.mycompany.model.responseModel.SendChallengeResponseModel resp = new com.mycompany.model.responseModel.SendChallengeResponseModel(
                            true, challenge.getPlayer1Id());
                    com.mycompany.data.datasource.remote.RemoteServerConnection.getInstance().send(resp);

                    // Navigate to Game handled in onChallengeResponse
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                // Send Reject
                try {
                    com.mycompany.model.responseModel.SendChallengeResponseModel resp = new com.mycompany.model.responseModel.SendChallengeResponseModel(
                            false, challenge.getPlayer1Id());
                    com.mycompany.data.datasource.remote.RemoteServerConnection.getInstance().send(resp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onChallengeResponse(com.mycompany.model.responseModel.ReceiveChallengeResponseModel response) {
        javafx.application.Platform.runLater(() -> {
            if (response.isAccepted()) {
                // Game Started!
                System.out.println("Challenge Accepted! Game ID: " + response.getGameIdUuid());

                int myId = lobbyManager.getCurrentPlayer().getId();
                String myName = lobbyManager.getCurrentPlayer().getUserName();

                String challengerName = response.getChallengerName();
                String opponentNameResp = response.getOpponentName();

                boolean amIChallenger = myName.equals(challengerName);

                String mySymbol = amIChallenger ? "X" : "O";
                boolean isMyTurn = amIChallenger;

                // If I am Challenger, opponent is OpponentName.
                // If I am Opponent, opponent is ChallengerName.
                String opponentName = amIChallenger ? opponentNameResp : challengerName;
                int opponentId = (myId == response.getSenderPlayerId()) ? response.getReceiverPlayerId()
                        : response.getSenderPlayerId();

                com.mycompany.presentation.networkgame.GameContext.getInstance().setGameSession(
                        response.getGameIdUuid(),
                        myId,
                        opponentId,
                        mySymbol,
                        opponentName,
                        isMyTurn);

                try {
                    lobbyManager.stopListening();
                    App.setRoot(Routes.NETWORK_GAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Rejected
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Challenge Rejected");
                alert.setHeaderText(null);
                alert.setContentText("The player rejected your challenge.");
                if (getClass().getResource("/com/mycompany/styles.css") != null) {
                    alert.getDialogPane().getStylesheets()
                            .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
                }
                alert.getDialogPane().getStyleClass().add("dialog-pane");
                alert.show();
            }
        });
    }

    @Override
    public void onMoveReceived(com.mycompany.model.responseModel.MakeMoveResponseModel move) {
        // Not handled here, handled in Game Controller
    }

    @Override
    public void onGameEnd(com.mycompany.model.requestModel.EndGameSessionRequestModel endRequest) {
        // Not handled here
    }

    @Override
    public void onFailure(String errorMessage) {
        javafx.application.Platform.runLater(() -> {
            // Show Error
        });
    }

    // ... Navigation and other methods ...

    // ...

    private void loadFriends(boolean sortByScore) {
        // Update Tab Active States
        String activeStyle = "-fx-background-color: rgba(168, 85, 247, 0.2); -fx-border-color: #a855f7;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-border-color: rgba(255,255,255,0.1);";

        if (sortByScore) {
            btnTabFriends.setStyle(
                    "-fx-background-radius: 20 0 0 0; -fx-border-radius: 20 0 0 0; -fx-border-width: 0 0 2 0; -fx-min-width: 150; "
                            + inactiveStyle);
            btnTabLeaderboard.setStyle(
                    "-fx-background-radius: 0 20 0 0; -fx-border-radius: 0 20 0 0; -fx-border-width: 0 0 2 0; -fx-min-width: 150; "
                            + activeStyle);
        } else {
            btnTabFriends.setStyle(
                    "-fx-background-radius: 20 0 0 0; -fx-border-radius: 20 0 0 0; -fx-border-width: 0 0 2 0; -fx-min-width: 150; "
                            + activeStyle);
            btnTabLeaderboard.setStyle(
                    "-fx-background-radius: 0 20 0 0; -fx-border-radius: 0 20 0 0; -fx-border-width: 0 0 2 0; -fx-min-width: 150; "
                            + inactiveStyle);
        }

        // Run on background thread to avoid blocking UI
        new Thread(() -> {
            // storage in ArrayList to ensure mutability
            List<Player> friends = new java.util.ArrayList<>(lobbyManager.getFriends());

            if (sortByScore) {
                // Add current player for leaderboard
                Player currentPlayer = lobbyManager.getCurrentPlayer();
                if (currentPlayer != null) {
                    // Fix: Check for duplicates before adding
                    boolean exists = false;
                    for (Player p : friends) {
                        if (p.getId() == currentPlayer.getId()) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        friends.add(currentPlayer);
                    }
                }

                // Sort by score descending (high to low)
                friends.sort((p1, p2) -> Long.compare(p2.getScore(), p1.getScore()));
                currentView = "LEADERBOARD";
            } else {
                // Sort friends: Online first, then Offline
                friends.sort((p1, p2) -> {
                    // isActive() returns true for online
                    boolean p1Online = p1.isIsActive();
                    boolean p2Online = p2.isIsActive();
                    if (p1Online && !p2Online)
                        return -1;
                    if (!p1Online && p2Online)
                        return 1;
                    return 0;
                });
                currentView = "FRIENDS";
            }

            javafx.application.Platform.runLater(() -> {
                updatePlayerList(friends);
            });
        }).start();
    }

    // Default loadFriends for initial load (Friends view)
    private void loadFriends() {
        loadFriends(false);
    }

    private void updatePlayerList(java.util.List<Player> players) {
        if (players == null)
            return;

        int onlineCount = 0;
        int offlineCount = 0;

        listContainer.getChildren().clear();

        for (Player p : players) {
            // Check if it's the current user
            Player currentUser = lobbyManager.getCurrentPlayer();
            if (currentUser != null && p.getId() == currentUser.getId()) {
                // If in FRIENDS view, skip current user (don't show self in friends list)
                // If in LEADERBOARD view, show current user
                if (!"LEADERBOARD".equals(currentView)) {
                    continue;
                }
            }

            boolean isOnline = p.isIsActive(); // Using isActive as isOnline
            if (isOnline)
                onlineCount++;
            else
                offlineCount++;

            listContainer.getChildren().add(createPlayerItem(p));
        }

        if (lblOnlineCount != null)
            lblOnlineCount.setText("Online (" + onlineCount + ")");
        if (lblOfflineCount != null)
            lblOfflineCount.setText("Offline (" + offlineCount + ")");
    }

    private HBox createPlayerItem(Player player) {
        HBox item = new HBox(10);
        item.getStyleClass().add("player-item");
        item.setPadding(new javafx.geometry.Insets(10));
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Highlight current player in Leaderboard
        Player currentUser = lobbyManager.getCurrentPlayer();
        if (currentUser != null && player.getId() == currentUser.getId()) {
            item.setStyle(
                    "-fx-border-color: #6900ff; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        }

        // Avatar
        javafx.scene.layout.StackPane avatar = new javafx.scene.layout.StackPane();
        avatar.getStyleClass().add("player-avatar");
        Label avatarEmoji = new Label(getCharacterSymbol(player.getAvatar()));
        avatarEmoji.getStyleClass().add("player-avatar-text");

        // Status Indicator
        javafx.scene.shape.Circle statusDot = new javafx.scene.shape.Circle(5);
        if (player.isIsActive()) {
            statusDot.setFill(javafx.scene.paint.Color.LIMEGREEN);
        } else {
            statusDot.setFill(javafx.scene.paint.Color.GRAY);
        }
        statusDot.setStroke(javafx.scene.paint.Color.WHITE);
        statusDot.setStrokeWidth(1);

        avatar.getChildren().addAll(avatarEmoji, statusDot);
        javafx.scene.layout.StackPane.setAlignment(statusDot, javafx.geometry.Pos.BOTTOM_RIGHT);
        javafx.scene.layout.StackPane.setMargin(statusDot, new javafx.geometry.Insets(0, 0, 4, 4));

        // Info
        VBox info = new VBox(2);
        Label name = new Label(player.getUserName());
        name.getStyleClass().add("player-name");
        Label score = new Label("Score: " + player.getScore());
        score.getStyleClass().add("player-score");

        // Activ Status
        String statusText;
        String statusColor;
        if (!player.isIsActive()) {
            statusText = "Offline";
            statusColor = "#9ca3af"; // Gray
        } else if (player.isIsAvailable()) {
            statusText = "Available";
            statusColor = "#10b981"; // Green
        } else {
            statusText = "In Game";
            statusColor = "#f59e0b"; // Amber/Orange
        }
        Label availability = new Label(statusText);
        availability.setStyle("-fx-font-size: 10px; -fx-text-fill: " + statusColor + ";");

        info.getChildren().addAll(name, score, availability);
        javafx.scene.layout.HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        // Action Button
        Button btnAction = new Button();
        btnAction.getStyleClass().addAll("game-button", "game-button-outline");
        btnAction.setStyle("-fx-font-size: 12px; -fx-padding: 8 16; -fx-min-height: 36px; -fx-pref-height: 36px;");
        btnAction.setText("Challenge");

        // Disable challenge if offline
        if (!player.isIsActive()) {
            btnAction.setDisable(true);
        }

        // "button getChlange do not do any ting in him"
        btnAction.setOnAction(e -> {
            // Challenge Flow:
            // 1. Show Dialog: "Record Game?" and "Choose Symbol"
            // For now, let's keep it simple as requested: "Dialog with checkbox to record
            // and choose symbol"

            javafx.scene.control.Dialog<javafx.util.Pair<String, Boolean>> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Challenge " + player.getUserName());
            dialog.setHeaderText("Challenge Settings");

            // Set the button types
            javafx.scene.control.ButtonType sendButtonType = new javafx.scene.control.ButtonType("Send Challenge",
                    javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, javafx.scene.control.ButtonType.CANCEL);

            // Create UI
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            javafx.scene.control.CheckBox recordCb = new javafx.scene.control.CheckBox("Record Game");
            // Symbol Choice (e.g. ComboBox or Toggle) - defaulting to X/O logic handled by
            // server/client?
            // User requested "choose the symbol i want to play with".
            // Implementation detail: SendChallengeRequestModel might NOT have symbol field
            // yet?
            // Checking SendChallengeRequestModel... it usually has p1, p2.
            // If model doesn't support symbol, we can't send it.
            // Assuming we just send challenge for now.

            grid.add(recordCb, 0, 0);

            dialog.getDialogPane().setContent(grid);

            // Style
            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            dialog.getDialogPane().getStyleClass().add("dialog-pane");

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == sendButtonType) {
                    return new javafx.util.Pair<>("X", recordCb.isSelected());
                }
                return null;
            });

            java.util.Optional<javafx.util.Pair<String, Boolean>> result = dialog.showAndWait();

            if (result.isPresent()) {
                // Send Challenge
                int myId = lobbyManager.getCurrentPlayer().getId();
                int opponentId = player.getId();
                com.mycompany.model.requestModel.SendChallengeRequestModel req = new com.mycompany.model.requestModel.SendChallengeRequestModel(
                        myId, opponentId);

                // Note: Request model might need update to carry "record" and "symbol" info if
                // server supports it.
                // Current usage implies basic challenge.

                try {
                    com.mycompany.data.datasource.remote.RemoteServerConnection.getInstance().send(req);
                    // Show "Waiting for response" Dialog (Non-blocking or blocking?)
                    // "Sender cannot send other challenge for 10 seconds"
                    btnAction.setDisable(true);

                    // Show visual feedback
                    javafx.scene.control.Alert waitingAlert = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.INFORMATION);
                    waitingAlert.setTitle("Waiting");
                    waitingAlert.setHeaderText(null);
                    waitingAlert.setContentText("Waiting for " + player.getUserName() + " to respond...");

                    // Styling
                    if (getClass().getResource("/com/mycompany/styles.css") != null) {
                        waitingAlert.getDialogPane().getStylesheets()
                                .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
                    }
                    waitingAlert.getDialogPane().getStyleClass().add("dialog-pane");

                    waitingAlert.show(); // Non-blocking so we can receive response?
                    // Actually showAndWait blocks UI thread, preventing us from processing response
                    // in onChallengeResponse (if runLater).
                    // So show() is better, but we need to close it when response arrives.
                    // Saving reference to close it?

                    // Re-enable button after 10s if no response?
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                btnAction.setDisable(false);
                                waitingAlert.close();
                            });
                        }
                    }, 10000);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item.getChildren().addAll(avatar, info);

        // Only show Challenge button in Friends view
        if (!"LEADERBOARD".equals(currentView)) {
            item.getChildren().add(btnAction);
        }

        return item;
    }

    @FXML
    private void onTabFriends() {
        loadFriends(false);
    }

    @FXML
    private void onTabLeaderboard() {
        loadFriends(true);
    }

    @FXML
    private void onProfile() {
        try {
            lobbyManager.stopListening();
            App.setRoot(Routes.PROFILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordedGames() {
        try {
            lobbyManager.stopListening();
            App.setRoot(Routes.GAME_HISTORY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBack() {
        try {
            lobbyManager.leaveLobby();
            lobbyManager.stopListening();
            App.setRoot(Routes.HOME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                dialogPane.getStylesheets().add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            dialogPane.getStyleClass().add("dialog-pane");

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                lobbyManager.logout();
                lobbyManager.stopListening();
                App.setRoot(Routes.AUTH);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
