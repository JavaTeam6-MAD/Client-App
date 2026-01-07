package com.mycompany.presentation.singlegamescreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ComputerGameManager {
    private ComputerGameState state = new ComputerGameState();
    private final Random random = new Random();

    public ComputerGameManager() {
        state.setBoard(new char[9]);
        resetGame();
        state.setScoreX(0);
        state.setScoreO(0);
        state.setDifficulty(3); // Default to hard
    }

    public void setDifficulty(int difficulty) {
        state.setDifficulty(difficulty);
    }

    public int getDifficulty() {
        return state.getDifficulty();
    }

    public void resetGame() {
        Arrays.fill(state.getBoard(), ' ');
        state.setGameActive(true);
    }

    public boolean isGameActive() {
        return state.isGameActive();
    }

    public void setGameActive(boolean active) {
        state.setGameActive(active);
    }

    public char[] getBoard() {
        return state.getBoard();
    }

    public int getScoreX() {
        return state.getScoreX();
    }

    public int getScoreO() {
        return state.getScoreO();
    }

    public void incrementScoreX() {
        state.setScoreX(state.getScoreX() + 1);
    }

    public void incrementScoreO() {
        state.setScoreO(state.getScoreO() + 1);
    }

    public boolean mapPlayerMove(int index) {
        char[] board = state.getBoard();
        if (state.isGameActive() && index >= 0 && index < 9 && board[index] == ' ') {
            board[index] = 'X';
            return true;
        }
        return false;
    }

    public int makeComputerMove() {
        if (!state.isGameActive())
            return -1;

        int difficulty = state.getDifficulty();
        int move = -1;

        if (difficulty == 1) {
            move = computerEasy();
        } else if (difficulty == 2) {
            move = computerMedium();
        } else {
            move = computerHard();
        }

        if (move != -1) {
            state.getBoard()[move] = 'O';
        }
        return move;
    }

    private int computerEasy() {
        char[] board = state.getBoard();
        List<Integer> available = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ')
                available.add(i);
        }
        if (!available.isEmpty()) {
            return available.get(random.nextInt(available.size()));
        }
        return -1;
    }

    private int computerMedium() {
        int move = findBestImmediateMove('O');
        if (move == -1)
            move = findBestImmediateMove('X');

        if (move != -1)
            return move;
        else
            return computerEasy();
    }

    private int computerHard() {
        char[] board = state.getBoard();
        if (board[4] == ' ') {
            return 4;
        }

        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                board[i] = 'O';
                int score = minimax(false, 0);
                board[i] = ' ';
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        if (bestMove != -1)
            return bestMove;
        else
            return computerEasy();
    }

    private int minimax(boolean isMax, int depth) {
        if (checkWin('O'))
            return 10 - depth;
        if (checkWin('X'))
            return depth - 10;
        if (isDraw())
            return 0;

        char[] board = state.getBoard();
        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {
                    board[i] = 'O';
                    int score = minimax(false, depth + 1);
                    board[i] = ' ';
                    best = Math.max(best, score);
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {
                    board[i] = 'X';
                    int score = minimax(true, depth + 1);
                    board[i] = ' ';
                    best = Math.min(best, score);
                }
            }
            return best;
        }
    }

    private int findBestImmediateMove(char p) {
        char[] board = state.getBoard();
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                board[i] = p;
                if (checkWin(p)) {
                    board[i] = ' ';
                    return i;
                }
                board[i] = ' ';
            }
        }
        return -1;
    }

    public boolean checkWin(char p) {
        char[] board = state.getBoard();
        int[][] wins = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 },
                { 2, 4, 6 } };
        for (int[] w : wins)
            if (board[w[0]] == p && board[w[1]] == p && board[w[2]] == p)
                return true;
        return false;
    }

    public boolean isDraw() {
        char[] board = state.getBoard();
        for (char c : board)
            if (c == ' ')
                return false;
        return true;
    }
}
