package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.ReceiveChallengeRequestModel;
import com.mycompany.model.requestModel.EndGameSessionRequestModel;
import com.mycompany.model.responseModel.MakeMoveResponseModel;
import com.mycompany.model.responseModel.ReceiveChallengeResponseModel;
import com.mycompany.presentation.lobbyscreen.LobbyManager;
import com.mycompany.presentation.networkgame.NetworkGameManager;

import java.io.ObjectInputStream;
import java.util.List;

public class ServerListener extends Thread {
    private ObjectInputStream in;
    private boolean running = true;

    // Direct dependencies via RemoteDataSource, no callback passing
    public ServerListener(ObjectInputStream in) {
        this.in = in;
    }

    public void stopListener() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running && !isInterrupted()) {
                Object msg = in.readObject();
                handleMessage(msg);
            }
        } catch (Exception e) {
            System.out.println("ServerListener stopped or connection lost: " + e.getMessage());
            if (running) {
                // Notify both managers of failure possibility
                NetworkGameManager netManager = RemoteDataSource.getInstance().getNetworkGameManager();
                if (netManager != null)
                    netManager.onFailure("Connection lost");

                LobbyManager lobbyManager = RemoteDataSource.getInstance().getLobbyManager();
                if (lobbyManager != null)
                    lobbyManager.onFailure("Connection lost");
            }
        }
    }

    private void handleMessage(Object msg) {
        RemoteDataSource rds = RemoteDataSource.getInstance();
        NetworkGameManager netMgr = rds.getNetworkGameManager();
        LobbyManager lobbyMgr = rds.getLobbyManager();

        if (msg instanceof List) {
            try {
                // Friends List -> Lobby
                List<Player> friends = (List<Player>) msg;
                if (lobbyMgr != null)
                    lobbyMgr.onFriendsListReceived(friends);
            } catch (ClassCastException e) {
            }
        } else if (msg instanceof ReceiveChallengeRequestModel) {
            // Challenge Received -> Lobby OR Game?
            if (netMgr != null)
                netMgr.onChallengeReceived((ReceiveChallengeRequestModel) msg);
            if (lobbyMgr != null)
                lobbyMgr.onChallengeReceived((ReceiveChallengeRequestModel) msg);

        } else if (msg instanceof ReceiveChallengeResponseModel) {
            // Challenge Response -> Game Start or Rejection
            if (netMgr != null)
                netMgr.onChallengeResponse((ReceiveChallengeResponseModel) msg);
            if (lobbyMgr != null)
                lobbyMgr.onChallengeResponse((ReceiveChallengeResponseModel) msg);

        } else if (msg instanceof MakeMoveResponseModel) {
            // Move -> Game
            if (netMgr != null)
                netMgr.onMoveReceived((MakeMoveResponseModel) msg);

        } else if (msg instanceof EndGameSessionRequestModel) {
            // Game End (Forfeit) -> Game
            if (netMgr != null)
                netMgr.onGameEnd((EndGameSessionRequestModel) msg);

        } else {
            System.out.println("Unknown message received: " + msg.getClass().getSimpleName());
        }
    }
}
