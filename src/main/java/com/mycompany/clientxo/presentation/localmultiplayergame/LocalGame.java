/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clientxo.presentation.localmultiplayergame;

import com.mycompany.clientxo.models.app.Game;

/**
 *
 * @author TRUE TECH
 */
public class LocalGame {
    private char[][] board;
    private boolean isXTurn;
    private boolean gameActive;
    private String playerXName;
    private String playerOName;
    private int scoreX;
    private int scoreY;
    
    public LocalGame() {
        board = new char[3][3];
        resetGame();
        scoreX = 0;
        scoreY = 0;
    }
    
    public void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' '; // Use a space to represent 'empty'
            }
        }
        isXTurn = true;
        gameActive = true;
    }
    
    public boolean makeMove(int row, int col) {
        boolean madeMove = false;
        if(gameActive && board[row][col] == ' ') {
            board[row][col] = isXTurn ? 'X' : 'O';
            madeMove = true;
        }
        return madeMove;
    }
    
    public boolean chechWinner() {
        boolean isWin = false;
        // Rows and Columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2])
                isWin = true;
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i])
                isWin = true;
        }
        // Diagonals
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            isWin = true;
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            isWin = true;

        return isWin;
    }
    
    public boolean isBoardFull() {
        boolean isFull = true;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] == ' ') {
                    isFull = false;
                }
            }
        }
        return isFull;
    }
    
    public void nextTurn() {
        isXTurn = !isXTurn;
    }
    
    public char getCurrentPlayer() {
        return isXTurn ? 'X' : 'O';
    }
    
    public void incrementScore() {
        if(isXTurn) {
            scoreX++;
        } else {
            scoreY++;
        }
    }
    
    public int getScoreX() {
        return scoreX;
    }
    
    public int getScoreO() {
        return scoreY;
    }
    
    public boolean isGameActive() {
        return gameActive;
    }
    
    public void setGameActive(boolean active) {
        this.gameActive = active;
    }
}
