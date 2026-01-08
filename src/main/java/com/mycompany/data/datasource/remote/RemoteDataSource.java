package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.LoginRequestModel;
import com.mycompany.model.requestModel.LogoutRequestModel;
import com.mycompany.model.requestModel.RegisterRequestModel;
import com.mycompany.model.requestModel.ChangeNameRequestModel;

public class RemoteDataSource {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public Player login(String username, String password) {
        return sendPlayerRequest(new LoginRequestModel(username, password));
    }

    public Player register(String username, String password) {
        return sendPlayerRequest(new RegisterRequestModel(username, password));
    }

    public Player changeUserName(int id, String newName) {
        return sendPlayerRequest(new ChangeNameRequestModel(id, newName));
    }

    public void logout(int playerId) {
        try {
            RemoteServerConnection.getInstance().connect(SERVER_IP, SERVER_PORT);
            RemoteServerConnection.getInstance().send(new LogoutRequestModel(playerId));
        } catch (Exception e) {
            System.err.println("Logout network error: " + e.getMessage());
        }
    }

    private Player sendPlayerRequest(Object request) {
        try {
            RemoteServerConnection.getInstance().connect(SERVER_IP, SERVER_PORT);
            RemoteServerConnection.getInstance().send(request);
            Object response = RemoteServerConnection.getInstance().receive();

            if (response instanceof Player) {
                return (Player) response;
            }
        } catch (Exception e) {
            System.err.println("Network error: " + e.getMessage());
            e.printStackTrace();
        }
        Player errorPlayer = new Player();
        errorPlayer.setId(0);
        return errorPlayer;
    }
}
