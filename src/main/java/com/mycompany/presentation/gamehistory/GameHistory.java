package com.mycompany.presentation.gamehistory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameHistory {
    private String gameId;
    private LocalDateTime date;
    private String opponentName;
    private String opponentCharacter;
    private boolean playerWon;
    private boolean isDraw;
    private boolean recordedGameAvailable;

    public GameHistory(String gameId, LocalDateTime date, String opponentName,
            String opponentCharacter, boolean playerWon, boolean isDraw,
            boolean recordedGameAvailable) {
        this.gameId = gameId;
        this.date = date;
        this.opponentName = opponentName;
        this.opponentCharacter = opponentCharacter;
        this.playerWon = playerWon;
        this.isDraw = isDraw;
        this.recordedGameAvailable = recordedGameAvailable;
    }

    public String getGameId() {
        return gameId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return date.format(formatter);
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return date.format(formatter);
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getOpponentCharacter() {
        return opponentCharacter;
    }

    public boolean isPlayerWon() {
        return playerWon;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public boolean isRecordedGameAvailable() {
        return recordedGameAvailable;
    }

    public String getResultText() {
        if (isDraw) {
            return "DRAW";
        }
        return playerWon ? "WIN" : "LOSS";
    }

    public String getResultColor() {
        if (isDraw) {
            return "#fbbf24"; // Yellow/Orange
        }
        return playerWon ? "#10b981" : "#ef4444"; // Green for win, Red for loss
    }
}
