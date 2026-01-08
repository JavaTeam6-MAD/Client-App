package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.LoginRequestModel;
import com.mycompany.model.requestModel.RegisterRequestModel;
import com.mycompany.model.requestModel.getFriendsRequestModel;

import java.util.List;

public class RemoteDataSource {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public Player login(String username, String password) {
        return sendRequest(new LoginRequestModel(username, password));
    }

    public Player register(String username, String password) {
        return sendRequest(new RegisterRequestModel(username, password));
    }
  public List<Player> getFriends(int userId) {
      try {
          RemoteServerConnection.getInstance().connect(SERVER_IP, SERVER_PORT);
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
      }
      return new java.util.ArrayList<>();
  }
    private Player sendRequest(Object request) {
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
        // Return a default error player if connection fails
        Player errorPlayer = new Player();
        errorPlayer.setId(0); // ID 0 signifies failure
        return errorPlayer;
    }
}
