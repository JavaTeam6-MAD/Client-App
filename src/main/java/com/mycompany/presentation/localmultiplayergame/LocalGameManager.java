/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.presentation.localmultiplayergame;

/**
 *
 * @author TRUE TECH
 */
public class LocalGameManager {
    // Private instance of the state to maintain encapsulation
    private LocalGameState state = new LocalGameState();
    
    public LocalGameManager() {
        // Initialize the board array within the state
        state.setBoard(new char[3][3]);
        resetGame();
        state.setScoreX(0);
        state.setScoreY(0);
    }
    
    public void resetGame() {
        char[][] currentBoard = state.getBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                currentBoard[i][j] = ' '; // Use a space to represent 'empty'
            }
        }
        state.setIsXTurn(true);
        state.setGameActive(true);
    }
    
    public boolean makeMove(int row, int col) {
        boolean madeMove = false;
        char[][] currentBoard = state.getBoard();
        
        if(state.isGameActive() && currentBoard[row][col] == ' ') {
            currentBoard[row][col] = state.isIsXTurn() ? 'X' : 'O';
            madeMove = true;
        }
        return madeMove;
    }
    
    public boolean checkWinner() {
        boolean isWin = false;
        char[][] currentBoard = state.getBoard();
        
        // Rows and Columns
        for (int i = 0; i < 3; i++) {
            if (currentBoard[i][0] != ' ' && currentBoard[i][0] == currentBoard[i][1] && currentBoard[i][1] == currentBoard[i][2])
                isWin = true;
            if (currentBoard[0][i] != ' ' && currentBoard[0][i] == currentBoard[1][i] && currentBoard[0][i] == currentBoard[2][i])
                isWin = true;
        }
        // Diagonals
        if (currentBoard[0][0] != ' ' && currentBoard[0][0] == currentBoard[1][1] && currentBoard[0][0] == currentBoard[2][2])
            isWin = true;
        if (currentBoard[0][2] != ' ' && currentBoard[0][2] == currentBoard[1][1] && currentBoard[0][2] == currentBoard[2][0])
            isWin = true;

        return isWin;
    }
    
    public boolean isBoardFull() {
        boolean isFull = true;
        char[][] currentBoard = state.getBoard();
        
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(currentBoard[i][j] == ' ') {
                    isFull = false;
                    break;
                }
            }
        }
        return isFull;
    }
    
    public void nextTurn() {
        state.setIsXTurn(!state.isIsXTurn());
    }
    
    public char getCurrentPlayer() {
        return state.isIsXTurn() ? 'X' : 'O';
    }
    
    public void incrementScore() {
        if(state.isIsXTurn()) {
            state.setScoreX(state.getScoreX() + 1);
        } else {
            state.setScoreY(state.getScoreY() + 1);
        }
    }
    
    public int getScoreX() {
        return state.getScoreX();
    }
    
    public int getScoreO() {
        return state.getScoreY();
    }
    
    public boolean isGameActive() {
        return state.isGameActive();
    }
    
    public void setGameActive(boolean active) {
        state.setGameActive(active);
    }
    
    // Helper to allow the controller to set player names in the state
    public void setPlayerNames(String xName, String oName) {
        state.setPlayerXName(xName);
        state.setPlayerOName(oName);
    }
}