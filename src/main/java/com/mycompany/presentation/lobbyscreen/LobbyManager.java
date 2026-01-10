package com.mycompany.presentation.lobbyscreen;

import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.data.datasource.remote.RemoteServerConnection;
import com.mycompany.data.repo_impl.PlayerRepositoryImpl;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;

import com.mycompany.model.requestModel.ReceiveChallengeRequestModel;

import com.mycompany.model.responseModel.ReceiveChallengeResponseModel;
import com.mycompany.model.responseModel.SendChallengeResponseModel;
import com.mycompany.presentation.networkgame.GameSession;

import java.util.List;

public class LobbyManager {
    // No static instance
    private final PlayerRepository playerRepository;
    private LobbyScreenController controller;

    public LobbyManager() {
        this.playerRepository = new PlayerRepositoryImpl();
        // Abstract Repository listener usage?

        RemoteDataSource.getInstance().setLobbyManager(this); // Register self
        RemoteDataSource.getInstance().startListening();
    }

    public void setController(LobbyScreenController controller) {
        this.controller = controller;
    }

    public Player getCurrentPlayer() {
        return playerRepository.getCurrentPlayer();
    }

    public void logout() {
        playerRepository.logout();
    }

    public List<Player> getFriends() {
        return playerRepository.getFriends();
    }

    public void leaveLobby() {
        playerRepository.setPlayerUnavailable();
    }

    public void disconnect() {
        RemoteDataSource.getInstance().disconnect();
    }

    public void stopListening() {
        RemoteDataSource.getInstance().stopListening();
    }

    private boolean isSendingChallenge = false;

    public void sendChallenge(int opponentId) {
        if (isSendingChallenge)
            return;
        isSendingChallenge = true;
        try {
            int myId = getCurrentPlayer().getId();
            com.mycompany.model.requestModel.SendChallengeRequestModel req = new com.mycompany.model.requestModel.SendChallengeRequestModel(
                    myId, opponentId);
            System.out.println("Sending Challenge Request to: " + opponentId);
            RemoteServerConnection.getInstance().send(req);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Reset after short delay or immediately?
            // Should reset when response comes?
            // Or just simple debounce protection (e.g. 1 sec).
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    isSendingChallenge = false;
                }
            }, 1000);
        }
    }

    public void respondToChallenge(boolean accept, int challengerId) {
        try {
            SendChallengeResponseModel resp = new SendChallengeResponseModel(accept, challengerId);
            RemoteServerConnection.getInstance().send(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Network Handling (Called directly by ServerListener) ---

    public void onFriendsListReceived(List<Player> friends) {
        if (controller != null)
            controller.updateFriendsList(friends);
    }

    public void onChallengeReceived(ReceiveChallengeRequestModel challenge) {
        if (controller != null)
            controller.showIncomingChallenge(challenge);
    }

    public void onChallengeResponse(ReceiveChallengeResponseModel response) {
        if (controller != null)
            controller.handleChallengeResponse(response);

        if (response.isAccepted()) {
            // Logic to setup Game Context
            int myId = getCurrentPlayer().getId();
            String myName = getCurrentPlayer().getUserName();
            String challengerName = response.getChallengerName();
            String opponentNameResp = response.getOpponentName();
            boolean amIChallenger = myName.equals(challengerName);
            String mySymbol = amIChallenger ? "X" : "O";
            boolean isMyTurn = amIChallenger;
            String opponentName = amIChallenger ? opponentNameResp : challengerName;
            int opponentId = (myId == response.getSenderPlayerId()) ? response.getReceiverPlayerId()
                    : response.getSenderPlayerId();
            long myScore = amIChallenger ? response.getChallengerScore() : response.getOpponentScore();
            long opponentScore = amIChallenger ? response.getOpponentScore() : response.getChallengerScore();

            GameSession.getInstance().resetSessionScores();
            GameSession.getInstance().setGameSession(
                    response.getGameIdUuid(),
                    myId,
                    myName,
                    opponentId,
                    mySymbol,
                    opponentName,
                    isMyTurn,
                    myScore,
                    opponentScore);

            if (controller != null)
                controller.navigateToGame();
        }
    }

    public void onFailure(String errorMessage) {
        if (controller != null)
            controller.showError(errorMessage);
    }
}
