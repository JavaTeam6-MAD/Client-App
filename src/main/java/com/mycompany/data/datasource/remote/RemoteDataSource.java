package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.LoginRequestModel;
import com.mycompany.model.requestModel.LogoutRequestModel;
import com.mycompany.model.requestModel.RegisterRequestModel;
import com.mycompany.model.requestModel.getFriendsRequestModel;

import java.util.List;
import com.mycompany.model.requestModel.ChangeNameRequestModel;
import com.mycompany.model.requestModel.ChangePasswordRequestModel;
import com.mycompany.model.requestModel.ChangeAvatarRequestModel;
import java.io.ObjectInputStream;

public class RemoteDataSource {
    private static String serverIp = "localhost";
    private static final int SERVER_PORT = 12345;

    private static RemoteDataSource instance;
    private ServerListener listener;

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

    public Player login(String username, String password) {
        return sendPlayerRequest(new LoginRequestModel(username, password));
    }

    public Player register(String username, String password) {
        return sendPlayerRequest(new RegisterRequestModel(username, password));
    }

    public void startListening(NetworkCallback callback) {
        if (listener != null && listener.isAlive()) {
            // Hot swap callback
            listener.setCallback(callback);
            return;
        }
        try {
            RemoteServerConnection.getInstance().connect(serverIp, SERVER_PORT);
            ObjectInputStream in = RemoteServerConnection.getInstance().getInputStream();
            listener = new ServerListener(in, callback);
            listener.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {
        // Default behavior: Don't kill connection, just nullify callback?
        // Or if we want to truly stop (e.g. Logout), we should have a separate method.
        // For now, to support persistent connection, we CANNOT close the socket here.
        // We will just remove the callback reference if possible,
        // OR better yet, do nothing and let the next startListening take over.
        // BUT, if we go to a screen that *doesn't* listen, we might want to stop
        // events.
        if (listener != null) {
            listener.setCallback(new NetworkCallback() {
                public void onFriendsListReceived(List<com.mycompany.model.app.Player> friends) {
                }

                public void onChallengeReceived(
                        com.mycompany.model.requestModel.ReceiveChallengeRequestModel challenge) {
                }

                public void onChallengeResponse(
                        com.mycompany.model.responseModel.ReceiveChallengeResponseModel response) {
                }

                public void onMoveReceived(com.mycompany.model.responseModel.MakeMoveResponseModel move) {
                }

                public void onGameEnd(com.mycompany.model.requestModel.EndGameSessionRequestModel endRequest) {
                }

                public void onFailure(String errorMessage) {
                }
            });
        }
    }

    public void disconnect() {
        if (listener != null) {
            listener.stopListener();
            // Actually close the socket
            RemoteServerConnection.getInstance().disconnect();
            listener = null;
        }
        // Force socket close even if listener is null (safety)
        RemoteServerConnection.getInstance().disconnect();
    }

    public List<Player> getFriends(int userId) {
        try {
            RemoteServerConnection.getInstance().connect(serverIp, SERVER_PORT);
            RemoteServerConnection.getInstance().send(new getFriendsRequestModel(userId));
            // Response handled by Listener
        } catch (Exception e) {
            System.err.println("Error loading friends: " + e.getMessage());
            e.printStackTrace();
            RemoteServerConnection.getInstance().disconnect();
        }
        return new java.util.ArrayList<>(); // Empty list, populated async
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
