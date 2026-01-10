package com.mycompany.presentation.networkgame;

import com.mycompany.core.utils.SoundManager;
import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.data.datasource.remote.RemoteServerConnection;
import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.EndGameSessionRequestModel;
import com.mycompany.model.requestModel.MakeMoveRequestModel;
import com.mycompany.model.requestModel.ReceiveChallengeRequestModel;
import com.mycompany.model.requestModel.SendChallengeRequestModel;
import com.mycompany.model.responseModel.MakeMoveResponseModel;
import com.mycompany.model.responseModel.ReceiveChallengeResponseModel;
import com.mycompany.model.responseModel.SendChallengeResponseModel;
import com.mycompany.model.utils.GameStatus;

import java.util.List;

public class NetworkGameManager {

    // No Static Instance
    private NetworkGameController controller;
    private RemoteDataSource remoteDataSource;

    private GameSession session;
    private SoundManager soundManager;

    public NetworkGameManager() {
        remoteDataSource = RemoteDataSource.getInstance();
        session = GameSession.getInstance();
        soundManager = SoundManager.getInstance();

        remoteDataSource.setNetworkGameManager(this); // Register self
    }

    public void setController(NetworkGameController controller) {
        this.controller = controller;
        if (controller != null) {
            remoteDataSource.startListening();
        }
    }

    public void stopGame() {
        remoteDataSource.detachNetworkGameManager();
    }

    // --- Business Logic Methods called by Controller ---

    public void makeMove(int row, int col) {
        if (!session.isMyTurn())
            return;

        session.setMyTurn(false);
        if (controller != null) {
            controller.updateBoard(row, col, session.getMySymbol());
            controller.updateTurnStatus(false, session.getMySymbol());
        }

        try {
            MakeMoveRequestModel req = new MakeMoveRequestModel(row, col, session.getGameId(), session.getMySymbol());
            RemoteServerConnection.getInstance().send(req);
        } catch (Exception e) {
            e.printStackTrace();
            if (controller != null)
                controller.showConnectionError("Failed to send move.");
        }
    }

    // Extracted Sound Logic
    public void playSound(boolean isWin) {
        // Logic: if isWin is true/false, maybe this is for GAME END?
        // If called from View inside makeMove?
        // In Controller, playSound is used for turn sound AND game end sound.
        // Let's expose specific methods to clear confusion.
        if (soundManager != null) {
            if (isWin)
                soundManager.playSound(SoundManager.WIN);
            else
                soundManager.playSound(SoundManager.PLACE_O); // Logic?
        }
    }

    public void playPlaceSound() {
        if (soundManager != null)
            soundManager.playSound(SoundManager.PLACE_O);
    }

    public void playWinSound() {
        if (soundManager != null)
            soundManager.playSound(SoundManager.WIN);
    }

    public void playLoseSound() {
        if (soundManager != null)
            soundManager.playSound(SoundManager.LOSE);
    }

