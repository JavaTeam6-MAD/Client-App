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

public class RemoteDataSource {
    private static String serverIp = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void setServerIp(String ip) {
        serverIp = ip;
    }

    public Player login(String username, String password) {
        return sendPlayerRequest(new LoginRequestModel(username, password));
    }

    public Player register(String username, String password) {
        return sendPlayerRequest(new RegisterRequestModel(username, password));
    }

    public List<Player> getFriends(int userId) {
        try {
            RemoteServerConnection.getInstance().connect(serverIp, SERVER_PORT);
            RemoteServerConnection.getInstance().send(new getFriendsRequestModel(userId));
            // Using receive() which returns Object, then checking type.
            Object response = RemoteServerConnection.getInstance().receive();
            if (response instanceof List) {
                System.out.println("List Player coming");
                return (List<Player>) response;
            }
        } catch (Exception e) {
            System.err.println("Error loading friends: " + e.getMessage());
            e.printStackTrace();
            RemoteServerConnection.getInstance().disconnect();
        }
        return new java.util.ArrayList<>();
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
