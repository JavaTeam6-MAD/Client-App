package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.ReceiveChallengeRequestModel;
import com.mycompany.model.requestModel.EndGameSessionRequestModel;
import com.mycompany.model.responseModel.MakeMoveResponseModel;
import com.mycompany.model.responseModel.ReceiveChallengeResponseModel;
import com.mycompany.presentation.gamehistory.GameHistoryManager;
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

    private synchronized void handleMessage(Object msg) {
        RemoteDataSource rds = RemoteDataSource.getInstance();
        NetworkGameManager netMgr = rds.getNetworkGameManager();
        LobbyManager lobbyMgr = rds.getLobbyManager();

        if (msg instanceof List) {
            List<?> list = (List<?>) msg;
            if (!list.isEmpty()) {
                Object first = list.get(0);
                if (first instanceof Player) {
                    // Friends List -> Lobby
                    if (lobbyMgr != null)
                        lobbyMgr.onFriendsListReceived((List<Player>) msg);
                } else if (first instanceof com.mycompany.model.app.Game) {
                    // Game History -> History Manager
                   GameHistoryManager historyMgr = rds.getGameHistoryManager();
                    if (historyMgr != null)
                        historyMgr.onGameHistoryReceived((List<com.mycompany.model.app.Game>) msg);
                }
            } else {
                // Empty list - hard to distinguish, maybe send to both or assume friends?
                // Or better: Server should wrap lists in ResponseModels.
                // For now, if empty, we can just ignore or safely send empty list if we knew
                // the context.
                // But without context, we can't route 100% correctly if connection is shared.
                // However, usually we are in one screen at a time.
                if (lobbyMgr != null)
                    lobbyMgr.onFriendsListReceived(new java.util.ArrayList<>());
                com.mycompany.presentation.gamehistory.GameHistoryManager historyMgr = rds.getGameHistoryManager();
                if (historyMgr != null)
                    historyMgr.onGameHistoryReceived(new java.util.ArrayList<>());
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
