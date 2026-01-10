package com.mycompany.presentation.networkgame;

public class GameContext {
    private static GameContext instance;
    private String gameId;
    private int myId;
    private int opponentId;
    private String mySymbol; // "X" or "O"
    private String opponentName;
    private boolean isMyTurn;
    private long myScore;
    private long opponentScore;

    private long mySessionScore = 0;
    private long opponentSessionScore = 0;

    private String myName;

    private GameContext() {
    }

    public static GameContext getInstance() {
        if (instance == null) {
            instance = new GameContext();
        }
        return instance;
    }

    public void resetSessionScores() {
        mySessionScore = 0;
        opponentSessionScore = 0;
    }

    public void incrementMySessionScore() {
        mySessionScore++;
    }

    public void incrementOpponentSessionScore() {
        opponentSessionScore++;
    }

    public long getMySessionScore() {
        return mySessionScore;
    }

    public long getOpponentSessionScore() {
        return opponentSessionScore;
    }

    public void setGameSession(String gameId, int myId, String myName, int opponentId, String mySymbol,
            String opponentName,
            boolean isMyTurn, long myScore, long opponentScore) {
        this.gameId = gameId;
        this.myId = myId;
        this.myName = myName;
        this.opponentId = opponentId;
        this.mySymbol = mySymbol;
        this.opponentName = opponentName;
        this.isMyTurn = isMyTurn;
        this.myScore = myScore;
        this.opponentScore = opponentScore;
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

    public long getMyScore() {
        return myScore;
    }

    public long getOpponentScore() {
        return opponentScore;
    }

    public void clear() {
        gameId = null;
        opponentName = null;
    }
}
