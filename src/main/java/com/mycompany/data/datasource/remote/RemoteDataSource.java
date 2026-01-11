package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Game;
import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.LoginRequestModel;
import com.mycompany.model.requestModel.LogoutRequestModel;
import com.mycompany.model.requestModel.RegisterRequestModel;
import com.mycompany.model.requestModel.getFriendsRequestModel;
import com.mycompany.presentation.lobbyscreen.LobbyManager;
import com.mycompany.presentation.networkgame.NetworkGameManager;

import java.util.List;
import com.mycompany.model.requestModel.ChangeNameRequestModel;
import com.mycompany.model.requestModel.ChangePasswordRequestModel;
import com.mycompany.model.requestModel.ChangeAvatarRequestModel;
import com.mycompany.model.requestModel.MakeUnavailableRequestModel;
import com.mycompany.presentation.gamehistory.GameHistoryManager;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class RemoteDataSource {
    private static String serverIp = "localhost";
    private static final int SERVER_PORT = 12345;

    private static RemoteDataSource instance;
    private ServerListener listener;

    // References to Active Managers
    private LobbyManager lobbyManager;
    private NetworkGameManager networkGameManager;
    private GameHistoryManager gameHistoryManager;

    private RemoteDataSource() {
    }

    public static synchronized RemoteDataSource getInstance() {
        if (instance == null) {
            instance = new RemoteDataSource();
        }
        return instance;
    }

    public static void setServerIp(String ip) {
        serverIp = ip;
    }

    public void setLobbyManager(LobbyManager manager) {
        this.lobbyManager = manager;
    }

    public void detachLobbyManager() {
        this.lobbyManager = null;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public void setNetworkGameManager(NetworkGameManager manager) {
        this.networkGameManager = manager;
    }

    public void detachNetworkGameManager() {
        this.networkGameManager = null;
    }

    public NetworkGameManager getNetworkGameManager() {
        return networkGameManager;
    }

    public void setGameHistoryManager(GameHistoryManager manager) {
        this.gameHistoryManager = manager;
    }

    public void detachGameHistoryManager() {
        this.gameHistoryManager = null;
    }

    public com.mycompany.presentation.gamehistory.GameHistoryManager getGameHistoryManager() {
        return gameHistoryManager;
    }

    /**
     * Handle server shutdown notification
     * Shows alert to user and redirects to home screen
     */
    public void handleServerShutdown(String message) {
        javafx.application.Platform.runLater(() -> {
            try {
                // Show alert
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Server Down");
                alert.setHeaderText("Connection Lost");
                alert.setContentText(message != null ? message : "Server has shut down");
                alert.showAndWait();

                // Disconnect from server
                RemoteServerConnection.getInstance().disconnect();

                // Stop listener
                if (listener != null) {
                    listener.stopListener();
                    listener = null;
                }

                // Clear managers
                lobbyManager = null;
                networkGameManager = null;
                gameHistoryManager = null;

                // Navigate to home screen
                com.mycompany.App.setRoot(com.mycompany.core.navigation.Routes.HOME);
            } catch (Exception e) {
                System.err.println("Error handling server shutdown: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public Player login(String username, String password) {
        System.out.println(username + password + "  login data source");
        return sendPlayerRequest(new LoginRequestModel(username, password));
    }

    public Player register(String username, String password) {
        return sendPlayerRequest(new RegisterRequestModel(username, password));
    }

    public void startListening() {
        if (listener != null && listener.isAlive()) {
            return;
        } else if (RemoteServerConnection.getInstance().isConnected()) {
            try {
                ObjectInputStream in = RemoteServerConnection.getInstance().getInputStream();
                listener = new ServerListener(in);
                listener.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            RemoteServerConnection.getInstance().connect(serverIp, SERVER_PORT);
            ObjectInputStream in = RemoteServerConnection.getInstance().getInputStream();
            listener = new ServerListener(in);
            listener.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {
        // Just stop local processing if needed
    }

    /*
     * public void disconnect() {
     * if (listener != null) {
     * listener.stopListener();
     * RemoteServerConnection.getInstance().disconnect();
     * listener = null;
     * }
     * RemoteServerConnection.getInstance().disconnect();
     * lobbyManager = null;
     * networkGameManager = null;
     * }
     */

    public List<Player> getFriends(int userId) {
        try {
            RemoteServerConnection.getInstance().connect(serverIp, SERVER_PORT);
            RemoteServerConnection.getInstance().send(new getFriendsRequestModel(userId));
        } catch (Exception e) {
            System.err.println("Error loading friends: " + e.getMessage());
            e.printStackTrace();
            RemoteServerConnection.getInstance().disconnect();
        }
        return new ArrayList<>();
    }

    public List<Game> getGameHistory(int userId) {
        try {
            RemoteServerConnection.getInstance().connect(serverIp, SERVER_PORT);
            RemoteServerConnection.getInstance()

                    .send(new com.mycompany.model.requestModel.GetGamesRequestModel(userId));
        } catch (Exception e) {
            e.printStackTrace();
            RemoteServerConnection.getInstance().disconnect();
        }
        return new ArrayList<>();
    }

    public Player changeUserName(int id, String newName) {
        return sendPlayerRequest(new ChangeNameRequestModel(id, newName));
    }

    public Player changePassword(int id, String password) {
        return sendPlayerRequest(new ChangePasswordRequestModel(id, password));
    }

    public Player changeAvatar(int id, String avatar) {
        return sendPlayerRequest(new ChangeAvatarRequestModel(id, avatar));
    }

    public void makeUnavailable(int id) {
        try {
            listener.stopListener();
            RemoteServerConnection.getInstance().send(new MakeUnavailableRequestModel(id));
            System.out.println(id);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(id);
        }
    }

    public void logout(int playerId) {
        try {
            RemoteServerConnection.getInstance().connect(serverIp, SERVER_PORT);
            RemoteServerConnection.getInstance().send(new LogoutRequestModel(playerId));
            RemoteServerConnection.getInstance().disconnect();
        } catch (Exception e) {
            System.err.println("Logout network error: " + e.getMessage());
            RemoteServerConnection.getInstance().disconnect();
        }
    }

    private Player sendPlayerRequest(Object request) {
        try {
            RemoteServerConnection.getInstance().connect(serverIp, SERVER_PORT);
            RemoteServerConnection.getInstance().send(request);
            Object response = RemoteServerConnection.getInstance().receive();

            if (response instanceof Player) {
                return (Player) response;
            }
        } catch (Exception e) {
            System.err.println("Network error: " + e.getMessage());
            e.printStackTrace();
            RemoteServerConnection.getInstance().disconnect();
        }
        Player errorPlayer = new Player();
        errorPlayer.setId(0);
        return errorPlayer;
    }
}
