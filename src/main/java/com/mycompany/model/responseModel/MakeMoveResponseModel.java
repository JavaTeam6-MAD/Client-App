package com.mycompany.model.responseModel;

import java.io.Serializable;

public class MakeMoveResponseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int row;
    private int col;
    private int playerId; // Who made the move
    private String symbol; // "X" or "O"
    private boolean isGameOver;
    private String winner; // "X", "O", "Draw", or null

    public MakeMoveResponseModel(int row, int col, int playerId, String symbol, boolean isGameOver, String winner) {
        this.row = row;
        this.col = col;
        this.playerId = playerId;
        this.symbol = symbol;
        this.isGameOver = isGameOver;
        this.winner = winner;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getPlayerId() {
        return playerId;
    }
    
    public String getSymbol() {
        return symbol;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public String getWinner() {
        return winner;
    }
}