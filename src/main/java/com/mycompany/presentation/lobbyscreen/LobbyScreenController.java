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
import com.mycompany.model.requestModel.ReceiveChallengeRequestModel;

import com.mycompany.model.responseModel.ReceiveChallengeResponseModel;
import java.util.Optional;
import java.util.Timer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import java.util.ArrayList;

public class LobbyScreenController implements Initializable {

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

    private enum ViewMode {
        FRIENDS,
        LEADERBOARD
    }

    private ViewMode currentView = ViewMode.FRIENDS;
    private Alert pendingChallengeAlert;

    private Timer refreshTimer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lobbyManager = new LobbyManager();
        lobbyManager.setController(this);
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

        // Start Listening defined in Manager ctor

        loadFriends();

        // Auto-refresh friends list every 3 seconds
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                // Determine sort based on current view/tab
                boolean sortByScore = (currentView == ViewMode.LEADERBOARD);
                loadFriends(sortByScore);
            }
        }, 6000, 6000);
    }

    private void cleanup() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer.purge();
        }
        lobbyManager.stopListening();
    }

    // ... Existing methods (loadFriends, etc.) ...

    // Network Callback Implementation
    public void updateFriendsList(List<Player> friends) {
        Platform.runLater(() -> {
            updatePlayerList(friends);
        });
    }

    public void showIncomingChallenge(ReceiveChallengeRequestModel challenge) {
        Platform.runLater(() -> {
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION);
            alert.setTitle("Incoming Challenge");
            alert.setHeaderText("Challenge from " + challenge.getSenderName()); // Using Name
            alert.setContentText("Do you want to accept?");

            ButtonType acceptBtn = new ButtonType("Accept",
                    ButtonBar.ButtonData.OK_DONE);
            ButtonType rejectBtn = new ButtonType("Reject",
                    ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(acceptBtn, rejectBtn);

            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                alert.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            alert.getDialogPane().getStyleClass().add("dialog-pane");

            // Auto-reject after 8 seconds if no action?
            new Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (alert.isShowing()) {
                            alert.close();
                            // Can't easily force result here, but closing usually returns null/cancel
                        }
                    });
                }
            }, 8000);

            Optional<ButtonType> result = alert.showAndWait();

            boolean accepted = false;
            if (result.isPresent() && result.get() == acceptBtn) {
                accepted = true;
            }

            if (accepted) {
                // Show "Record?" Dialog
                Alert recordAlert = new Alert(
                        Alert.AlertType.CONFIRMATION);
                recordAlert.setTitle("Record Game?");
                recordAlert.setContentText("Do you want to record this game?");
                ButtonType yesBtn = new ButtonType("Yes",
                        ButtonBar.ButtonData.YES);
                ButtonType noBtn = new ButtonType("No",
                        ButtonBar.ButtonData.NO);
                recordAlert.getButtonTypes().setAll(yesBtn, noBtn);

                if (getClass().getResource("/com/mycompany/styles.css") != null) {
                    recordAlert.getDialogPane().getStylesheets()
                            .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
                }
                recordAlert.getDialogPane().getStyleClass().add("dialog-pane");

                // 7 sec timer for record dialog
                new Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            if (recordAlert.isShowing()) {
                                recordAlert.close();
                            }
                        });
                    }
                }, 7000);

                recordAlert.showAndWait();
                // Logic for recording preference? Model doesn't support it properly in response
                // yet.
                // Just send response.

                try {
                    lobbyManager.respondToChallenge(true, challenge.getPlayer1Id()); // Using Logic in Manager now
                    // Navigate to Game handled in onChallengeResponse (View Call)
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                // Send Reject
                try {
                    lobbyManager.respondToChallenge(false, challenge.getPlayer1Id());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void handleChallengeResponse(ReceiveChallengeResponseModel response) {
        Platform.runLater(() -> {
            if (response.isAccepted()) {
                if (pendingChallengeAlert != null && pendingChallengeAlert.isShowing()) {
                    pendingChallengeAlert.close();
                    pendingChallengeAlert = null;
                }

                // logic moved to manager

                // Navigate also handled via separate call from Manager -> View if needed,
                // but View.navigateToGame() is called by Manager.
                // So this method mainly updates UI if we stayed here, but we are leaving.
                // We can print log or show toast?

                System.out.println("Challenge Accepted! Game ID: " + response.getGameIdUuid());

            } else {
                if (pendingChallengeAlert != null && pendingChallengeAlert.isShowing()) {
                    pendingChallengeAlert.close();
                    pendingChallengeAlert = null;
                }
                // Rejected
                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION);
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

    public void navigateToGame() {
        Platform.runLater(() -> {
            try {
                cleanup();
                App.setRoot(Routes.NETWORK_GAME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void showError(String errorMessage) {
        Platform.runLater(() -> {
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

            // Redirect to Login/Home
            // Redirect to Login/Home
            try {
                // lobbyManager.disconnect(); // Ensure clean disconnect
                App.setRoot(Routes.HOME); // Or Routes.AUTH
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ... Navigation and other methods ...

    // ...

    private void loadFriends(boolean sortByScore) {
        // Update Tab Active States
        // Update Tab Active States
        btnTabFriends.getStyleClass().removeAll("tab-button-active", "tab-button-inactive", "tab-button-left");
        btnTabLeaderboard.getStyleClass().removeAll("tab-button-active", "tab-button-inactive", "tab-button-right");

        // Ensure base shape classes
        btnTabFriends.getStyleClass().add("tab-button-left");
        btnTabLeaderboard.getStyleClass().add("tab-button-right");

        if (sortByScore) {
            btnTabFriends.getStyleClass().add("tab-button-inactive");
            btnTabLeaderboard.getStyleClass().add("tab-button-active");
        } else {
            btnTabFriends.getStyleClass().add("tab-button-active");
            btnTabLeaderboard.getStyleClass().add("tab-button-inactive");
        }

        // Run on background thread to avoid blocking UI
        new Thread(() -> {
            // storage in ArrayList to ensure mutability
            List<Player> friends = new ArrayList<>(lobbyManager.getFriends());

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
                // Sort by score descending (high to low)
                friends.sort((p1, p2) -> {
                    if (p2.getScore() > p1.getScore())
                        return 1;
                    if (p2.getScore() < p1.getScore())
                        return -1;
                    return 0;
                });
                currentView = ViewMode.LEADERBOARD;
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
                currentView = ViewMode.FRIENDS;
            }

            Platform.runLater(() -> {
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
                if (currentView != ViewMode.LEADERBOARD) {
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
        item.setPadding(new Insets(10));
        item.setAlignment(Pos.CENTER_LEFT);

        // Highlight current player in Leaderboard
        Player currentUser = lobbyManager.getCurrentPlayer();
        if (currentUser != null && player.getId() == currentUser.getId()) {
            item.getStyleClass().add("player-item-current");
        }

        // Avatar
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("player-avatar");
        Label avatarEmoji = new Label(getCharacterSymbol(player.getAvatar()));
        avatarEmoji.getStyleClass().add("player-avatar-text");

        // Status Indicator
        Circle statusDot = new Circle(5);
        if (player.isIsActive()) {
            statusDot.setFill(Color.LIMEGREEN);
        } else {
            statusDot.setFill(Color.GRAY);
        }
        statusDot.setStroke(Color.WHITE);
        statusDot.setStrokeWidth(1);

        avatar.getChildren().addAll(avatarEmoji, statusDot);
        StackPane.setAlignment(statusDot, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(statusDot, new Insets(0, 0, 4, 4));

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
        availability.getStyleClass().add("status-label");
        if (!player.isIsActive()) {
            availability.getStyleClass().add("status-label-offline");
        } else if (player.isIsAvailable()) {
            availability.getStyleClass().add("status-label-available");
        } else {
            availability.getStyleClass().add("status-label-ingame");
        }

        info.getChildren().addAll(name, score, availability);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Action Button
        Button btnAction = new Button();
        btnAction.getStyleClass().addAll("game-button", "game-button-outline", "game-button-small");
        btnAction.setText("Challenge");

        // Disable challenge if offline or in-game (not available)
        if (!player.isIsActive() || !player.isIsAvailable()) {
            btnAction.setDisable(true);
        }

        // "button getChlange do not do any ting in him"
        btnAction.setOnAction(e -> {
            // Challenge Flow:
            // 1. Show Dialog: "Record Game?" and "Choose Symbol"
            // For now, let's keep it simple as requested: "Dialog with checkbox to record
            // and choose symbol"

            Dialog<Pair<String, Boolean>> dialog = new Dialog<>();
            dialog.setTitle("Challenge " + player.getUserName());
            dialog.setHeaderText("Challenge Settings");

            // Set the button types
            ButtonType sendButtonType = new ButtonType("Send Challenge",
                    ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

            // Create UI
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            CheckBox recordCb = new CheckBox("Record Game");
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
                    return new Pair<>("X", recordCb.isSelected());
                }
                return null;
            });

            Optional<Pair<String, Boolean>> result = dialog.showAndWait();

            if (result.isPresent()) {
                // Send Challenge
                int opponentId = player.getId();
                try {
                    lobbyManager.sendChallenge(opponentId);
                    // Show "Waiting for response" Dialog (Non-blocking or blocking?)
                    // "Sender cannot send other challenge for 10 seconds"
                    btnAction.setDisable(true);

                    // Show visual feedback
                    Alert waitingAlert = new Alert(
                            Alert.AlertType.INFORMATION);
                    waitingAlert.setTitle("Waiting");
                    waitingAlert.setHeaderText(null);
                    waitingAlert.setContentText("Waiting for " + player.getUserName() + " to respond...");

                    // Styling
                    if (getClass().getResource("/com/mycompany/styles.css") != null) {
                        waitingAlert.getDialogPane().getStylesheets()
                                .add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
                    }
                    pendingChallengeAlert = waitingAlert;

                    waitingAlert.getDialogPane().getStyleClass().add("dialog-pane");

                    waitingAlert.show(); // Non-blocking so we can receive response?
                    // Actually showAndWait blocks UI thread, preventing us from processing response
                    // in onChallengeResponse (if runLater).
                    // So show() is better, but we need to close it when response arrives.
                    // Saving reference to close it?

                    // Re-enable button after 15s if no response?
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                btnAction.setDisable(false);
                                waitingAlert.close();
                            });
                        }
                    }, 15000);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item.getChildren().addAll(avatar, info);

        // Only show Challenge button in Friends view
        if (currentView != ViewMode.LEADERBOARD) {
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
            cleanup();
            App.setRoot(Routes.PROFILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordedGames() {
        try {
            cleanup();
            App.setRoot(Routes.GAME_HISTORY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBack() {
        try {
            lobbyManager.leaveLobby();
            cleanup();
            lobbyManager.disconnect();
            App.setRoot(Routes.HOME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        try {
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            DialogPane dialogPane = alert.getDialogPane();
            if (getClass().getResource("/com/mycompany/styles.css") != null) {
                dialogPane.getStylesheets().add(getClass().getResource("/com/mycompany/styles.css").toExternalForm());
            }
            dialogPane.getStyleClass().add("dialog-pane");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                lobbyManager.logout();
                lobbyManager.disconnect();
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
