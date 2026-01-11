package com.mycompany.model.requestModel;

import java.io.Serializable;

public class MakeMoveRequestModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int row;
    private int col;
    private String gameId; // UUID string
    private int playerId; // Optional, can be inferred from socket
    private String symbol; // "X" or "O"

    public MakeMoveRequestModel(int row, int col, String gameId, String symbol) {
        this.row = row;
        this.col = col;
        this.gameId = gameId;
        this.symbol = symbol;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