    public void forfeitGame() {
        try {
            EndGameSessionRequestModel req = new EndGameSessionRequestModel(
                    session.getMyId(), session.getOpponentId(), GameStatus.LOSE);
            RemoteServerConnection.getInstance().send(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopGame();
        if (controller != null)
            controller.navigateHome();
    }

    public void requestRematch() {
        try {
            SendChallengeRequestModel req = new SendChallengeRequestModel(session.getMyId(), session.getOpponentId());
            RemoteServerConnection.getInstance().send(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRematchResponse(boolean accept, int challengerId) {
        try {
            SendChallengeResponseModel resp = new SendChallengeResponseModel(accept, challengerId);
            RemoteServerConnection.getInstance().send(resp);

            if (!accept) {
                stopGame();
                if (controller != null)
                    controller.navigateHome();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Network Handling (Called directly by ServerListener) ---

    public void onMoveReceived(MakeMoveResponseModel move) {
        int r = move.getRow();
        int c = move.getCol();
        boolean isMe = (move.getPlayerId() == session.getMyId());
        String symbol = isMe ? session.getMySymbol() : (session.getMySymbol().equals("X") ? "O" : "X");

        if (controller != null) {
            controller.updateBoard(r, c, symbol);
        }

        if (!isMe) {
            session.setMyTurn(true);
            if (controller != null)
                controller.updateTurnStatus(true, session.getMySymbol());
            // Play sound?
            playPlaceSound();
        } else {
            session.setMyTurn(false);
            if (controller != null)
                controller.updateTurnStatus(false, session.getMySymbol());
        }

        if (move.isGameOver()) {
            handleGameOver(move.getWinner());
        }
    }

    private void handleGameOver(String winnerSymbol) {
        String msg;
        boolean isWin = false;
        if (winnerSymbol == null || winnerSymbol.isEmpty()) {
            msg = "It's a Draw!";
            session.incrementMySessionScore();
            session.incrementOpponentSessionScore();
            playLoseSound(); // Draw sound?
        } else if (winnerSymbol.equals(session.getMySymbol())) {
            msg = "You Won! 🎉";
            isWin = true;
            session.incrementMySessionScore();
            playWinSound();
        } else {
            msg = "You Lost! 😔";
            session.incrementOpponentSessionScore();
            playLoseSound();
        }

        if (controller != null) {
            controller.showGameEnd(msg, isWin);
            controller.updateScoreLabels(session.getMySessionScore(), session.getOpponentSessionScore());
        }
    }

    public void onGameEnd(EndGameSessionRequestModel endRequest) {
        // Opponent Forfeit
        session.incrementMySessionScore();
        playWinSound();
        if (controller != null) {
            controller.showOpponentForfeit();
        }
    }

    public void onChallengeReceived(ReceiveChallengeRequestModel challenge) {
        if (controller != null) {
            controller.showRematchRequest(challenge.getSenderName(), challenge.getPlayer1Id());
        }
    }

    public void onChallengeResponse(ReceiveChallengeResponseModel response) {
        if (response.isAccepted()) {
            resetGameInternal(response);
            if (controller != null) {
                controller.resetGameUI();
                controller.updateScoreLabels(session.getMyScore(), session.getOpponentScore());
                controller.updateTurnStatus(session.isMyTurn(), session.getMySymbol());
            }
        } else {
            if (controller != null) {
                controller.showRematchRejected();
            }
        }
    }

    private void resetGameInternal(ReceiveChallengeResponseModel response) {
        String myName = session.getMyName();
        String challengerName = response.getChallengerName();
        String opponentNameResp = response.getOpponentName();
        boolean amIChallenger = myName.equals(challengerName);
        String mySymbol = amIChallenger ? "X" : "O";
        boolean isMyTurn = amIChallenger;
        String opponentName = amIChallenger ? opponentNameResp : challengerName;
        int myId = session.getMyId();
        int opponentId = (myId == response.getSenderPlayerId()) ? response.getReceiverPlayerId()
                : response.getSenderPlayerId();
        long myScore = amIChallenger ? response.getChallengerScore() : response.getOpponentScore();
        long opponentScore = amIChallenger ? response.getOpponentScore() : response.getChallengerScore();

        session.setGameSession(
                response.getGameIdUuid(),
                myId,
                myName,
                opponentId,
                mySymbol,
                opponentName,
                isMyTurn,
                myScore,
                opponentScore);
    }

    public void onFriendsListReceived(List<Player> friends) {
    }

    public void onFailure(String errorMessage) {
        if (controller != null)
            controller.showConnectionError(errorMessage);
    }

    // Getters for Init
    public String getMySymbol() {
        return session.getMySymbol();
    }

    public String getOpponentName() {
        return session.getOpponentName();
    }

    public long getMySessionScore() {
        return session.getMySessionScore();
    }

    public long getOpponentSessionScore() {
        return session.getOpponentSessionScore();
    }

    public boolean isMyTurn() {
        return session.isMyTurn();
    }
}
