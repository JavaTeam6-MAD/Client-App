package com.mycompany.presentation.networkgame;

public class GameContext {
    private static GameContext instance;
    private String gameId;
    private int myId;
    private int opponentId;
    private String mySymbol; // "X" or "O"
    private String opponentName;
    private boolean isMyTurn;

    private String myName;

    private GameContext() {
    }

    public static GameContext getInstance() {
        if (instance == null) {
            instance = new GameContext();
        }
        return instance;
    }

    public void setGameSession(String gameId, int myId, String myName, int opponentId, String mySymbol,
            String opponentName,
            boolean isMyTurn) {
        this.gameId = gameId;
        this.myId = myId;
        this.myName = myName;
        this.opponentId = opponentId;
        this.mySymbol = mySymbol;
        this.opponentName = opponentName;
        this.isMyTurn = isMyTurn;
    }

    public String getGameId() {
        return gameId;
    }

    public int getMyId() {
        return myId;
    }

    public String getMyName() {
        return myName;
    }

    public int getOpponentId() {
        return opponentId;
    }

    public String getMySymbol() {
        return mySymbol;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public void clear() {
        gameId = null;
        opponentName = null;
    }
}
